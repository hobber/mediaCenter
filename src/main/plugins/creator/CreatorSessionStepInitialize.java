package main.plugins.creator;

import java.util.Map;

import main.server.content.ContentGroup;
import main.server.content.ContentInput;
import main.server.content.ContentInputForm;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;

public class CreatorSessionStepInitialize {

  static ContentItem start(ContentLocation location, CreatorSession session, int dummySessionId, String nextStep) {    
    ContentPage page = new ContentPage(location, "Define endpoint");
    
    ContentGroup group = page.createContentGroup();
    ContentInputForm form = new ContentInputForm(Integer.toString(dummySessionId) + "&action=" + nextStep, "create");
    group.add(form);
    
    if(session != null && session.name.length() > 0)
      form.addInput("name", "name", session.name);
    else
      form.addInput("name", "name");
    
    if(session != null && session.endpoint.length() > 0)
      form.addInput("endpoint", "endpoint", session.endpoint);
    else
      form.addInput("endpoint", "endpoint");
        
    ContentGroup test = page.createContentGroup();
    test.add(new ContentInput(5, 5, 50, 50, "test", "value", "123"));
    
    return page;
  }

  static ContentItem complete(ContentLocation location, CreatorSession session, Map<String, String> parameters) {
    String name = parameters.get("name");    
    if(name == null || name.length() == 0)
      return CreatorPlugin.createErrorPage(location, "no valid session name defined");
    session.name = name;
    
    String endpoint = parameters.get("endpoint");
    if(endpoint == null || endpoint.length() == 0)
      return CreatorPlugin.createErrorPage(location, "no valid endpoint defined");    
    session.endpoint = endpoint;
    
    return null;
  }
}
