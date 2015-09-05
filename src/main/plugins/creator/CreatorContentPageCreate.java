package main.plugins.creator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;
import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;

public class CreatorContentPageCreate extends ContentMenuSubEntry {
  
  private int sessionCounter = 0;
  private HashMap<Integer, CreatorSession> sessions = new HashMap<Integer, CreatorSession>();
  private ContentLocation location;
  
  public CreatorContentPageCreate(String pluginName) {
    super("Create");
    this.location = new ContentLocation(pluginName, getName());
  }
  
  public CreatorContentPageCreate(String pluginName, FileReader file) {
    super("Create");
    this.location = new ContentLocation(pluginName, getName());
    readState(file);
  }
  
  private void readState(FileReader file) {
    try {
      sessionCounter = file.readInt();      
      int size = file.readInt();
      System.out.println("read " + size + " CreatorSessions");
      for(int i = 0; i < size; i++) {
        CreatorSession session = new CreatorSession(file);
        sessions.put(session.id, session);
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
    if(parameter != null) {
      int sessionId = Integer.parseInt(parameter);
      CreatorSession session = sessions.get(sessionId);
      String action = parameters.get("action");
      
      if(action != null) {
        if(action.equals("delete")) {
          sessions.remove(sessionId);
          return showStartPage();
        }
        
        if(action.equals("open")) {
          if(session == null)
            return CreatorPlugin.createErrorPage(location, "invalid session ID " + sessionId);
          action = session.currentStep;
        }
        
        if(action.equals("initialize")) {          
          return CreatorSessionStepInitialize.start(location, session, -1, "create");
        }
        
        else if(action.equals("create")) {
          if(session == null) {
            session = new CreatorSession(sessionCounter++);
            session.currentStep = "initialize";
            sessions.put(session.id, session);
            
            ContentItem error = CreatorSessionStepInitialize.complete(location, session, parameters);
            if(error != null)
              return error;
            session.currentStep = "create";
          }
          
          return CreatorSessionStepCreate.start(location, session);
        }
      }      
    }
    
    return showStartPage();    
  }
  
  public ContentItem showStartPage() {
    ContentPage page = new ContentPage(location, "Creator");
    
    for(CreatorSession session : sessions.values()) {
      ContentGroup group = page.createContentGroup();
      group.add(new ContentButton(5,5, "open", session.id + "&action=open"));
      group.add(new ContentButton(55,5, "delete", session.id + "&action=delete"));
      group.add(new ContentText(125, 5, session.name));      
    }
    
    ContentGroup group = page.createContentGroup();
    group.add(new ContentButton(5, 5, "Create new plugin", "-1&action=initialize"));    
    
    return page;
  }
}
