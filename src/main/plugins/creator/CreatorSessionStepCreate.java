package main.plugins.creator;

import java.util.HashMap;
import java.util.Map;

import main.plugins.creator.CreatorSessionStepInitialize.InitActionStart;
import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentLocation;
import main.server.content.ContentPage;

import org.json.JSONObject;

public class CreatorSessionStepCreate implements CreatorSessionStep {

  private ContentLocation location;
  private String actionsPrefix;
  
  public CreatorSessionStepCreate(ContentLocation location, String actionsPrefix) {
    this.location = location;
    this.actionsPrefix = actionsPrefix;
  }
  
  @Override
  public Map<String, CreatorAction> getActions() {
    HashMap<String, CreatorAction> actions = new HashMap<String, CreatorAction>();
    return actions;
  }
  
//  static ContentItem start(ContentLocation location, CreatorSession session) {    
//    ContentPage page = new ContentPage(location, "Create new Plugin");
//    ContentGroup group = page.createContentGroup();
//    
//    String text = "{\"menu\": {" +
//                     "id: \"file\"," +
//                       "\"value\": \"File\"," + 
//                       "\"popup\": {" +
//                          "\"menuitem\": [" + 
//                             "{\"value\": \"New\", \"onclick\": \"CreateNewDoc()\"}," + 
//                             "{\"value\": \"Open\", \"onclick\": \"OpenDoc()\"}," + 
//                             "{\"value\": \"Close\", \"onclick\": \"CloseDoc()\"}" +
//                          "]," + 
//                          "\"attributes\": ['red', 'big', 'clickable']" +
//                       "}" +
//                    "}" +
//                  "}";
//    
//    JSONObject object = new JSONObject(text);
//    CreatorPlugin.showJSONObject(group, object);
//    
//    return page;
//  }
//  
//  static ContentItem complete(ContentLocation location, CreatorSession session, Map<String, String> parameters) {
//    return new ContentPage(location, "Create plugin");
//  }
}
