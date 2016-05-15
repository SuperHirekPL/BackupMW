package testrun;
import java.util.ArrayList;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.batch.BatchRequest;
import com.restfb.types.FriendList;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.User;

import service.connector.FbConnector;


public class Example {
	private static final String MY_TOKEN="EAAGJtcEBEQsBACFij9vobtALKYTrG1pZAXVPYf3U5MZBdgH5xF3kQl2Ko7pTWtSQukCiYlc70SzlGZAi8WRa78ifQ9C7qwGGwevLiO4QTqJY0VAWC6WTK32K1sioHjZCZCKlAlINieuJm2dYleARC5Vb24Ci6eAL5ZC11uqPe0cAZDZD";

	public static void connectWithFacebookByToken(){
		//FacebookClient fbClient = new DefaultFacebookClient(MY_TOKEN,Version.VERSION_2_2);
		
		Version ver = Version.VERSION_2_2;
//		FacebookClient fbClient2 = new  DefaultFacebookClient(MY_TOKEN, ver);
		
		FbConnector fbConnector = new FbConnector(MY_TOKEN,ver);
		
		fbConnector.saveFacebookPostToDbByGroupId("1604917516431522");
	
		
//		User user = fbClient2.fetchObject("me", User.class);
////		Connection<User> myFriends = fbClient2.fetchConnection("me/friends", User.class);
////		  myFriends.getNextPageUrl();
//		// user.getFavoriteTeams().forEach(n -> System.out.println(n.getId()+" "+n.getName()));
//		 
//		
//		  
//		
//		Connection<Post> myFeed = fbClient.fetchConnection("me/feed", Post.class);
//		
//		
//		Page page = fbClient.fetchObject("cocacola", Page.class);
//		List<User> listUser= new ArrayList<User>();
//		
//		
//		System.out.println("User name: " + user.getName());
//		System.out.println("Page likes: " + page.getLikes());


		

		//System.out.print(user.getMeetingFor());
		 
	}
}
