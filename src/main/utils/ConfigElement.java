package main.utils;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import main.utils.ConfigFile.ConfigElementBase;

public class ConfigElement<Type> implements ConfigElementBase {				
	private Type value;
	
	public ConfigElement(Type value) {				
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
