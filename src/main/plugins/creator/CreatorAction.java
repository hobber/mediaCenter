package main.plugins.creator;

import main.server.RequestParameters;

public interface CreatorAction {
  
  public CreatorActionResult handle(RequestParameters parameters, CreatorSession session);
  
  public CreatorActionResult complete(RequestParameters parameters, CreatorSession session);
  
}
