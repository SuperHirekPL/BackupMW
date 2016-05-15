package service.model;

import java.util.concurrent.atomic.AtomicLong;

public class Recomendation {
	private long id;
	private String post;
	private String user;
	private String groupName;
	private String groupId;
	

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String group) {
		this.groupName = group;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	private static final AtomicLong counter = new AtomicLong(100);

	public Recomendation(long id, String post, String user, String group) {
		this.id = id;
		this.post = post; 
		this.user = user;
		this.groupName = group;
	}
	public Recomendation(String post, String user, String group) {
		this.id = counter.incrementAndGet();
		this.post = post; 
		this.user = user;
		this.groupName = group;
	}
	public Recomendation(){
		
	}

}
