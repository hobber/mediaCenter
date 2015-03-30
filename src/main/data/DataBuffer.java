package main.data;

import java.nio.ByteBuffer;

public class DataBuffer {
	
	ByteBuffer buffer;
	
	public DataBuffer(int size) {
		buffer = ByteBuffer.allocate(size);
	}
	
	public void putByte(int index, byte value) {
		buffer.put(index, value);
	}
	
	public byte getByte(int index) {
		return buffer.get(index);
	}
	
	public void putShort(int index, short value) {
		buffer.putShort(index, value);
	}
	
	public short getShort(int index) {
		return buffer.getShort(index);
	}
	
	public void putInt(int index, int value) {
		buffer.putInt(index, value);
	}
	
	public int getInt(int index) {
		return buffer.getInt(index);
	}	
	
	public void putString(int index, String value) {
		byte[] bytes = value.getBytes();
		for(int i = 0; i < bytes.length; i++)
			buffer.put(index + i, bytes[i]);		
	}
	
	public String getString(int index, int length) {
		char[] tmp = new char[length];
		for(int i=0; i<length; i++)
			tmp[i] = (char)buffer.get(index + i);	  
		return new String(tmp);
	}
	
	public int getSize() {
		return buffer.capacity();
	}

	public byte[] getArray() {
		return buffer.array();
	}
 }
