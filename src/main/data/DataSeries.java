package main.data;


public class DataSeries implements DataObject {

	private String name;
	
	public DataSeries(String name) {
		this.name = name;		
	}
	
	public DataSeries(DataBuffer buffer) {	
		int nameLength = buffer.getShort(0);
		name = buffer.getString(2, nameLength);
	}
	
	@Override
  public DataBuffer serialize() {		
		DataBuffer buffer = new DataBuffer(getEntrySize());
		buffer.putShort(0, (short)name.length());
		buffer.putString(2, name);
	  return buffer;
  }

	@Override
  public int getEntrySize() {
		if(name.length() > Short.MAX_VALUE) {
			throw new RuntimeException("Series name's length exceeds " + Short.MAX_VALUE + " characters");			
		}
		int size = name.length() + 2;		
	  return size;
  }
	
	@Override
	public String toString() {
		return name;
	}

}
