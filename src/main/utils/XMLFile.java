package main.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class XMLFile {

	public static interface Element {
		public void write(XMLStreamWriter writer, int intend) throws XMLStreamException;		
	}
	
	public static class ElementList implements Element {
		private HashMap<String, Element> list = new HashMap<String, Element>();
		
		public void add(String name, Element value) {			
			if(name.contains(".")) {
				int index = name.indexOf(".");				
				String firstName = name.substring(0, index);
				String restName = name.substring(index+1);
				
				Element entry = list.get(firstName); 
				if(entry == null) {
					ElementList newEntry = new ElementList();
					newEntry.add(restName, value);
					list.put(firstName, newEntry);
				}
				else if(entry instanceof ElementList)
					((ElementList)entry).add(restName, value);				
				else 
					throw new RuntimeException("ERROR: \""+firstName+"\" is not a list!");				
			}
			else {
				Element element = get(name);	
				if(element == null) {		
					list.put(name, value);
				}
				else if(element instanceof ElementList) {
					if(value instanceof ElementList)
						((ElementList)element).merge((ElementList) value);
					else					
						throw new RuntimeException("ERROR: \""+name+"\" is not a list!");
				}					
				else
					throw new RuntimeException("ERROR: \""+name+"\" is not a list!");
			}
		}
		
		public void merge(ElementList list) {
			for(Entry<String, Element> element : list.list.entrySet())
				this.list.put(element.getKey(), element.getValue());			
		}
		
		public int size() {
			return list.size();
		}
		
		public void clear() {
			list.clear();
		}
		
		public Element get(String name) {
			if(name.contains(".")) {
				int index = name.indexOf(".");
				
				String firstName = name.substring(0, index);
				String restName = name.substring(index+1);
				
				Element entry = list.get(firstName);
				if(entry != null && entry instanceof ElementList){						
					return ((ElementList)entry).get(restName);
				}
				return null;				
			}
			else
				return list.get(name);			
		}
		
	  public int getInt(String name, int defaultValue) {		
			try {
				return Integer.parseInt(get(name).toString());
			} catch(Exception e) {
				return defaultValue;
			}			
		}
		
		public String getString(String name, String defaultValue) {		
			try {			
				return get(name).toString();
			} catch(Exception e) {
				return defaultValue;
			}
		}
		
		public ElementList getElement(String name) {
			Element result = get(name);
			if(result == null)
				return new ElementList();
			return (ElementList)result;
		}
		
		@Override
		public void write(XMLStreamWriter writer, int intend) throws XMLStreamException {
			writer.writeCharacters("\n");
			for(String entryName : list.keySet()) {										
				for(int i=0; i<intend; i++)
						writer.writeCharacters(" ");
				
				writer.writeStartElement(entryName);
				Element entry = list.get(entryName);
				entry.write(writer, intend+2);
				
				if(entry instanceof ElementList)
					for(int i=0; i<intend; i++)
						writer.writeCharacters(" ");
					
				writer.writeEndElement();				
				writer.writeCharacters("\n");
			}
		}
		
		@Override
		public String toString() {
			return toString("");
		}
		
		public String toString(String prefix) {			
			String s = "";
			int counter = 0;			
			for(Entry<String, Element> element : list.entrySet()) {
				if(counter == 0) {
					s += element.getKey();					
				}
				else {
					s += prefix + element.getKey();
				}
				counter++;
				
				Element e = element.getValue();
				if(e instanceof ElementList) {
					s += "."+((ElementList)e).toString(prefix+element.getKey()+".");
				}
				else {
					s += " = " + e.toString();					
				}				
				
				if(counter != list.size())
					s += "\n";
			}
			return s;
		}
	}
	
	public static class ElementValue<Type> implements Element {				
		private Type value;
		
		public ElementValue(Type value) {				
			this.value = value;
		}
		
		public Type getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			if(value == null)
				return "null";
			return value.toString();
		}

		@Override
    public void write(XMLStreamWriter writer, int intend) throws XMLStreamException {						
			writer.writeCharacters(toString());
    }		
	}
	
	
	
	private ElementList list = new ElementList();
	private String fileName;
	
	public XMLFile(String fileName) {
		this.fileName = fileName;		
	}
	
	public void add(String name, Element value) {
		list.add(name, value);
	}
	
	public void add(String name, int value) {
		list.add(name, new ElementValue<Integer>(value));		
	}
	
	public void add(String name, String value) {
		list.add(name, new ElementValue<String>(value));		
	}
	
  public int getInt(String name, int defaultValue) {		
  	return list.getInt(name, defaultValue);
	}
	
	public String getString(String name, String defaultValue) {		
		return list.getString(name, defaultValue);
	}
	
	public ElementList getElement(String name) {
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
	
	private void parse(XMLStreamReader reader, ElementList list, String elementName) {	
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
	      			ElementList sublist = new ElementList();
	      			parse(reader, sublist, reader.getLocalName());
	      			list.add(name, sublist);
	      		}
	      		else if(reader.getEventType() == XMLStreamReader.CHARACTERS) {
	      			String text = reader.getText().trim();
	      			if(text.length() == 0)  //newline
	      				continue;	      		
	      			list.add(name, new ElementValue<String>(text));
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
	
	@Override
	public String toString() {
		return list.toString();
	}
}
