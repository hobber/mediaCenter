package main.oauth;

import java.util.Vector;

public class OAuthTokenRefresher implements Runnable {

	private boolean running = true;
	private Vector<OAuthToken> tokens = new Vector<OAuthToken>();
	private Thread thread;
	static private OAuthTokenRefresher instance;
	
	private OAuthTokenRefresher() {		
		thread = new Thread(this);
    thread.start();
	}
	
	static public void addToken(OAuthToken token) {
		if(instance == null)
			instance = new OAuthTokenRefresher();
		instance.tokens.addElement(token);
	}
	
	static public void stop() {
		if(instance != null) {
			instance.running = false;
			instance.thread.interrupt();
		}
	}
	
	@Override
  public void run() {
		while(running) {
			for(OAuthToken token : tokens)
				if(token.willExpireWithin(120*1000) && token.refresh() == false)
					System.err.println("ERROR: a token could not be refreshed!");
			try {
				Thread.sleep(60*1000);
			} catch(InterruptedException e) {			
			}
		}
  }

}
