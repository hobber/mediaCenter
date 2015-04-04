package main.server.content;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class ContentTable extends ContentItem {

	private int columns;
	
	public ContentTable(int x, int y, int columns, int rowHeight) {
		this.columns = columns;
		
		try {
			put("type", "table");
			put("x", x);
			put("y", y);
			put("columns", columns);	
			put("rowHeight", rowHeight);			
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
		append("rows", array);
		} catch(JSONException e) {
			System.err.println("ERROR: " + e);
		}
	}
}
