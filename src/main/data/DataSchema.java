package main.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.data.DataQuery.Query;

public class DataSchema {
	
	private class DataSchemaByte extends DataSchemaObject<Byte> {					
		public DataSchemaByte(int index, String name) {			
			super(index, name);
		}		
		public int getNumberOfHeaderBytes() {
			return 1;
		}
		public int getObjectTotalSize() {
			return 1;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getByte(index);
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putByte(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Byte.parseByte(mask) == value;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	private class DataSchemaShort extends DataSchemaObject<Short> {					
		public DataSchemaShort(int index, String name) {			
			super(index, name);
		}
		public int getNumberOfHeaderBytes() {
			return 2;
		}

		public int getObjectTotalSize() {
			return 2;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getShort(index);
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putShort(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Short.parseShort(mask) == value;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	private class DataSchemaInt extends DataSchemaObject<Integer> {					
		public DataSchemaInt(int index, String name) {			
			super(index, name);
		}
		public int getNumberOfHeaderBytes() {
			return 4;
		}
		public int getObjectTotalSize() {
			return 4;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getInt(index);
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putInt(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Integer.parseInt(mask) == value;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	private class DataSchemaFloat extends DataSchemaObject<Float> {					
		public DataSchemaFloat(int index, String name) {			
			super(index, name);
		}
		public int getNumberOfHeaderBytes() {
			return 4;
		}
		public int getObjectTotalSize() {
			return 4;
		}
		public void readValue(DataBuffer buffer) {
			value = buffer.getFloat(index);
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putFloat(index, value);
			return 0;
		}
		public boolean match(String mask) {
			return Float.parseFloat(mask) == value;
		}
		public String toString() {
			return value.toString();
		}
	}
	
	private class DataSchemaString extends DataSchemaObject<String> {					
		public DataSchemaString(int index, String name) {			
			super(index, name);
		}
		public int getNumberOfHeaderBytes() {
			return 4;
		}
		public int getObjectTotalSize() {
			return 8 + value.length();
		}
		public void readValue(DataBuffer buffer) {
			int offset = buffer.getInt(index);
			int length = buffer.getInt(offset);
			value = buffer.getString(offset + 4, length);
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putInt(index, offset);
			buffer.putInt(offset, value.length());
			buffer.putString(offset + 4, value);
			return value.length() + 4;
		}
		public boolean match(String mask) {
			return mask.equals(value);
		}
		public String toString() {
			return value;
		}
	}
	
	private class DataSchemaIdList extends DataSchemaObject<List<Integer>> {					
		public DataSchemaIdList(int index, String name) {			
			super(index, name);
		}
		public int getNumberOfHeaderBytes() {
			return 4;
		}
		public int getObjectTotalSize() {
			return 8 + value.size() * 4;
		}
		public void readValue(DataBuffer buffer) {
			int offset = buffer.getInt(index);
			int length = buffer.getInt(offset);
			value = new LinkedList<Integer>();
			for(int i=0; i<length; i++)
			  value.add(buffer.getInt(offset + 4 + i * 4));
		}
		public int writeValue(DataBuffer buffer, int offset) {
			buffer.putInt(index, offset);
			buffer.putInt(offset, value.size());
			for(int i=0; i<value.size(); i++)
			  buffer.putInt(offset + 4 + i * 4, value.get(i));
			return value.size() * 4 + 4;
		}
		public boolean match(String mask) {
			return mask.equals(value);
		}
		public String toString() {
			String s = "[";
			for(int i=0; i<value.size(); i++)
				s += (i > 0 ? ", " : "") + value.get(i);
			return s + "]";
		}
	}
	
	private String className;
	private Map<String, DataSchemaObject<?>> schemas = new HashMap<String, DataSchemaObject<?>>();
	private short nextIndex = 0;
	
	public DataSchema(String className) {
		this.className = className;
	}
	
	public void createSchemaByte(String fieldName) {
		DataSchemaByte object = new DataSchemaByte(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}
	
	public void createSchemaShort(String fieldName) {
		DataSchemaShort object = new DataSchemaShort(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}

	public void createSchemaInt(String fieldName) {
		DataSchemaInt object = new DataSchemaInt(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}

	public void createSchemaFloat(String fieldName) {
		DataSchemaFloat object = new DataSchemaFloat(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}
	
	public void createSchemaString(String fieldName) {
		DataSchemaString object = new DataSchemaString(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}
	
	public void createSchemaIdList(String fieldName) {
		DataSchemaIdList object = new DataSchemaIdList(nextIndex, fieldName);
		nextIndex += object.getNumberOfHeaderBytes();
		schemas.put(fieldName,  object);		
	}
	
	public void setValue(String fieldName, byte value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaByte == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of byte");
		((DataSchemaByte)schemaObject).set(value);
	}

	public void setValue(String fieldName, short value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaShort == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of short");
		((DataSchemaShort)schemaObject).set(value);
	}

	public void setValue(String fieldName, int value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaInt == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of int");
		((DataSchemaInt)schemaObject).set(value);
	}
	
	public void setValue(String fieldName, float value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaFloat == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of byte");
		((DataSchemaFloat)schemaObject).set(value);
	}
	
	public void setValue(String fieldName, String value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaString == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of string");
		((DataSchemaString)schemaObject).set(value);
	}
	
	public void setValue(String fieldName, List<Integer> value) {
		DataSchemaObject<?> schemaObject = schemas.get(fieldName);
		if(schemaObject == null)
			throw new RuntimeException(className + " has no attribute with name " + fieldName);
		if(schemaObject instanceof DataSchemaIdList == false)
			throw new RuntimeException("attribute " + fieldName + " of " + className + " is not type of ID list");
		((DataSchemaIdList)schemaObject).set(value);
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
	
	public boolean match(DataQuery dataQuery) {
		for(Query query : dataQuery.getQueries()) {
			String fieldName = query.getFieldName();
			DataSchemaObject<?> schemaObject = schemas.get(fieldName);
			if(schemaObject == null)
				continue;
			if(schemaObject.match(query.getMask()) == false)
				return false;			
		}
		return true;
	}
	
	@Override 
	public String toString() {
		String s = "";
		boolean first = true;
		for(String fieldName : schemas.keySet()) {
			s += (first ? "" : ", ") + fieldName + ": " + schemas.get(fieldName);
			first = false;
		}
		return s;
	}
}
