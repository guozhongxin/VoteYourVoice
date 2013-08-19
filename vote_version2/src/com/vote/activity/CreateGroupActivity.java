package com.vote.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vote4.R;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;

public class CreateGroupActivity extends Activity {

	private DBUtils db = new DBUtils();
	private EditText et_groupname = null;
	private EditText et_groupinfo = null;	
	private LinearLayout loading = null;
	private Group group = null;
	private int userid;
	private boolean isCreated = true;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				moveLoading();
				finish();
				break;
			}
		}
	}; 
	
	private Thread createGroupThread = new Thread() {
		@Override
	    public void run() {
			db.createGroup(group);
			Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
        userid = userInfo.getInt("id", 0);  
        
		getViewsAndSetListeners();
		moveLoading();
	}
	
	public void getViewsAndSetListeners() {
		loading = (LinearLayout) findViewById(R.id.loading);
		
		et_groupname = (EditText) findViewById(R.id.etgname);
		et_groupinfo = (EditText) findViewById(R.id.etginfo);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getMenuInflater().inflate(R.menu.add_group, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_cg :
				if (getGroupInfo()) 
					setLoading();
					createGroupThread.start();
				break;

		}
		return super.onOptionsItemSelected(item);
	}
	
	public boolean getGroupInfo() {
		boolean flag = false;
		
		String groupname = et_groupname.getText().toString();
		String groupinfo = et_groupinfo.getText().toString();
		
		View focusView = null;
		if (TextUtils.isEmpty(groupname)) {
			et_groupname.setError(getString(R.string.add_groupname_require));
			focusView = et_groupname;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(groupinfo)) {
			et_groupinfo.setError(getString(R.string.add_groupinfo_require));
			focusView = et_groupinfo;
			focusView.requestFocus();
		} else {
	        group = new Group();
	        group.setCreatorid(userid);
	        group.setGroupname(groupname);
	        group.setDescription(groupinfo);
	        flag = true;
		}
		return flag;
	}
	
	public void setLoading() {
		loading.setVisibility(View.VISIBLE);
	}
	
	public void moveLoading() {
		loading.setVisibility(View.GONE);
	}
}