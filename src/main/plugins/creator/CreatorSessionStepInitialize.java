package main.plugins.creator;

import main.server.RequestParameters;
import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentInput;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;

public class CreatorSessionStepInitialize {

  static public ContentItem start(ContentLocation location, CreatorSession session, int sessionId) {    
    ContentPage page = new ContentPage(location, "Define endpoint");
    
    String sessionName = session == null ? null : session.name;
    String endpoint = session == null ? null : session.endpoint;
    
    ContentGroup group = page.createContentGroup();
    group.add(new ContentInput(5, 5, 100, 250, "name", "name", sessionName));
    group.add(new ContentInput(5, 25, 100, 250, "authentication", "authentication", endpoint));
    group.add(new ContentButton(370, 20, "test", Integer.toString(sessionId) + "&action=authentication"));
    
    return page;
  }
  
  static public ContentItem authentication(ContentLocation location, CreatorSession session, RequestParameters parameters) {
    String name = parameters.get("name");    
    if(name.length() == 0)
      return CreatorPlugin.createErrorPage(location, "no valid session name defined");
    session.name = name;
    
    String endpoint = parameters.get("endpoint");
    if(endpoint.length() == 0)
      return CreatorPlugin.createErrorPage(location, "no valid endpoint defined");    
    session.endpoint = endpoint;
    
    ContentPage page = new ContentPage(location, "Define endpoint");
        
    ContentGroup group = page.createContentGroup();
    group.add(new ContentInput(5, 5, 100, 250, "name", "name", session.name));
    group.add(new ContentInput(5, 25, 100, 250, "authentication", "authentication", session.endpoint));
    group.add(new ContentButton(370, 20, "test", Integer.toString(session.id) + "&action=authentication"));
    
    return page;
  }

  static public ContentItem complete(ContentLocation location, CreatorSession session, RequestParameters parameters) {    
    return null;
  }
}
