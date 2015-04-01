package main.data;

public abstract class DataSchemaObject<Type> {
	protected Type value;
	protected int index;
	protected String name;
	
	protected DataSchemaObject(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public void set(Type value) {
		this.value = value;
	}
	
	public Type get() {
		return value;
	}
	
	public abstract int getNumberOfHeaderBytes();
	public abstract int getObjectTotalSize();
	public abstract void readValue(DataBuffer buffer);
	public abstract int writeValue(DataBuffer buffer, int offset);	
	public abstract boolean match(String mask);
}
