package main.plugins.creator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import main.server.content.ContentButton;
import main.server.content.ContentErrorPage;
import main.server.content.ContentEventOnClick;
import main.server.content.ContentGroup;
import main.server.content.ContentInputForm;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentText.TextType;
import main.server.content.ContentTextLine;
import main.server.menu.ContentMenuSubEntry;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class CreatorContentPageCreate extends ContentMenuSubEntry {

  private static final int lineHeight = 19;
  private String pluginName;
  private int sessionCounter = 0;
  private HashMap<Integer, CreatorSession> sessions = new HashMap<Integer, CreatorSession>();
  
  public CreatorContentPageCreate(String pluginName) {
    super("Create");
    this.pluginName = pluginName;
  }
  
  public CreatorContentPageCreate(String pluginName, FileReader file) {
    super("Create");
    this.pluginName = pluginName;
    readState(file);
  }
  
  private void readState(FileReader file) {
    try {
      sessionCounter = file.readInt();      
      int size = file.readInt();
      System.out.println("read " + size + " CreatorSessions");
      for(int i = 0; i < size; i++) {
        CreatorSession session = new CreatorSession(file);
        sessions.put(session.getId(), session);
      }
    } catch(IOException e) {
      Logger.error(e);
    }
  }
  
  public void saveState(FileWriter file) {
    try {
      file.writeInt(sessionCounter);
      file.writeInt(sessions.size());
      System.out.println("write " + sessions.size() + " CreatorSessions");
      for(CreatorSession session : sessions.values())
        session.write(file);
    } catch(IOException e) {
      Logger.error(e);
    }
  }

  @Override
  public ContentItem handleAPIRequest(Map<String, String> parameters) {
    System.out.print("parameters: ");
    for(Entry<String, String> parameter : parameters.entrySet())
      System.out.print(parameter.getKey() + "=" + parameter.getValue() + " ");
    System.out.println("");
    
    String parameter = parameters.get("parameter");    
    String action = parameters.get("action");
    if(parameter != null && action != null) {
      if(action.equals("create"))
        return showCreatePage(parameters);
//      else if(action.equals("open"));
      else if(action.equals("delete"))
        return showDeletePage(parameters);
    }
    return showStartPage();    
  }
  
  public ContentItem showStartPage() {
    int sessionId = sessionCounter++;    
    
    ContentLocation location = new ContentLocation(pluginName, getName());
    ContentPage page = new ContentPage(location, "Define endpoint");
    
    for(CreatorSession session : sessions.values()) {
      ContentGroup group = page.createContentGroup();
      group.add(new ContentButton(5,5, "open", session.getId() + "&action=open"));
      group.add(new ContentButton(55,5, "delete", session.getId() + "&action=delete"));
      group.add(new ContentText(125, 5, session.getName()));      
    }
    
    ContentGroup group = page.createContentGroup();    
    ContentInputForm form = new ContentInputForm(Integer.toString(sessionId) + "&action=create", "create");
    form.addInput("name", "name");
    form.addInput("endpoint", "endpoint");
    group.add(form);
    
    return page;
  }
  
  public ContentItem showCreatePage(Map<String, String> parameters) {
    int sessionId = Integer.parseInt(parameters.get("parameter"));
    CreatorSession session = new CreatorSession(sessionId); 
    sessions.put(sessionId, session);
    
    String name = parameters.get("name");    
    if(name == null || name.length() == 0)
      return createErrorPage("no valid session name defined (" + name + ")");
    
    String endpoint = parameters.get("endpoint");
    if(endpoint == null || endpoint.length() == 0)
      return createErrorPage("no valid session name defined (" + name + ")");
    
    session.setName(name);
    session.setEndpoint(endpoint);
    
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
  
  private ContentItem showDeletePage(Map<String, String> parameters) {
    String parameter = parameters.get("parameter");
    if(parameter == null || parameter.length() == 0)
      return createErrorPage("no valid session id given");
    int sessionId = Integer.parseInt(parameter);
    CreatorSession session = sessions.get(sessionId); 
    if(session == null)
      return createErrorPage("no valid session with id " + parameter + " defined");
    sessions.remove(sessionId);
    return showStartPage();
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
          group.add(new ContentTextLine(5, 5 + lineHeight * line++)
                           .addFixedWidthText(padding)
                           .addFixedWidthTextBold("\"" + key.toString() + "\"", new ContentEventOnClick(1))
                           .addFixedWidthText(": \"" + value.toString() + "\"" + closing));
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
  
  private ContentLocation createLocation() {
    return new ContentLocation(pluginName, getName());
  }
  
  private ContentErrorPage createErrorPage(String error) {
    Logger.error("Creator: " + error);
    return new ContentErrorPage(createLocation(), error);
  }
}
