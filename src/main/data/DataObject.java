package main.data;


public abstract class DataObject {
	
	protected String className;
	protected DataSchema schema;

	protected DataObject(String className) {
		this.className = className;
		schema = new DataSchema(this.getClass().getName());
		createSchema();
	}
	
	protected DataObject(String className, DataBuffer buffer) {
		this.className = className;
		schema = new DataSchema(this.getClass().getName());
		createSchema();
		schema.readValues(buffer);	
	}
	
  public DataBuffer serialize() {
		return schema.writeValues();
  }

  public int getEntrySize() {
		return schema.getBufferSize();
  }
  
	public boolean match(DataQuery dataQuery) {
		return schema.match(dataQuery);
	}
	
	public final String toString() {
		return schema.toString();
	}
	
	protected abstract void createSchema();	
}
