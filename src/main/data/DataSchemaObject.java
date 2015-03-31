package main.data;

public abstract class DataSchemaObject<Type> {
	protected Type value;
	protected short index;
	protected String name;
	
	protected DataSchemaObject(short index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public void set(Type value) {
		this.value = value;
	}
	
	public Type get() {
		return value;
	}
	
	public abstract short getNumberOfHeaderBytes();
	public abstract short getObjectTotalSize();
	public abstract void readValue(DataBuffer buffer);
	public abstract short writeValue(DataBuffer buffer, short offset);	
	public abstract boolean match(String mask);
}
