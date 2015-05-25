package main.server.content;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class ContentTable extends ContentItem {

	private int columns;
	
	public ContentTable(int x, int y, int columns, int rowHeight) {
		this.columns = columns;
		
		try {
		  data.put("type", "table");
		  data.put("x", x);
		  data.put("y", y);
		  data.put("columns", columns);	
		  data.put("rowHeight", rowHeight);			
		} catch(JSONException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
	
	public void addRow(List<ContentItem> columns) {
		if(columns.size() != this.columns)
			throw new RuntimeException("wrong number of columns (" + columns.size() + " instead of " + this.columns);
		JSONArray array = new JSONArray();
		for(int i=0; i<this.columns; i++)
			array.put(columns.get(i));
		try {
		  data.append("rows", array);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e);
		}
	}
}
