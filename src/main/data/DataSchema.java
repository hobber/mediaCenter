package main.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSchema {
	
	private class DataSchemaByte extends DataSchemaObject<Byte> {					
		public DataSchemaByte(short index, String name) {			
			super(index, name);
		}		
		public short getNumberOfHeaderBytes() {
			return 1;
		}
		public short getObjectTotalSize() {
			return 1;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getByte(index);
		}
		public short writeValue(DataBuffer buffer, short offset) {
			buffer.putByte(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Byte.parseByte(mask) == value;
		}
	}
	
	private class DataSchemaShort extends DataSchemaObject<Short> {					
		public DataSchemaShort(short index, String name) {			
			super(index, name);
		}
		public short getNumberOfHeaderBytes() {
			return 2;
		}

		public short getObjectTotalSize() {
			return 1;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getShort(index);
		}
		public short writeValue(DataBuffer buffer, short offset) {
			buffer.putShort(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Short.parseShort(mask) == value;
		}
	}
	
	private class DataSchemaInt extends DataSchemaObject<Integer> {					
		public DataSchemaInt(short index, String name) {			
			super(index, name);
		}
		public short getNumberOfHeaderBytes() {
			return 4;
		}
		public short getObjectTotalSize() {
			return 4;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getInt(index);
		}
		public short writeValue(DataBuffer buffer, short offset) {
			buffer.putInt(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Integer.parseInt(mask) == value;
		}
	}
	
	private class DataSchemaString extends DataSchemaObject<String> {					
		public DataSchemaString(short index, String name) {			
			super(index, name);
		}
		public short getNumberOfHeaderBytes() {
			return 2;
		}
		public short getObjectTotalSize() {
			return (short)(4 + value.length());
		}
		public void readValue(DataBuffer buffer) {
			short offset = buffer.getShort(index);
			short length = buffer.getShort(offset);
			value = buffer.getString(offset+2, length);
		}
		public short writeValue(DataBuffer buffer, short offset) {
			buffer.putShort(index, offset);
			buffer.putShort(offset, (short)value.length());
			buffer.putString(offset+2, value);
			return (short)(value.length() + 2);
		}
		public boolean match(String mask) {
			return mask.equals(value);
		}
	}
	
	private String className;
	private Map<String, DataSchemaObject<?>> schemas = new HashMap<String, DataSchemaObject<?>>();
	private short nextIndex = 0;
	
	public DataSchema(String className) {
		this.className = className;
	}
	
	public DataSchemaObject<String> getSchemaString(String name) {
		DataSchemaObject<String> object = new DataSchemaString(nextIndex, name);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(name,  object);
		return object;
	}
	
	public void readValues(DataBuffer buffer) {
		for(DataSchemaObject<?> schema : schemas.values())
			schema.readValue(buffer);			
	}
	
	public DataBuffer writeValues() {		
		DataBuffer buffer = new DataBuffer(getBufferSize());
		short offset = nextIndex;
		for(DataSchemaObject<?> schema : schemas.values())
			offset += schema.writeValue(buffer, offset);
		return buffer;
	}
	
	public short getBufferSize() {
		short size = 0;
		for(DataSchemaObject<?> schema : schemas.values())
			size += schema.getObjectTotalSize();
		return size;
	}
	
	public DataSelector getSelector(List<String> fieldNames) {
		DataSelector selector = new DataSelector(className);
		for(String fieldName : fieldNames) {
			DataSchemaObject<?> schemaObject = schemas.get(fieldName);
			if(schemaObject == null) {
				System.out.println("ERROR: " + className + " has no field " + fieldName);
				continue;
			}
			selector.addSchemaObject(schemaObject);
		}
		return selector;
	}
}
