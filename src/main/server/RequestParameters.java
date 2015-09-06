package main.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import main.utils.Logger;

public class RequestParameters {

  private Map<String, String> parameters = new LinkedHashMap<String, String>();
  
  public RequestParameters(String uri) throws UnsupportedEncodingException {
    read(uri);
  }
  
  public void add(String parameters) throws UnsupportedEncodingException {
    read(parameters);
  }
  
  private void read(String uri) throws UnsupportedEncodingException {
    if(uri == null)
      return;

    int parametersStart = uri.indexOf('?') + 1;
    if(parametersStart <= 0)
      parametersStart = 1;
    
    if(parametersStart >= uri.length())
      return;
    
    String converted = URLDecoder.decode(uri.substring(parametersStart), "UTF-8");
    if(converted.indexOf('&') < 0 && converted.indexOf('=') < 0)
      return;

    byte[] parametersBytes = converted.getBytes();
    LinkedList<String> pairs = new LinkedList<String>();
    int parameterStart = 0;
    boolean split = true;
    for(int i = 0; i < parametersBytes.length; i++) {
      byte character = parametersBytes[i];
      if(character == '"' && i != parametersBytes.length - 1)
        split = !split;
      else if((character == '&' && split) || i == parametersBytes.length - 1) {
        if(i == parametersBytes.length - 1)
          i++;
        if(parameterStart == i)
          parameterStart++;
        else {
          String pair = converted.substring(parameterStart, i);
          pairs.add(pair);
          parameterStart = i + 1;
        }
      }
    }   

    for (String pair : pairs) {
      final int idx = pair.indexOf("=");
      final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
      String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
      if(value.length() >= 2 && value.startsWith("\"") && value.endsWith("\""))
        value = value.substring(1, value.length() - 1);
      if(this.parameters.put(key, value) != null)
        Logger.error("parameter string contains parameter " + key + " multiple times");
    }
  }
  
  public boolean contains(String parameterName) {
    return parameters.containsKey(parameterName);
  }
  
  public String get(String parameterName) {
    String value = parameters.get(parameterName);
    if(value == null)
      return "";
    return value;
  }
  
  public void set(String parameterName, String parameterValue) {
    parameters.put(parameterName, parameterValue);
  }
  
  public String get(String parameterName, String defaultValue) {
    String value = parameters.get(parameterName);
    if(value == null)
      return defaultValue;
    return value;
  }
  
  @Override
  public String toString() {
    String s = "";
    int counter = 0;
    for(Entry<String, String> parameter : parameters.entrySet())
      s += (counter++ == 0 ? "" : ", ") + parameter.getKey() + "=" + parameter.getValue();
    return s;
  }
}
