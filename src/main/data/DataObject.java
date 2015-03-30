package main.data;


public interface DataObject {
	public DataBuffer serialize();
	public int getEntrySize();
}
