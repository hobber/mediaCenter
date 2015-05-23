package main.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class ConfigFile {
  
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

	public static interface ConfigElementBase {
		public void write(XMLStreamWriter writer, int intend) throws XMLStreamException;		
	}
	
	private ConfigElementGroup list = new ConfigElementGroup();
	private String fileName;
	
	public ConfigFile(String fileName) {
		this.fileName = fileName;		
	}
	
	public void add(String name, ConfigElementBase value) {
		list.add(name, value);
	}
	
	public void add(String name, int value) {
		list.add(name, new ConfigElement<Integer>(value));		
	}
	
	public void add(String name, String value) {
		list.add(name, new ConfigElement<String>(value));		
	}
	
  public int getInt(String name, int defaultValue) {		
  	return list.getInt(name, defaultValue);
	}
	
	public String getString(String name, String defaultValue) {		
		return list.getString(name, defaultValue);
	}
	
	public ConfigElementGroup getElement(String name) {
		return list.getElement(name);
	}
	
	public boolean read() {
		File file = new File(fileName);
		if(file.exists() == false) {
			System.err.println("ERROR: file \""+fileName+"\" not found!");
			return false;
		}
		
		FileReader fileReader = null;		
		try {			
			fileReader = new FileReader(fileName);	
		} catch(Exception e) {			
			System.err.println("ERROR: could not open file!");
			return false;
		}
		
		XMLStreamReader reader;	
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
	    reader = factory.createXMLStreamReader(fileReader);
    } catch (Exception e) {     	    	
    	System.err.println("ERROR: could not parse file!");
    	return false;
    }
		
		list.clear();
		try {			 
			parse(reader, list, "");
			reader.close();
		} catch(Exception e) {
			System.err.println("ERROR: could not read file!");
			return false;
		}
		
		return true;
	}
	
	private void parse(XMLStreamReader reader, ConfigElementGroup list, String elementName) {	
		try {
	    while(reader.hasNext()) {	
	    	if(elementName.length() == 0)
	    		reader.next();
	      if(elementName.length() > 0 || reader.getEventType() == XMLStreamReader.START_ELEMENT) {
	      	String name = elementName;
	      	if(name.length() == 0)	      		
	      		name = reader.getLocalName();
	      	elementName = "";
	        	        
	        while(reader.hasNext()){	    	
	        	reader.next();
	      		if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {	      			
	      			ConfigElementGroup sublist = new ConfigElementGroup();
	      			parse(reader, sublist, reader.getLocalName());
	      			list.add(name, sublist);
	      		}
	      		else if(reader.getEventType() == XMLStreamReader.CHARACTERS) {
	      			String text = reader.getText().trim();
	      			if(text.length() == 0)  //newline
	      				continue;	      		
	      			list.add(name, new ConfigElement<String>(text));
	      		}
	      		else if(reader.getEventType() == XMLStreamReader.END_ELEMENT)
	      			return;	      		
	      	}
	      }	      
	    }
    } catch (Exception e) {
    	throw new RuntimeException("ERROR: could not read config file!", e);
    }		
	}
	
	public void write() {
		XMLStreamWriter writer;
		try {
			XMLOutputFactory output = XMLOutputFactory.newInstance();			
			FileWriter fileWriter = new FileWriter(fileName);			
			writer = output.createXMLStreamWriter(fileWriter);			
		} catch(Exception e) {
			throw new RuntimeException("ERROR: could not open file!", e);			
		}
		
		try {
			writer.writeStartDocument();
			list.write(writer, 0);
			writer.writeEndDocument();
			writer.flush();
		} catch(Exception e) {
			throw new RuntimeException("ERROR: could not write file!", e);
		}
	}
	
	public void clear() {
		list.clear();
	}
	
	public static String dateToString(Calendar date) {
	  return DATE_FORMAT.format(date.getTime());
	}
	
	@Override
	public String toString() {
		return list.toString();
	}
}
