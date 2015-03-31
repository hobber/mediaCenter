package main.data;


public interface DataObject {
	public DataBuffer serialize();
	public int getEntrySize();
	public boolean match(DataQuery dataQuery);
}
