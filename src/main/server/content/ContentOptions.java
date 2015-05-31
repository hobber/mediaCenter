package main.server.content;

import java.util.HashMap;

public class ContentOptions {
	
  private HashMap<String, String> options = new HashMap<String, String>();
  
  public ContentOptions() {
  }
  
  public ContentOptions(String name, String value) {
    addOption(name, value);
  }
  
  public ContentOptions addOption(String name, String value) {
    options.put(name, value);
    return this;
  }
  
  @Override
  public String toString() {
    String s = "{";
    int counter = 0;
    for(String option : options.keySet()) {
      String value = options.get(option);
      if(value.equals("true") || value.equals("false"))
        s += (counter++ > 0 ? ", " : "") + "\"" + option + "\": " + value;
      else if(value.matches("-?\\d+(\\.\\d+)?"))
        s += (counter++ > 0 ? ", " : "") + "\"" + option + "\": " + value;
      else
        s += (counter++ > 0 ? ", " : "") + "\"" + option + "\": \"" + value + "\"";
    }
    return s + "}";
  }
}
