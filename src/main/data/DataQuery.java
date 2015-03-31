package main.data;

import java.util.LinkedList;
import java.util.List;

public class DataQuery {
	
	public class Query {
		private String fieldName;
		private String mask;
		public Query(String fieldName, String mask) {			
			this.fieldName = fieldName;
			this.mask = mask;
		}
		public String getFieldName() {
			return fieldName;
		}
		public String getMask() {
			return mask;
		}
	}
	
	private List<Query> queries = new LinkedList<Query>();
	private String className;
	
	public DataQuery(String className) {
		this.className = className;
	}
	
	public String getQueryClassName() {
		return className;
	}
	
	public void addQuery(String fieldName, String mask) {
		queries.add(new Query(fieldName, mask));		
	}
	
	public List<Query> getQueries() {
		return queries;
	}
}
