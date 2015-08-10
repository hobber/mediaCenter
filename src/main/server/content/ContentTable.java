package main.server.content;

import java.util.List;

public class ContentTable extends ContentItem {
  
  private ContentItemArray rows = new ContentItemArray();
  private List<String> widths;
	private int columns;
	
	public ContentTable(int x, int y, int columns, int rowHeight) {
	  super("table");
	  this.columns = columns;
	  setAttribute("x", x);
	  setAttribute("y", y);
	  setAttribute("columns", columns);	
	  setAttribute("rowHeight", rowHeight);
	  setAttribute("rows", rows);
	  setAttribute("widths", widths);
	}
	
	public void addRow(List<ContentItem> row) {
		if(row.size() != this.columns)
			throw new RuntimeException("wrong number of columns (" + row.size() + " instead of " + this.columns + ")");
		rows.addRow(row);
	}
	
	public void setWidths(List<String> widths) {
	  if(widths.size() != columns)
	    throw new RuntimeException("wrong number of columns widths (" + widths.size() + " instead of " + this.columns + ")");
	  
	  this.widths = widths;
	}
}
