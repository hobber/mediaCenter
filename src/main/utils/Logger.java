package main.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

  private static Logger INSTANCE;
  private static DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
  private PrintWriter writer;
  
  private Logger(String logFile) throws FileNotFoundException, UnsupportedEncodingException {  	
  	writer = new PrintWriter(logFile, "UTF-8");  	
  }
  
  public static void setup(ConfigElementGroup config) {
  	if(config == null) {
  	  log("no log config defined -> no log file will be created");
  	  return;
  	}
  	
  	String logFile = config.getString("logFile", null);
  	if(logFile == null) {
  		log("no log file defined -> no log file will be created");
  		return;
  	}
  	
  	try {
  		INSTANCE = new Logger(logFile);	    	
  	} catch(UnsupportedEncodingException e) {
  		error("UTF-8 encoding not supported -> no log file will be created");
  	} catch(FileNotFoundException e) {
  		error("Log file not found -> no log file will be created");
  	}
  }
  
  public static void closeLogFile() {
  	if(INSTANCE != null)
  		INSTANCE.writer.close();
  }
  
  public static void log(String msg) {
	  System.out.println("INFO:  " + msg);
	  if(INSTANCE != null)
	  	INSTANCE.logToFile(msg);
  }
  
  public static void error(String msg) {
  	System.err.println("ERROR: " + msg);
  	if(INSTANCE != null)
	  	INSTANCE.errorToFile(msg);
  }
  
  public static void error(Throwable error) {
  	System.err.println("ERROR: " + error.getMessage());
  	error.printStackTrace();
  	if(INSTANCE != null)
	  	INSTANCE.errorToFile(error);
  }
  
  public static String getDate() {
  	return format.format(Calendar.getInstance().getTime());
  }
  
  private void logToFile(String msg) {
  	writer.println(getDate() + " - INFO:  " + msg);
  }
  
  private void errorToFile(String msg) {
  	writer.println(getDate() + " - ERROR: " + msg);
  }
  
  private void errorToFile(Throwable error) {
  	writer.println(getDate() + " - ERROR: " + error.getMessage());
  	error.printStackTrace(writer);
  }
}
