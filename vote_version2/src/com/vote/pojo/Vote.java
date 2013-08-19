package com.vote.pojo;

public class Vote {
	
	private int id;
	private String votename;
	private String voetinfo;
	private String options;
	private String groupname;
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public void setVoetinfo(String voetinfo) {
		this.voetinfo = voetinfo;
	}
	private int groupid;
	private int creatorid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVotename() {
		return votename;
	}
	public void setVotename(String votename) {
		this.votename = votename;
	}
	public String getVoetinfo() {
		return voetinfo;
	}
	public void setVoteinfo(String voetinfo) {
		this.voetinfo = voetinfo;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public int getCreatorid() {
		return creatorid;
	}
	public void setCreatorid(int creatorid) {
		this.creatorid = creatorid;
	}
}
