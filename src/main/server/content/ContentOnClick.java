package main.server.content;


public class ContentOnClick {

  protected int id;
  protected int subId;
  protected String parameter;
  
	public ContentOnClick(int id, int subId, String parameter) {		
		this.id = id;
		this.subId = subId;
		this.parameter = parameter;
	}
	
	public String getParameter() {
	  return parameter;
	}
}
