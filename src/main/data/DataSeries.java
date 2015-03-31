package main.data;

import java.util.List;
import java.util.LinkedList;

import main.data.DataQuery.Query;


public class DataSeries implements DataObject {

	private DataSchema schema;
	private DataSchemaObject<String> name;
	
	public DataSeries(String name) {
		createSchema();
		this.name.set(name);		
	}
	
	public DataSeries(DataBuffer buffer) {
		createSchema();
		schema.readValues(buffer);		
	}
	
	private void createSchema() {
		schema = new DataSchema(this.getClass().getName());
		name = schema.getSchemaString("name");
	}
	
	@Override
  public DataBuffer serialize() {
		return schema.writeValues();
  }

	@Override
  public int getEntrySize() {
		return schema.getBufferSize();
  }
		
	public DataSelector getSelectorName() {
		List<String> fieldNames = new LinkedList<String>();
		fieldNames.add("name");
		return schema.getSelector(fieldNames);		
	}
	
	@Override
	public String toString() {
		return name.get();
	}
	
	@Override
	public boolean match(DataQuery dataQuery) {
		for(Query query : dataQuery.getQueries()) {
			if(query.getFieldName().equals("name") && name.match(query.getMask()) == false)
				return false;
		}
		return true;
	}
	
	public static String getClassName() {
		return DataSeries.class.getName();
	}
}
