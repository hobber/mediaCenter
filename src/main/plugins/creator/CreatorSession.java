package main.plugins.creator;

import java.io.IOException;

import main.utils.FileReader;
import main.utils.FileWriter;
import main.utils.Logger;
import main.utils.Timestamp;

public class CreatorSession {

  int id;
  Timestamp timestamp;
  String name = "";
  String endpoint = "";
  String currentStep = "";
  
  public CreatorSession(int sessionId) {
    id = sessionId;
    timestamp = new Timestamp();
  }
  
  public CreatorSession(FileReader file) {
    try {
      id = file.readInt();
      timestamp = new Timestamp(file.readLong());
      name = file.readString(file.readInt());
      endpoint = file.readString(file.readInt());
      Logger.log("session " + name + ": " + endpoint);
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
    } catch(IOException e) {
      Logger.error(e);
    }
  }
}
