package com.vote.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.vote.pojo.Group;
import com.vote.pojo.Option;
import com.vote.pojo.User;
import com.vote.pojo.Vote;


import android.net.ParseException;
import android.util.Log;

public class DBUtils {
	public static String host = "http://voteyourvoice.duapp.com/vote";
	public static HttpClient httpclient = null;
	public static HttpPost httppost = null;
	public static HttpResponse response = null;
	public static HttpEntity entity = null;
	public static InputStream is = null;
	public static StringBuilder sb = null;
	public static JSONArray jArray;
	public static String result = null;
	public static String action = null;
	
	public String getRequestResult(String action, ArrayList<NameValuePair> nameValuePairs) {
		String result = null;
		action = host + "/" + action;
		Log.e("action", action);
		
		try{
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(action);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			is = entity.getContent();	
		}	catch(Exception e)	{
			Log.e("log_tag", "Error in http connection"+e.toString());
		}
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
			sb = new StringBuilder();
		    
			String line = null;
			while ((line = reader.readLine()) != null) {
				Log.e("line",line);
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();

		}	catch(Exception e)	{
			Log.e("log_tag", "Error converting result "+e.toString());
		}
		return result;
	}
	
	/**
	 * @param username
	 * @param password
	 * @return
	 */
	public int checkUser(String username, String password) {
		int status = 0;
		action = "checkUser.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		if (!result.equals("fail")) {
			status = Integer.parseInt(result);
			Log.e("status", status+"");
		}
		return status;
	}
	
	/**
	 * 0 for success, 1 for user already exists, 2 for email already exists
	 * @param user
	 * @return
	 */
	public int registerUser(User user) {
		int status = 0;
		action = "registerUser.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username", user.getUsername()));
		nameValuePairs.add(new BasicNameValuePair("email", user.getEmail()));
		nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		if (result.equals("user")) {
			status = 1;
		} else if (result.equals("email")) {
			status = 2;
		}
		return status;
	}
	
	/** 
	 * @param userid
	 * @return
	 */
	public ArrayList<Group> getMyGroups(int userid) {
		ArrayList<Group> groupList = new ArrayList<Group>();
		action = "getMyGroups.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		result = getRequestResult(action, nameValuePairs);
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	Group group = new Group();
		    	group.setId(json_data.getInt("id"));
		    	group.setCreatorname(json_data.getString("username"));
		    	group.setGroupname(json_data.getString("groupname"));
		    	group.setDescription(json_data.getString("description"));
		    	groupList.add(group);
		    }
		} catch(JSONException e1) {
		} catch (ParseException e1) {
		     // e1.printStackTrace();
		}
		return groupList;
	}
	
	/**
	 * @param userid
	 * @param group
	 */
	public void createGroup(Group group) {	
		action = "createGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("creatorid", group.getCreatorid()+""));
		nameValuePairs.add(new BasicNameValuePair("groupname", group.getGroupname()));
		nameValuePairs.add(new BasicNameValuePair("description", group.getDescription()));
		result = getRequestResult(action, nameValuePairs);
	}
	
	/**
	 * 
	 * @param vote
	 */
	public void createVote(Vote vote) {
		action = "createVote.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		Log.e("options", vote.getOptions());
		nameValuePairs.add(new BasicNameValuePair("votename", vote.getVotename()));
		nameValuePairs.add(new BasicNameValuePair("voteinfo", vote.getVoetinfo()));
		nameValuePairs.add(new BasicNameValuePair("options", vote.getOptions()));
		nameValuePairs.add(new BasicNameValuePair("groupid", vote.getGroupid()+""));
		nameValuePairs.add(new BasicNameValuePair("creatorid", vote.getCreatorid()+""));
		result = getRequestResult(action, nameValuePairs);
	}
	
	/**
	 * 
	 * @param groupid
	 * @return
	 */
	public ArrayList<Vote> getGroupVotes(int groupid) {
		ArrayList<Vote> voteList = new ArrayList<Vote>();
		action = "getGroupVotes.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs);
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data = null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	Vote vote = new Vote();
		    	vote.setId(json_data.getInt("id"));
		    	vote.setGroupname(json_data.getString("groupname"));
		    	vote.setVotename(json_data.getString("votename"));
		    	vote.setVoetinfo(json_data.getString("voteinfo"));
				voteList.add(vote);
		    }
		} catch(JSONException e1) {
			
		} catch (ParseException e1) {
		     // e1.printStackTrace();
		}
		return voteList;
	}
	
	/**
	 * 
	 * @param groupid
	 */
	public boolean deleteGroup(int userid, int groupid) {
		boolean flag = false;
		action = "deleteGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		if (result.equals("ok")) {
			flag = true;
		}
		return flag;
	}
	
	/**
	 * @param voteid
	 * @return
	 */
	public Vote getVote(int voteid) {
		Vote vote = new Vote();
		action = "getVote.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("voteid", voteid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
			    vote.setId(json_data.getInt("id"));
			    vote.setVotename(json_data.getString("votename"));
			    vote.setVoteinfo(json_data.getString("voteinfo"));
		    }
		} catch(JSONException e1) {
		} catch (ParseException e1) {
		      //e1.printStackTrace();
		}
		return vote;
	}
	
	public ArrayList<Option> getVoteOptions(int voteid) {
		ArrayList<Option> options = new ArrayList<Option>();
		action = "getVoteOptions.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("voteid", voteid+""));
		result = getRequestResult(action, nameValuePairs);
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	Option option = new Option();
		    	option.setId(json_data.getInt("id"));
		    	option.setValue(json_data.getString("option"));
		    	option.setNumber(json_data.getInt("number"));
			    options.add(option);
		    }
		} catch(JSONException e1) {
		} catch (ParseException e1) {
		      //e1.printStackTrace();
		}
		return options;
	}
	
	public boolean isUserVoted(int userid, int voteid) {
		boolean flag = false;
		action = "isUserVoted.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("voteid", voteid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		if (result.equals("yes")) {
			flag = true;
		}
		return flag;
	}
	
	public void vote(int userid, int voteid, String option) {
		action = "vote.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("voteid", voteid+""));
		nameValuePairs.add(new BasicNameValuePair("option", option));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
	}
	
	public ArrayList<Group> searchGroup(String groupname) {
		ArrayList<Group> groupList = new ArrayList<Group>();
		action = "searchGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("groupname", groupname));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	Group group = new Group();
		    	group.setId(json_data.getInt("id"));
		    	group.setCreatorname(json_data.getString("username"));
		    	group.setGroupname(json_data.getString("groupname"));
		    	group.setDescription(json_data.getString("description"));
		    	groupList.add(group);
		    }
		} catch(JSONException e1) {
		} catch (ParseException e1) {
		      //e1.printStackTrace();
		}
		return groupList;
	}
	
	public Group getGroupById(int groupid) {
		Group group = new Group();
		action = "getGroupById.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data=null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	group.setId(json_data.getInt("id"));
		    	group.setGroupname(json_data.getString("groupname"));
		    	group.setDescription(json_data.getString("description"));
		    	group.setCreatorname(json_data.getString("username"));
		    }
		} catch(JSONException e1) {
		} catch (ParseException e1) {
		      //e1.printStackTrace();
		}
		return group;
	}
	/**
	 * 0 for not, 1 for is, 2 for creator
	 * @param userid
	 * @param groupid
	 * @return
	 */
	public int isUserInGroup(int userid, int groupid) {
		int flag = 0;
		action = "isUserInGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
		if (result.equals("creator")) {
			flag = 2;
		} else if(result.equals("yes")) {
			flag = 1;
		}
		return flag;
	}
	
	public void joinGroup(int userid, int groupid) {
		action = "joinGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
	}
	
	public void quitGroup(int userid, int groupid) {
		action = "quitGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
	}
	
	public void dismissGroup(int userid, int groupid) {
		action = "dismissGroup.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("userid", userid+""));
		nameValuePairs.add(new BasicNameValuePair("groupid", groupid+""));
		result = getRequestResult(action, nameValuePairs).replaceAll("\n", "");
	}
	
	public ArrayList<Vote> getOpenVotes() {
		ArrayList<Vote> voteList = new ArrayList<Vote>();
		action = "getOpenVotes.php";
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		result = getRequestResult(action, nameValuePairs);
		try{
		    jArray = new JSONArray(result);
		    JSONObject json_data = null;
		    for(int i=0;i<jArray.length();i++){
		    	json_data = jArray.getJSONObject(i);
		    	Vote vote = new Vote();
		    	vote.setId(json_data.getInt("id"));
		    	vote.setVotename(json_data.getString("votename"));
				voteList.add(vote);
		    }
		} catch(JSONException e1) {
			
		} catch (ParseException e1) {
		     // e1.printStackTrace();
		}
		return voteList;
	}
}