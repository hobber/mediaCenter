package main.oauth;

import java.util.Vector;

public class TokenExpirationObserver implements Runnable {

	private Vector<OAuthToken> tokens = new Vector<OAuthToken>();
	private Thread thread;
	static private TokenExpirationObserver instance;
	
	private TokenExpirationObserver() {		
		thread = new Thread(this);
    thread.start();
	}
	
	static public void addToken(OAuthToken token) {
		if(instance == null)
			instance = new TokenExpirationObserver();
		instance.tokens.addElement(token);
	}
	
	@Override
  public void run() {
		while(true) {
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
