package main.plugins.creator;

import java.util.Map;

public interface CreatorSessionStep {

  public Map<String, CreatorAction> getActions();
  
}
