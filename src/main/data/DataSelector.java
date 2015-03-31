package main.data;

import java.util.LinkedList;
import java.util.List;

public class DataSelector {	
	
	private String className;
	private List<DataSchemaObject<?>>schemaObjects = new LinkedList<DataSchemaObject<?>>();
	
	public DataSelector(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}	
	
	public void addSchemaObject(DataSchemaObject<?> schemaObject) {
		schemaObjects.add(schemaObject);
	}
}
