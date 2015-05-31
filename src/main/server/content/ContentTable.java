package main.server.content;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

public class ContentTable extends ContentItem {
  
  private LinkedList<List<ContentItem>> rows = new LinkedList<List<ContentItem>>();
  private List<String> widths;
  private ContentOptions options;
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
			throw new RuntimeException("wrong number of columns (" + columns.size() + " instead of " + this.columns + ")");
		rows.add(columns);
	}
	
	public void setOption(ContentOptions options) {
	  this.options = options; 
	}
	
	public void setWidths(List<String> widths) {
	  if(widths.size() != columns)
	    throw new RuntimeException("wrong number of columns widths (" + widths.size() + " instead of " + this.columns + ")");
	  
	  this.widths = widths;
	}

  @Override
  public String getContentString() {
    String s = data.toString();
    s = s.substring(0, s.length() - 1) + ", \"rows\": [";
    for(int i = 0; i< rows.size(); i++) {
      List<ContentItem> row = rows.get(i);
      s += i > 0 ? ", [" : "[";
      for(int j = 0; j < row.size(); j++)
        s+= (j > 0 ? ", " : "") + row.get(j).getContentString();
      s += "]";
    }
    
    if(widths != null) {
      s += "], \"widths\": [";
      for(int i = 0; i < widths.size(); i++)
        s += (i > 0 ? ", " : "") + "\"" + widths.get(i) + "\"";
    }
      
    return s + "], \"options\": " + options + "}";
  }
}
