package main.plugins.creator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.server.RequestParameters;
import main.utils.Logger;

public class CreatorActionList {

  private HashMap<String, CreatorAction> actions = new HashMap<String, CreatorAction>();
  private String firstActionName;
  
  public CreatorActionList(String firstActionName) {
    this.firstActionName = firstActionName;
  }
  
  public boolean add(Map<String, CreatorAction> actions) {
    if(actions == null) {
      Logger.error("tried to add null to actions");
      return false;
    }
    
    for(Entry<String, CreatorAction> action : actions.entrySet()) {
      CreatorAction predessor = this.actions.put(action.getKey(),  action.getValue());
      if(predessor != null)
        Logger.error("duplicated action " + action.getKey());
      Logger.log("added action " + action.getKey());
    }
    
    return true;
  }
  
  public CreatorActionResult handle(RequestParameters parameters) {
    String actionName = parameters.get("action");
    if(actionName.length() == 0)
      return new CreatorActionResult("no valid action name given");
    Logger.log("performing action " + actionName);
    
    CreatorAction action = actions.get(actionName);
    if(action == null)
      return new CreatorActionResult("unknown action " + actionName);
    
    String sessionId = parameters.get("parameter", null);
    CreatorSession session = null;
    if(sessionId != null)
      session = CreatorSessionStore.get(Integer.parseInt(sessionId));
    
    if((session == null && actionName.equals(firstActionName) == false) || 
       (session != null && actionName.equals(session.currentAction) == false)) {      
      String predesessor = session == null ? firstActionName : session.currentAction;
      CreatorActionResult result = actions.get(predesessor).complete(parameters, session);
      if(result.isEmpty() == false)
        return result;
    }
    
    if(session != null)
      session.currentAction = actionName;
    return action.handle(parameters, session);
  }
}
