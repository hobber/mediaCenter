package main.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import main.utils.ConfigFile.ConfigElementBase;

public class ConfigElementGroup implements ConfigElementBase {
	private HashMap<String, ConfigElementBase> list = new HashMap<String, ConfigElementBase>();
	
	public void add(String name, ConfigElementBase value) {			
		if(name.contains(".")) {
			int index = name.indexOf(".");				
			String firstName = name.substring(0, index);
			String restName = name.substring(index+1);
			
			ConfigElementBase entry = list.get(firstName); 
			if(entry == null) {
				ConfigElementGroup newEntry = new ConfigElementGroup();
				newEntry.add(restName, value);
				list.put(firstName, newEntry);
			}
			else if(entry instanceof ConfigElementGroup)
				((ConfigElementGroup)entry).add(restName, value);				
			else 
				throw new RuntimeException("ERROR: \""+firstName+"\" is not a list!");				
		}
		else {
			ConfigElementBase element = get(name);	
			if(element == null) {		
				list.put(name, value);
			}
			else if(element instanceof ConfigElementGroup) {
				if(value instanceof ConfigElementGroup)
					((ConfigElementGroup)element).merge((ConfigElementGroup) value);
				else					
					throw new RuntimeException("ERROR: \""+name+"\" is not a list!");
			}					
			else
				throw new RuntimeException("ERROR: \""+name+"\" is not a list!");
		}
	}
	
	public void merge(ConfigElementGroup list) {
		for(Entry<String, ConfigElementBase> element : list.list.entrySet())
			this.list.put(element.getKey(), element.getValue());			
	}
	
	public int size() {
		return list.size();
	}
	
	public void clear() {
		list.clear();
	}
	
	public ConfigElementBase get(String name) {
		if(name.contains(".")) {
			int index = name.indexOf(".");
			
			String firstName = name.substring(0, index);
			String restName = name.substring(index+1);
			
			ConfigElementBase entry = list.get(firstName);
			if(entry != null && entry instanceof ConfigElementGroup){						
				return ((ConfigElementGroup)entry).get(restName);
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
	
	public ConfigElementGroup getElement(String name) {
		ConfigElementBase result = get(name);
		if(result == null)
			return new ConfigElementGroup();
		return (ConfigElementGroup)result;
	}
	
	@Override
	public void write(XMLStreamWriter writer, int intend) throws XMLStreamException {
		writer.writeCharacters("\n");
		for(String entryName : list.keySet()) {										
			for(int i=0; i<intend; i++)
					writer.writeCharacters(" ");
			
			writer.writeStartElement(entryName);
			ConfigElementBase entry = list.get(entryName);
			entry.write(writer, intend+2);
			
			if(entry instanceof ConfigElementGroup)
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
		for(Entry<String, ConfigElementBase> element : list.entrySet()) {
			if(counter == 0) {
				s += element.getKey();					
			}
			else {
				s += prefix + element.getKey();
			}
			counter++;
			
			ConfigElementBase e = element.getValue();
			if(e instanceof ConfigElementGroup) {
				s += "."+((ConfigElementGroup)e).toString(prefix+element.getKey()+".");
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
