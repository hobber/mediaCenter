package main.plugins.creator;

import java.util.HashMap;
import java.util.Map;

import main.server.RequestParameters;
import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentInput;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;

public class CreatorSessionStepInitialize implements CreatorSessionStep {
  
  static public class InitActionStart implements CreatorAction {

    private CreatorSessionStepInitialize step;
    
    public InitActionStart(CreatorSessionStepInitialize step) {
      this.step = step;
    }
    
    @Override
    public CreatorActionResult handle(RequestParameters parameters, CreatorSession session) {
      ContentPage page = step.createPage();
      step.addSessionNameField(page,  session, null);
      step.addEndpointField(page,  session, null);
      return new CreatorActionResult(page);
    }
    
    @Override
    public CreatorActionResult complete(RequestParameters parameters, CreatorSession session) {
      String name = parameters.get("name");
      String endpoint = parameters.get("endpoint");
      
      if(name.length() > 0) {
        if(session == null)
          session = CreatorSessionStore.create("initstart");
        session.name = name;
      }
      
      if(endpoint.length() > 0) {
        if(session == null)
          session = CreatorSessionStore.create("initstart");
        session.endpoint = endpoint;
      }
      
      if(name.length() > 0 && endpoint.length() > 0)
        return new CreatorActionResult();      
      
      ContentPage page = step.createPage();
      step.addSessionNameField(page,  session, name.length() > 0 ? null : "invalid name");
      step.addEndpointField(page,  session, endpoint.length() > 0 ? null : "invalid endpoint");
      return new CreatorActionResult(page);
    }
  }
  
  static public class InitActionAuthentication implements CreatorAction {

    private CreatorSessionStepInitialize step;
    
    public InitActionAuthentication(CreatorSessionStepInitialize step) {
      this.step = step;
    }
    
    @Override
    public CreatorActionResult handle(RequestParameters parameters, CreatorSession session) {
      ContentPage page = step.createPage();
//      step.addSessionNameField(page,  session, null);
//      step.addEndpointField(page,  session, null);
      return new CreatorActionResult(page);
    }
    
    @Override
    public CreatorActionResult complete(RequestParameters parameters, CreatorSession session) {
      return new CreatorActionResult();
    }
  }
  
  private ContentLocation location;
  private String actionsPrefix;
  
  public CreatorSessionStepInitialize(ContentLocation location, String actionsPrefix) {
    this.location = location;
    this.actionsPrefix = actionsPrefix;
  }
  
  @Override
  public Map<String, CreatorAction> getActions() {
    HashMap<String, CreatorAction> actions = new HashMap<String, CreatorAction>();
    actions.put(actionsPrefix + "start", new InitActionStart(this));
    actions.put(actionsPrefix + "authentication", new InitActionAuthentication(this));
    return actions;
  }
  
  private ContentPage createPage() {
    return new ContentPage(location, "Define endpoint");
  }
  
  private void addSessionNameField(ContentPage page, CreatorSession session, String error) {
    String sessionName = session == null ? null : session.name;
    
    page.createContentGroup().add(new ContentInput(5, 5, 100, 250, "name", "name", sessionName, error));
  }
  
  private void addEndpointField(ContentPage page, CreatorSession session, String error) {
    
    String endpoint = session == null ? null : session.endpoint;
    int sessionId = session == null ? CreatorSession.DUMMY_ID : session.id;
    
    ContentGroup group = page.createContentGroup();
    group.add(new ContentInput(5, 5, 100, 250, "authentication", "endpoint", endpoint, error));
    group.add(new ContentButton(5, 35, "test endpoint", sessionId + "&action=" + actionsPrefix + "authentication"));
  } 

  
//  static public ContentItem authentication(ContentLocation location, CreatorSession session, RequestParameters parameters) {
//    String name = parameters.get("name");    
//    if(name.length() == 0)
//      return CreatorPlugin.createErrorPage(location, "no valid session name defined");
//    session.name = name;
//    
//    String endpoint = parameters.get("endpoint");
//    if(endpoint.length() == 0)
//      return CreatorPlugin.createErrorPage(location, "no valid endpoint defined");    
//    session.endpoint = endpoint;
//    
//    ContentPage page = new ContentPage(location, "Define endpoint");
//        
//    ContentGroup group = page.createContentGroup();
//    group.add(new ContentInput(5, 5, 100, 250, "name", "name", session.name));
//    group.add(new ContentInput(5, 25, 100, 250, "authentication", "authentication", session.endpoint));
//    group.add(new ContentButton(370, 20, "test", Integer.toString(session.id) + "&action=authentication"));
//    
//    return page;
//  }
}
