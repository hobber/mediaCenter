package main.data;

import main.server.content.ContentGroup;

public abstract class DataContent extends DataObject {

	protected DataContent(String className) {
		super(className);
	}
	
	protected DataContent(String className, DataBuffer buffer) {
		super(className, buffer);	
	}
	
	public abstract ContentGroup getContentGroup();
}
