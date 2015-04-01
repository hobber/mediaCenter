package main.data;



public class DataRecordHeader {
	
	private int dataId;
  private int dataPointer;
  private int dataLength;
  private short classId;
  private boolean used;
  private DataObject body = null;
	
	public DataRecordHeader(int dataId, int dataPointer, int dataLength, short classId) {
		this.dataId = dataId;
		this.dataPointer = dataPointer;
		this.dataLength = dataLength;
		this.used = true;
	}
	
	public DataRecordHeader(DataBuffer buffer) {
		if(buffer.getSize() != getEntrySize())
			throw new RuntimeException("buffer has wrong size, must have " + getEntrySize() + " bytes!");
		dataId = buffer.getInt(0);
		dataPointer = buffer.getInt(4);
		dataLength = buffer.getInt(8);
		classId = buffer.getShort(12);
		used = buffer.getByte(14) == 1 ? true : false;
	}
	
	public int getDataId() {
		return dataId;
	}
	
	public int getDataPointer() {
		return dataPointer;		
	}
	
	public int getDataLength() {
		return dataLength;
	}
	
	public short getClassId() {
		return classId;
	}	
	
	public int getEntrySize() {
		return getByteCount();
	}
	
	public static int getByteCount() {
		return 15;
	}
	
	public void setBody(DataObject body) {
		this.body = body;
	}
	
	public boolean hasBody() {
		return body != null;
	}
	
	public DataObject getBody() {		
		return body;
	}
	
	@Override
	public String toString() {
		return String.format("%08d - %08d - %08d - %04d - %d", dataId, dataPointer, dataLength, classId, used ? 1 : 0);
	}

  public DataBuffer serialize() {
		DataBuffer buffer = new DataBuffer(getEntrySize());
		buffer.putInt(0, dataId);
		buffer.putInt(4, dataPointer);
		buffer.putInt(8, dataLength);
		buffer.putShort(12, classId);
		buffer.putByte(14, (byte)(used ? 1 : 0));
		return buffer;
	}	
}
