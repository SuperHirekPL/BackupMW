package service.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.netty.handler.ipfilter.OneIpFilterHandler;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.Facebook;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.experimental.api.Posts;
import com.restfb.types.Comment;
import com.restfb.types.Group;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.Post.Likes;
import com.restfb.types.User;

import dbContex.cassandra.DBContex;

public class FbConnector {

	private String _appToken;
	private FacebookClient _fbClient;
	private User _currentUser;
	private DBContex _dbContex;

	private final byte LIKE_VALUE = 1;
	private final byte ONE_COMMENT_VALUE = 2;
	private final byte MANY_COMMENTS_VALUE = 2;
	private final byte Max_VALUE = LIKE_VALUE + ONE_COMMENT_VALUE + MANY_COMMENTS_VALUE;

	private DBContex getDbContex() {
		if (_dbContex == null) {
			_dbContex = new DBContex();
		}
		return _dbContex;
	}

	public FbConnector(String appToken) {
		this._appToken = appToken;
		this._fbClient = new DefaultFacebookClient(_appToken, Version.VERSION_2_2);

	}

	public FbConnector(String appToken, Version version) {
		this._appToken = appToken;
		this._fbClient = new DefaultFacebookClient(_appToken, version);
	}

	public User getCurrentUser() {
		if (_currentUser == null) {
			_currentUser = _fbClient.fetchObject("me", User.class);
		}
		return _currentUser;
	}

	public String getNameUser() {
		String userName = "";
		User user = _fbClient.fetchObject("me", User.class);

		userName = user.getName();
		return userName;
	}

	public List<String> getGroupMembersId(String groupId) {
		List<String> membersId = new ArrayList<String>();
		try {
			Connection<User> userList = _fbClient.fetchConnection(groupId + "/members", User.class);

			for (List<User> myUserConnectionPage : userList) {
				for (User user : myUserConnectionPage) {
					membersId.add(user.getId());
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return membersId;
	}

	public List<String> getGroupPostId(String groupId) {
		List<String> postsId = new ArrayList<String>();
		try {
			Connection<Post> postList = _fbClient.fetchConnection(groupId + "/feed", Post.class);
			for (List<Post> myPostConnectionPage : postList) {
				for (Post post : myPostConnectionPage) {
					postsId.add(post.getId());
				}
			}
		} catch (Exception e) {

		}
		return postsId;
	}

	public List<Integer> getItemRecomendationByUser(String groupId) {
		Connection<Post> postList = _fbClient.fetchConnection(groupId + "/feed", Post.class);
		HashMap<String, Byte> tempMapUserValue = new HashMap<String, Byte>();
		Long sizeItemsId = null;
		Long sizeUserId = null;

		List<String> tempUsersId = new ArrayList<String>();
		List<String> tempItemsId = new ArrayList<String>();

		Byte tempValue = 0;
		String tempPostId = "";
		String likeId = "";
		
		List<NamedFacebookType> likesList = new ArrayList<NamedFacebookType>();

		// lecimy po wszystkich postach
		for (List<Post> myFeedConnectionPage : postList) {
			for (Post post : myFeedConnectionPage) {
				if (post.getLikes() != null) {
					tempValue = (byte) (tempValue + LIKE_VALUE);
					// post.getComments().getData().get(0).getFrom().getId();
					likesList =	post.getLikes().getData();
					for (NamedFacebookType like : likesList) {
						// do sprawdzenia! Czy na pewno like id. 
						likeId = like.getId();
						tempMapUserValue.put(likeId, LIKE_VALUE);
						// System.out.print(likeId);
					}
					if (post.getComments() != null) {
						for (Comment comment : post.getComments().getData()) {
							String commentatorId = comment.getFrom().getId();
							// spawdz czy zawiera klucz
							if (tempMapUserValue.containsKey(commentatorId)) {
								tempValue = (byte) tempMapUserValue.get(commentatorId);
								// oznacza ze uzytkownik dal like i narazie
								// tylko jedenen komentarz
								if (tempValue == LIKE_VALUE) {
									tempMapUserValue.put(commentatorId, (byte) (tempValue + ONE_COMMENT_VALUE));
								} else if (tempValue == LIKE_VALUE + ONE_COMMENT_VALUE) {
									tempMapUserValue.put(commentatorId, (byte) (tempValue + MANY_COMMENTS_VALUE));
								}
							} else {
								// oznacz ze post nie ma like
								tempMapUserValue.put(commentatorId, (byte) (tempValue + ONE_COMMENT_VALUE));
							}
						}
					}
				}
				

				tempPostId = convertPostId(post.getId());
				for (String key : tempMapUserValue.keySet()) {
					tempValue = tempMapUserValue.get(key);
					if (!tempUsersId.contains(key)) {
						sizeUserId = getDbContex().getLengthFromTable(DBContex.USERSIDS_TABLE) + 1;
						getDbContex().insertIntoUserIDs(sizeUserId.toString(), key);
						tempUsersId.add(key);
					}
					getDbContex().insertIntoUsers(key, tempPostId.toString(), tempValue.toString());
					if (!tempItemsId.contains(tempPostId)) {
						sizeItemsId = getDbContex().getLengthFromTable(DBContex.ITEMSIDS_TABLE) + 1;
						getDbContex().insertIntoItemsIDs(sizeItemsId.toString(), tempPostId);
						tempItemsId.add(tempPostId);
					}
					getDbContex().insertIntoItems(tempPostId, key, tempValue.toString());
					System.out.println(" postid: " + convertPostId(post.getId()) + " userID:  " + key + " value: "
							+ tempMapUserValue.get(key).toString());
					System.out.println("ID: " + sizeItemsId.toString() + " postID " + convertPostId(post.getId()));
				}
			}
		}

		return null;
	}

	private String convertPostId(String postId) {
		return postId.split("_")[1];
	}
	
	public Long getResultFbSearch(){
		
		/*DEPRACTED*/
		Connection<Post> publicSearch =
				_fbClient.fetchConnection("search", Post.class,
				    Parameter.with("q", "watermelon"), Parameter.with("type", "list"));	
//		Connection<User> targetedSearch =_fbClient.fetchConnection("1604917516431522/members", User.class,
//			    Parameter.with("q", "Marek"), Parameter.with("type", "user"));
		//return targetedSearch.getData().size();
		return publicSearch.getTotalCount();
	}
	
	public String testSmapleFql(){
		String query = "SELECT uid, name FROM user WHERE uid=978123312214781";
		List<FqlUser> users = _fbClient.executeFqlQuery(query, FqlUser.class);

		return "Users: " + users;




		
	}
	
	public class FqlUser {
		  @Facebook
		  String uid;

		  @Facebook
		  String name;

		  @Override
		  public String toString() {
		    return String.format("%s (%s)", name, uid);
		  }
		}

	/*
	 * public void CreateDateToModelCF(String groupId){ Connection<Post>
	 * postList = _fbClient.fetchConnection(groupId+"/feed", Post.class); for
	 * (List<Post> myFeedConnectionPage : postList) for (Post post :
	 * myFeedConnectionPage){ if(post.getLikes()!=null){ for(NamedFacebookType
	 * like : post.getLikes().getData()){ lik }
	 * 
	 * } } }
	 */

}
