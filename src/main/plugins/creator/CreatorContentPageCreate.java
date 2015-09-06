package main.plugins.creator;

import main.server.RequestParameters;
import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Logger;

public class CreatorContentPageCreate extends ContentMenuSubEntry {
  
  private static final String SUB_ENTRY_NAME = "Create";
  
  private CreatorActionList actionList = new CreatorActionList("initstart");
  private ContentLocation location;
  
  public CreatorContentPageCreate(String pluginName) {
    super(SUB_ENTRY_NAME);
    this.location = new ContentLocation(pluginName, SUB_ENTRY_NAME);
    
    actionList.add(new CreatorSessionStepInitialize(location, "init").getActions());
    actionList.add(new CreatorSessionStepCreate(location, "create").getActions());
  }

  @Override
  public ContentItem handleAPIRequest(RequestParameters parameters) {
    System.out.println("parameters: " + parameters);    
    
    String action = parameters.get("action", null);
    
    if(action != null) {

      if(action.equals("delete")) {
        String sessionId = parameters.get("parameter");
        if(sessionId != null) {
          CreatorSessionStore.remove(Integer.parseInt(sessionId));
          return showStartPage();
        }
      }

      if(action.equals("open")) {
        String sessionId = parameters.get("parameter");
        if(sessionId != null) {
          CreatorSession session = CreatorSessionStore.get(Integer.parseInt(sessionId));
          if(session != null)
            parameters.set("action", session.currentAction);
          else
            parameters.set("action", "initstart");
        }
      }

      CreatorActionResult result = actionList.handle(parameters);
      if(result.failed())
        Logger.error("action " + action + " failed: " + result.getError());
      else
        return result.getPage();
    }      
    
    return showStartPage();    
  }
  
  public ContentItem showStartPage() {
    ContentPage page = new ContentPage(location, "Creator");
    
    for(CreatorSession session : CreatorSessionStore.getAll()) {
      ContentGroup group = page.createContentGroup();
      group.add(new ContentButton(5,5, "open", session.id + "&action=open"));
      group.add(new ContentButton(55,5, "delete", session.id + "&action=delete"));
      group.add(new ContentText(125, 5, session.name));      
    }
    
    ContentGroup group = page.createContentGroup();
    group.add(new ContentButton(5, 5, "Create new plugin", "-1&action=initstart"));    
    
    return page;
  }
}
