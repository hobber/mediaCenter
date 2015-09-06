package main.plugins.creator;

import java.io.IOException;

import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.Timestamp;

public class CreatorSession {
  
  public static final int DUMMY_ID = -1;
  
  Integer id;
  Timestamp timestamp;
  String name = "";
  String endpoint = "";
  String currentAction = "";
  
  public CreatorSession(int id, String currentAction) {
    this.id = id;
    this.currentAction = currentAction;
    timestamp = new Timestamp();    
  }
  
  public CreatorSession(FileReader file) {
    try {
      id = file.readInt();
      timestamp = new Timestamp(file.readLong());
      name = file.readString(file.readInt());
      endpoint = file.readString(file.readInt());
      Logger.log("read session " + id + ": "+ name);
    } catch(IOException e) {
      Logger.error(e);
    }
  }
  
  public void write(FileWriter file) {
    try {
      file.writeInt(id);
      file.writeLong(timestamp.value());
      file.writeInt(name.length());
      file.writeString(name);
      file.writeInt(endpoint.length());
      file.writeString(endpoint);
      Logger.log("wrote session " + id + ": "+ name);
    } catch(IOException e) {
      Logger.error(e);
    }
  }
}
