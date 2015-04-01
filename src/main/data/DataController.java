package main.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.utils.ConfigElementGroup;

public class DataController {
	
	private Map<Integer, DataRecordHeader> headers = new HashMap<Integer, DataRecordHeader>();
	private Map<Short, String> classList = new HashMap<Short, String>();
	
	private String indexFile;
	private String dataFile;
	private String classFile;
	private int fileLength;
	private short nextClassIndex;
	
	public DataController(ConfigElementGroup config) {
		String path = config.getString("path", "./");
		File file = new File(path);
		if(file.exists() == false)
			file.mkdirs();
		indexFile = path + "index.mcif";
		dataFile = path + "data.mcdf";
		classFile = path + "classes.mccf";	
		readClassList();
		readIndex();
	}

//	private void writeHeader() {
//		try {
//			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(indexFile));
//			for(DataRecordHeader header : headers)
//				stream.write(header.serialize().getArray());						
//			stream.flush();
//			stream.close();			
//    } catch(IOException e) {
//       System.err.println("ERROR: " + e.getMessage());       
//    }
//	}
	
	private void readClassList() {
		classList.clear();
		nextClassIndex = 0;
		
		try {
		  new File(classFile).createNewFile();
		} catch(IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(classFile));
	    while(true) {
	    	String line = reader.readLine();
        if(line == null)
        	break;
        
        int index = line.indexOf(" ");
        if(index < 0) {
        	System.err.println("ERROR: invalid class file entry: " + line);
        	continue;
        }
        
        short id = Short.parseShort(line.substring(0, index));
        String className = line.substring(index+1);
        classList.put(id, className);
        if(id >= nextClassIndex)
        	nextClassIndex = (short)(id + 1);
	    }
			
			reader.close();			
    } catch(IOException e) {
       System.err.println("ERROR: " + e.getMessage());       
    }
	}
	
	private void readIndex() {		
		headers.clear();	
		fileLength = 0;
		
		try {
		  new File(indexFile).createNewFile();
		} catch(IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(indexFile));
			DataBuffer buffer = new DataBuffer(DataRecordHeader.getByteCount());
			while(true) {				
			  int status = stream.read(buffer.getArray());
			  if(status < 0)
			  	break;
			  DataRecordHeader header = new DataRecordHeader(buffer);
			  headers.put(header.getDataId(), header);
			  int dataEnd = header.getDataPointer() + header.getDataLength();
			  if(dataEnd > fileLength)
			  	fileLength = dataEnd;
			}			
			stream.close();			
    } catch(IOException e) {
       System.err.println("ERROR: " + e.getMessage());       
    }
	}
	
	private short getClassId(String className) {		
		for(Short id : classList.keySet())
			if(classList.get(id).equals(className))
				return id;		
		
		short id = nextClassIndex++;		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(classFile, true));			
			writer.write(String.format("%05d %s\n", id, className));						
			writer.flush();
			writer.close();					
    } catch(IOException e) {
       System.err.println("ERROR: " + e.getMessage()); 
       return -1;
    }
		
		return id;
	}
	
	public int add(DataObject object) {
		int id = headers.size();
		int objectSize = object.getEntrySize();
		short classId = getClassId(object.getClass().getName());
		DataRecordHeader header = new DataRecordHeader(id, fileLength, objectSize, classId);
		headers.put(id, header);
		fileLength += objectSize;
		
		try {
			BufferedOutputStream dataStream = new BufferedOutputStream(new FileOutputStream(dataFile, true));
			dataStream.write(object.serialize().getArray());						
			dataStream.flush();
			dataStream.close();		
			
			BufferedOutputStream indexStream = new BufferedOutputStream(new FileOutputStream(indexFile, true));			
			indexStream.write(header.serialize().getArray());						
			indexStream.flush();
			indexStream.close();	
    } catch(IOException e) {
       System.err.println("ERROR: " + e.getMessage()); 
       return -1;
    }
		return id;
	}
	
	private DataBuffer getBuffer(DataRecordHeader header) {	
		try {
			int dataLength = header.getDataLength();			
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(dataFile));
			DataBuffer buffer = new DataBuffer(dataLength);
			stream.skip(header.getDataPointer());
			int status = stream.read(buffer.getArray());
			stream.close();			
			if(status < dataLength)
				throw new RuntimeException("failed to read data entry with ID " + header.getDataId());
			return buffer;
		} catch(IOException e) {
			System.err.println("ERROR: " + e.getMessage());  
			return null;
		}
	}
	
	private DataObject convertBufferToObject(DataRecordHeader header, DataBuffer buffer) {
		String className = classList.get(header.getClassId());
		if(className == null)
			throw new RuntimeException("could not find class with ID " + header.getClassId());			
		
		try {
			Class<?> myClass = Class.forName(className);
			DataObject dataObject = (DataObject)myClass.getConstructor(DataBuffer.class).newInstance(buffer);
			header.setBody(dataObject);
			return dataObject;
		} catch(Exception e) {
			System.err.println("ERROR: " + e.getMessage());
			return null;
		} 
	}
	
	public DataObject get(int id) {
		DataRecordHeader header = headers.get(id);
		if(header == null)
			throw new RuntimeException("entry with ID " + id + " not found!");
		
		if(header.hasBody())
			return header.getBody();
		
		DataBuffer buffer = getBuffer(header);
		if(buffer == null)
			return null;
		
		return convertBufferToObject(header, buffer);       
	}
	
	public List<DataObject> select(DataQuery query) {		
		Short selectedClassId = getClassId(query.getQueryClassName());
		if(selectedClassId < 0)
			throw new RuntimeException("selected class " + query.getQueryClassName() + " not found");
		
		List<DataObject> dataObjects = new LinkedList<DataObject>();
		for(DataRecordHeader header : headers.values()) {	
			if(selectedClassId != header.getClassId())
				continue;
			
			DataObject dataObject;
			if(header.hasBody())
				dataObject = header.getBody();
			else
				dataObject = convertBufferToObject(header, getBuffer(header));
			
			if(dataObject.match(query))				
				dataObjects.add(dataObject);			
		}
		
		return dataObjects;
	}
}
