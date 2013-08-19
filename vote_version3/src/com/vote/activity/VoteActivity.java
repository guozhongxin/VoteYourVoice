package com.vote.activity;


import java.util.ArrayList;

import com.example.vote4.R;
import com.vote.pojo.Option;
import com.vote.pojo.Vote;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class VoteActivity extends Activity implements  OnClickListener {
		
	private DBUtils db = new DBUtils();
	private GetNetworkState networkstate = new GetNetworkState();
	
	private static Button bt_vote;
	public static String voteid1 = "1"; 
	private TextView tv_voteinfo=null;
	public static TextView tv_votename = null;
	private RadioGroup rg_options = null;
	private RadioButton rb_option = null;
	private LinearLayout loading = null;
	private int userid ;
	private int voteid;
	private boolean isVoted = false;
	
	private Vote vote = null;
	private ArrayList<Option> options = null;
	
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
				String hint = "没有网络~";
				showToast(hint);
				break;
			}
		}
	}; 
	
	private Thread getVoteThread = new Thread() {
		@Override
	    public void run() {
			if (!checkNetwork()) {
				Message message=new Message();  
	            message.what = 2;  
				return;
			}
			
			getVoteInfo();
			Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	private Thread voteThread = new Thread() {
		@Override
	    public void run() {
			if (!checkNetwork()) {
				Message message=new Message();  
	            message.what = 2;  
				return;
			}
			
			vote();
			getVoteInfo();
            Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);
		
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
        userid = userInfo.getInt("id", 0);  
		Bundle bundle = getIntent().getExtras();  
		voteid = bundle.getInt("voteid");
		
		loading = (LinearLayout) findViewById(R.id.loading);
		getVoteThread.start();
	}
	
	public void initView() {
		tv_votename = (TextView)findViewById(R.id.votename);
		tv_voteinfo = (TextView)findViewById(R.id.voteinfo);
		
		rg_options = (RadioGroup)findViewById(R.id.options);
		
		bt_vote = (Button)findViewById(R.id.vote);
		bt_vote.setOnClickListener(this);
		
		tv_votename.setText(vote.getVotename());
		tv_voteinfo.setText(vote.getVoetinfo());
		
		int num = 0;
		for (int i = 0; i < options.size(); i++) {
			Option option = options.get(i);
			num += option.getNumber();
		}
		
		rg_options.removeAllViews();
		for (int i = 0; i < options.size(); i++) {
			Option option = options.get(i);
			RadioButton radio = new RadioButton(this);
			radio.setText(option.getValue());
			ProgressBar prog = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
			@SuppressWarnings("deprecation")
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
			prog.setLayoutParams(params);  

			if (num > 0) {
				prog.setProgress(100*option.getNumber()/num);
			}
			rg_options.addView(radio);
			rg_options.addView(prog);
		}
		
        if (isVoted) {
        	findViewById(R.id.vote).setEnabled(false);
        } else {
        	findViewById(R.id.vote).setEnabled(true);
        }
	}
		
	public void vote() {
		int radioId = rg_options.getCheckedRadioButtonId();
        rb_option = (RadioButton) rg_options.findViewById(radioId);
        String option = rb_option.getText().toString();
        db.vote(userid, voteid, option);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.vote:
				int radioId = rg_options.getCheckedRadioButtonId();
				if (radioId < 0) {
		        	Toast.makeText(getApplicationContext(), "选一个吧~", Toast.LENGTH_SHORT).show();  
				} else { 
					voteThread.start();
					setLoading();
				}
				break;
		}
	}
	
	public void getVoteInfo() {
		vote = db.getVote(voteid);
		options = db.getVoteOptions(voteid);
        isVoted = db.isUserVoted(userid, voteid);
	}
	
	public void setLoading() {
		loading.setVisibility(View.VISIBLE);
	}
	
	public void moveLoading() {
		loading.setVisibility(View.GONE);
	}
	
	public void showToast(String hint) {
		Toast toast = Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT);  
    	toast.show();
	}
	
	@SuppressWarnings("static-access")
	public boolean checkNetwork() {
		return (networkstate.getAPNType(getApplicationContext()) > 0) ? true : false;
	}
}