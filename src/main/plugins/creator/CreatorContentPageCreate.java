package main.plugins.creator;

import java.util.Map;
import java.util.Set;

import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentText.TextType;
import main.server.content.ContentTextLine;
import main.server.menu.ContentMenuSubEntry;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreatorContentPageCreate extends ContentMenuSubEntry {

  private static final int lineHeight = 19;
  private String pluginName;
  
  public CreatorContentPageCreate(String pluginName) {
    super("Create");
    this.pluginName = pluginName;
  }

  @Override
  public ContentItem handleAPIRequest(Map<String, String> parameters) {
    ContentPage page = new ContentPage(new ContentLocation(pluginName, getName()), "Create new Plugin");
    ContentGroup group = page.createContentGroup();
    
    String text = "{\"menu\": {" +
                     "id: \"file\"," +
                       "\"value\": \"File\"," + 
                       "\"popup\": {" +
                          "\"menuitem\": [" + 
                             "{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"}," + 
                             "{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"}," + 
                             "{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}" +
                          "]," + 
                          "\"attributes\": ['red', 'big', 'clickable']" +
                       "}" +
                    "}" +
                  "}";
    
    JSONObject object = new JSONObject(text);
    int line = showObjectKeysRecursively(group, object, 0, 1, true);
    group.add(new ContentText(5, 5 + lineHeight * line++, "}", TextType.FIXED_SIZE));
    
    return page;
  }
  
  private String createPadding(int intend, boolean addLeadingBracket) {
    String padding = "";
    for(int i = 0; i < intend; i++)
      if(i == intend - 1 && addLeadingBracket)
        padding += "{ ";
      else      
        padding += "&nbsp;&nbsp;";
    return padding;
  }
  
  private int showObjectKeysRecursively(ContentGroup group, JSONObject object, int line, int intend, boolean addLeadingBracket) {    
    String padding = createPadding(intend, addLeadingBracket);
    int counter = 0;
    Set<?> keySet = object.keySet();
    for(Object key : keySet) {
      String closing = ++counter == keySet.size() ? "" : ","; 
      if(key instanceof String) {
        Object value = object.get((String)key);
        if(value instanceof String)
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": \"" + value.toString() + "\"" + closing));
        else if(value instanceof JSONObject) {
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": {"));                                                           
          line = showObjectKeysRecursively(group, (JSONObject)value, line, intend + 1, false);
          if(addLeadingBracket)
            padding = createPadding(intend, false);
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthText("}" + closing));
        }
        else if(value instanceof JSONArray) {
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": ["));          
          line = showObjectKeysRecursively(group, (JSONArray)value, line, intend + 1);
          if(addLeadingBracket)
            padding = createPadding(intend, false);
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthText("]" + closing));
        }
        else
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": \"" + value.toString() + "\"" + closing));
      } 
      else
        group.add(new ContentText(5, 5 + lineHeight * line++, padding + key.toString() + closing, TextType.FIXED_SIZE));
      
      if(addLeadingBracket)
        padding = createPadding(intend, false);
    }
    return line;
  }
  
  private int showObjectKeysRecursively(ContentGroup group, JSONArray array, int line, int intend) {
    String tab = createPadding(intend, false);
    int counter = 0;
    for(int i = 0; i < array.length(); i++) {
      String closing = ++counter == array.length() ? "" : ",";
      Object value = array.get(i);
      if(value instanceof String)
        group.add(new ContentText(5, 5 + lineHeight * line++, tab + "\"" + value.toString() + "\"" + closing, TextType.FIXED_SIZE));
      else if(value instanceof JSONObject) {        
        line = showObjectKeysRecursively(group, (JSONObject)value, line, intend + 1, true);
        group.add(new ContentText(5, 5 + lineHeight * line++, tab + "}" + closing, TextType.FIXED_SIZE));
      }
      else if(value instanceof JSONArray)
        line = showObjectKeysRecursively(group, (JSONArray)value, line, intend + 1);
      else
        group.add(new ContentText(5, 5 + lineHeight * line++, tab + value.toString(), TextType.FIXED_SIZE));      
    }
    return line;
  }

}
