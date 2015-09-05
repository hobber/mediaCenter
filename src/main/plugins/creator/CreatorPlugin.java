package main.plugins.creator;

import java.io.IOException;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import main.plugins.Plugin;
import main.server.content.ContentErrorPage;
import main.server.content.ContentEventOnClick;
import main.server.content.ContentGroup;
import main.server.content.ContentLocation;
import main.server.content.ContentText;
import main.server.content.ContentTextLine;
import main.server.content.ContentText.TextType;
import main.server.menu.ContentMenuEntry;
import main.utils.ConfigElementGroup;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;

public class CreatorPlugin implements Plugin {

  private static final int lineHeight = 19;
  
  private String databaseFileName;
  private ContentMenuEntry menuEntry;
  private CreatorContentPageCreate createPage;
  
  public CreatorPlugin(ConfigElementGroup config) {
    databaseFileName = config.getString("file", null);
    
    if(databaseFileName == null)
      throw new RuntimeException("Please store your creator database file name in the config file");
    
    try {
      FileReader file = new FileReader(databaseFileName);
      createPage = new CreatorContentPageCreate(getName(), file);
    } catch(IOException e) {
      Logger.error(e);
      createPage = new CreatorContentPageCreate(getName());
    }
    
    menuEntry = new ContentMenuEntry(this, Plugin.ICON_PATH + "creator.svg");
    menuEntry.addSubMenuEntry(createPage);
  }
  
  @Override
  public String getName() {
    return "Creator";
  }

  @Override
  public void update() {
    
  }

  @Override
  public void saveState() {
    try {
      FileWriter file = new FileWriter(databaseFileName);
      createPage.saveState(file);
    } catch(IOException e) {
      Logger.error(e);
    }
  }

  @Override
  public ContentMenuEntry getMenuEntry() {
    return menuEntry;
  }

  static ContentErrorPage createErrorPage(ContentLocation location, String error) {
    Logger.error("Creator: " + error);
    return new ContentErrorPage(location, error);
  }
  
  static void showJSONObject(ContentGroup group, JSONObject object) {
    int line = CreatorPlugin.showObjectKeysRecursively(group, object, 0, 1, true);
    group.add(new ContentText(5, 5 + lineHeight * line, "}", TextType.FIXED_SIZE));
  }
  
  private static String createPadding(int intend, boolean addLeadingBracket) {
    String padding = "";
    for(int i = 0; i < intend; i++)
      if(i == intend - 1 && addLeadingBracket)
        padding += "{ ";
      else      
        padding += "&nbsp;&nbsp;";
    return padding;
  }
  
  private static int showObjectKeysRecursively(ContentGroup group, JSONObject object, int line, int intend, boolean addLeadingBracket) {
    String padding = createPadding(intend, addLeadingBracket);
    int counter = 0;
    Set<?> keySet = object.keySet();
    for(Object key : keySet) {
      String closing = ++counter == keySet.size() ? "" : ","; 
      if(key instanceof String) {
        Object value = object.get((String)key);
        if(value instanceof String)
          group.add(new ContentTextLine(5, 5 + lineHeight * line++)
                           .addFixedWidthText(padding)
                           .addFixedWidthTextBold("\"" + key.toString() + "\"", new ContentEventOnClick(1))
                           .addFixedWidthText(": \"" + value.toString() + "\"" + closing));
        else if(value instanceof JSONObject) {
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": {"));                                                           
          line = CreatorPlugin.showObjectKeysRecursively(group, (JSONObject)value, line, intend + 1, false);
          if(addLeadingBracket)
            padding = createPadding(intend, false);
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthText("}" + closing));
        }
        else if(value instanceof JSONArray) {
          group.add(new ContentTextLine(5, 5 + lineHeight * line++).addFixedWidthText(padding).addFixedWidthTextBold("\"" + key.toString() + "\"").addFixedWidthText(": ["));          
          line = CreatorPlugin.showObjectKeysRecursively(group, (JSONArray)value, line, intend + 1);
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
  
  private static int showObjectKeysRecursively(ContentGroup group, JSONArray array, int line, int intend) {
    String tab = createPadding(intend, false);
    int counter = 0;
    for(int i = 0; i < array.length(); i++) {
      String closing = ++counter == array.length() ? "" : ",";
      Object value = array.get(i);
      if(value instanceof String)
        group.add(new ContentText(5, 5 + lineHeight * line++, tab + "\"" + value.toString() + "\"" + closing, TextType.FIXED_SIZE));
      else if(value instanceof JSONObject) {        
        line = CreatorPlugin.showObjectKeysRecursively(group, (JSONObject)value, line, intend + 1, true);
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
