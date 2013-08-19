package com.vote.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vote4.R;
import com.vote.pojo.Vote;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

public class CreateVoteActivity extends Activity {
	
	private DBUtils db = new DBUtils();
	private GetNetworkState networkstate = new GetNetworkState();
	
	private int groupid;
	private ImageButton addvote = null;
	private TextView tvs1,tvs2,tvs3,tvs4 = null;
	private EditText et_votename,et_voteinfo,et_vs = null;
	private Vote vote = null;
	private LinearLayout loading = null;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				moveLoading();
				finish();
				break;
			case 2:
				String hint = "没有网络~";
				showToast(hint);
				break;
			}
		}
	}; 
	
	private Thread createVoteThread = new Thread() {
		@Override
	    public void run() {
			if (!checkNetwork()) {
				Message message=new Message();  
	            message.what = 2;  
	            handler.sendMessage(message);  
	            return;
			}
			
			db.createVote(vote);
			Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_vote);
		
		getViewsAndSetListeners();
		moveLoading();
	}
	

	public void getViewsAndSetListeners() {
		loading = (LinearLayout) findViewById(R.id.loading);
		
		et_votename = (EditText)findViewById(R.id.etvname);
		et_voteinfo = (EditText)findViewById(R.id.etvinfo);
		et_vs    = (EditText)findViewById(R.id.inputvs);
				
		tvs1 = (TextView)findViewById(R.id.vs1);
		tvs2 = (TextView)findViewById(R.id.vs2);
		tvs3 = (TextView)findViewById(R.id.vs3);
		tvs4 = (TextView)findViewById(R.id.vs4);
		
		addvote = (ImageButton)findViewById(R.id.vsadd);
		addvote.setOnClickListener(new AddButton());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getMenuInflater().inflate(R.menu.add_vote, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_cv :
				createVote();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createVote() {
		Bundle bundle = getIntent().getExtras();  
		groupid = Integer.parseInt(bundle.getString("groupid"));  	
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
        int userid = userInfo.getInt("id", 0);  
        String votename = et_votename.getText().toString();
        String voteinfo = et_voteinfo.getText().toString();
		String options = tvs1.getText().toString() + "EOF" + tvs2.getText().toString() + "EOF" + tvs3.getText().toString() + "EOF" + tvs4.getText().toString();
		
		View focusView = null;
		if (TextUtils.isEmpty(votename)) {
			et_votename.setError(getString(R.string.add_votename_require));
			focusView = et_votename;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(voteinfo)) {
			et_voteinfo.setError(getString(R.string.add_voteinfo_require));
			focusView = et_voteinfo;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(tvs1.getText().toString())) {
			et_vs.setError(getString(R.string.add_voteoption_require));
			focusView = et_vs;
			focusView.requestFocus();
		} else {
			vote = new Vote();
			vote.setCreatorid(userid);
			vote.setGroupid(groupid);
			vote.setOptions(options);
			vote.setVoteinfo(voteinfo);
			vote.setVotename(votename);
			
			createVoteThread.start();
			setLoading();
		}
	}
	
	class AddButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			String vs = et_vs.getText().toString();
			et_vs.setText("");
			if (vs != null && vs.length() > 0){
				if (tvs1.getText().toString() == null || tvs1.getText().toString().length()<=0){
					
					tvs1.setText(vs);
				}
				else if (tvs2.getText().toString() == null || tvs2.getText().toString().length()<=0){
					tvs2.setText(vs);
				}
				else if (tvs3.getText().toString() == null || tvs3.getText().toString().length()<=0){
					tvs3.setText(vs);
				}
				else if (tvs4.getText().toString() == null || tvs4.getText().toString().length()<=0){
					tvs4.setText(vs);
				}
				else 
					Toast.makeText(CreateVoteActivity.this, "最多4个选项~", Toast.LENGTH_SHORT).show();
			}
		}
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
