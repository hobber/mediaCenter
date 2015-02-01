import java.util.Scanner;

import oauth.OAuthAPISpotify;
import oauth.Token;
import utils.XMLFile;

public class Main {
	
	public static void main(String[] args) {
		
		XMLFile config = new XMLFile("config.xml");
		if(config.read() == false) {
			return;
		}		
		
		String clientID = config.getString("config.clientID", "");
		String clientSecret = config.getString("config.clientSecret", "");
		String redirectURI = config.getString("config.redirectURI", "");
		if(clientID.length() == 0 || clientSecret.length() == 0 || redirectURI.length() == 0) {
			System.out.println("PLEASE STORE CLIENT ID, CLIENT SECRET AND REDIRECT URI IN CONFIG FILE!");
			return;
		}
		
		String scope = "user-read-private";
		
		OAuthAPISpotify oauthSpotify = new OAuthAPISpotify(clientID, clientSecret, redirectURI, scope);
		oauthSpotify.getAuthorizationUrl();
				
		String authorizationUrl = oauthSpotify.getAuthorizationUrl();	
		System.out.println("visit");
    System.out.println(authorizationUrl);
    System.out.println("then paste authorization code below");
    
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();	
		scanner.close();
		
		Token authorizationToken = oauthSpotify.getAuthorizationToken(code);
    if(authorizationToken.failed())
    {
    	System.err.println("ERROR: "+authorizationToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+authorizationToken.getExpirationTime());
    
    Token refreshToken = oauthSpotify.getRefreshToken(authorizationToken);
    if(refreshToken.failed())
    {
    	System.err.println("ERROR: "+refreshToken.getError());
    	return;
    }
    else
    	System.out.println("Token will expire @ "+refreshToken.getExpirationTime());
	}

}
