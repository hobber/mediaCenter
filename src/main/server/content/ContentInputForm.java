package main.server.content;

import java.util.LinkedList;

import org.json.JSONException;

public class ContentInputForm extends ContentItem {

  private class ContentTextInput extends ContentItem {
    
    public ContentTextInput(String caption, String valueName) {
      try {
        data.put("type", "textInput");
        data.put("caption", caption);
        data.put("name", valueName);
      } catch(JSONException e) {
        System.err.println("ERROR: " + e.getMessage());
      }  
    }
  }
  
  private LinkedList<ContentTextInput> items = new LinkedList<ContentTextInput>();
  
  public ContentInputForm(String parameter, String buttonCaption) {
    try {
      data.put("type", "inputForm");
      data.put("parameter", parameter);
      data.put("buttonCaption", buttonCaption);
    } catch(JSONException e) {
      System.err.println("ERROR: " + e.getMessage());
    }   
  }
  
  public void addInput(String caption, String valueName) {
     items.add(new ContentTextInput(caption, valueName));
  }
  
  @Override
  public String getContentString() {
    String s = data.toString();
    s = s.substring(0, s.length() - 1) + ", \"items\": [";
    for(int i = 0; i< items.size(); i++)
      s += (i > 0 ? ", " : "") + items.get(i).getContentString(); 
    return s + "]}";
  }
}
