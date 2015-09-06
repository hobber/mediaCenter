package main.plugins.creator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;

public class CreatorSessionStore {

  private static int nextSessionId = 0;
  private static HashMap<Integer, CreatorSession> sessions = new HashMap<Integer, CreatorSession>();
  
  public static void initialize(FileReader file) {
    try {     
      int size = file.readInt();
      for(int i = 0; i < size; i++) {
        CreatorSession session = new CreatorSession(file);
        sessions.put(session.id, session);
        if(session.id >= nextSessionId)
          nextSessionId = session.id + 1;
      }
    } catch(IOException e) {
      Logger.error(e);
    }
  }
  
  public static void write(FileWriter file) {
    try {
      file.writeInt(sessions.size());
      for(CreatorSession session : sessions.values())
        session.write(file);
    } catch(IOException e) {
      Logger.error(e);
    }
  }
  
  public static CreatorSession get(int sessionId) {
    return sessions.get(sessionId);
  }
  
  public static CreatorSession create(String currentAction) {
    CreatorSession session = new CreatorSession(nextSessionId++, currentAction);
    sessions.put(session.id, session);
    return session;
  }
  
  public static void remove(int sessionId) {
    sessions.remove(sessionId);
  }
  
  public static int getSize() {
    return sessions.size();
  }
  
  public static Collection<CreatorSession> getAll() {
    return sessions.values();
  }
}
