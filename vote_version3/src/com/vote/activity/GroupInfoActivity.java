package com.vote.activity;

import com.example.vote4.R;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupInfoActivity extends Activity implements  OnClickListener {
		
	private DBUtils db = new DBUtils();
	private LinearLayout loading = null;
	private Button bt_action = null;
	private TextView tv_groupname = null;
	private TextView tv_description = null;
	private TextView tv_creatorname = null;
	private int groupid;
	private int userid;
	private int flag;
	private Group group;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				moveLoading();
				initView();
				break;
			case 2:
				moveLoading();
				finish();
				break;
			}
		}
	}; 
	
	private Thread getGroupInfoThread = new Thread() {
		@Override
	    public void run() {
			group = db.getGroupById(groupid);
			flag = db.isUserInGroup(userid, groupid);
			Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	private Thread actionTread = new Thread() {
		@Override
	    public void run() {
			if (flag == 0) {
				db.joinGroup(userid, groupid);
			} else if (flag == 1) {
				db.quitGroup(userid, groupid);
			} else if (flag == 2) {
				db.dismissGroup(userid, groupid);
			}
			Message message=new Message();  
            message.what = 2;  
            handler.sendMessage(message);  
	    }
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_info);
		
		Bundle bundle = getIntent().getExtras();  
		groupid = bundle.getInt("groupid");
		
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
        userid = userInfo.getInt("id", 0);  
        
		loading = (LinearLayout) findViewById(R.id.loading);
		getGroupInfoThread.start();
	}
	
	public void initView() {
		tv_groupname = (TextView) findViewById(R.id.groupname);
		tv_description = (TextView) findViewById(R.id.description);
		tv_creatorname = (TextView) findViewById(R.id.creatorname);
		
		bt_action = (Button) findViewById(R.id.action);
		bt_action.setOnClickListener(this);

		tv_groupname.setText(group.getGroupname());
		tv_description.setText(group.getDescription());
		tv_creatorname.setText(group.getCreatorname());
	
		if (flag == 0) {
			bt_action.setText("加入");
		} else if (flag == 1) {
			bt_action.setText("退出");
		} else if (flag == 2) {
			bt_action.setText("解散");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.action:
				setLoading();
				actionTread.start();
				break;
		}
	}
	
	public void setLoading() {
		loading.setVisibility(View.VISIBLE);
	}
	
	public void moveLoading() {
		loading.setVisibility(View.GONE);
	}
}