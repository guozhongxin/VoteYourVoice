package com.vote.activity;

import com.example.vote4.R;
import com.vote.utils.AccessInternet;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	
	private DBUtils db = new DBUtils();
	private AccessInternet internet = new AccessInternet();
	private GetNetworkState networkstate = new GetNetworkState();
	private EditText et_username;
	private EditText et_password;
	private Button bt_login;
	private Button bt_register;
	private AnimationDrawable frameAnimation = null;
	private ImageView iv = null;
	private LinearLayout splash = null;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String hint = null;
			switch (msg.what) {
			case 1:
				toMain();
				break;
			case 2:
				stopSplash();
				hint = "密码过期~";
				showToast(hint);
            	break;
			case 3:
				stopSplash();
				break;
			case 4:
				toMain();
				break;
			case 5:
				stopSplash();
				hint = "Login failed!";
				showToast(hint);
				break;
			}
		}
	}; 
	
	private Thread isRememberedThread = new Thread() {
		@Override
	    public void run() {
    		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
            int userid = userInfo.getInt("id", 0); 
            Log.e("userid", userid+"");
            if (userid != 0) {
            	String username = userInfo.getString("username", "");
            	String password = userInfo.getString("password", "");
            	if (checkUser(username, password)) {
            		Message msg = new Message();
            		msg.what = 1;
            		handler.sendMessage(msg);
            	} else {
            		Message msg = new Message();
            		msg.what = 2;
            		handler.sendMessage(msg);
            	}     	
            } else {
        		Message msg = new Message();
        		msg.what = 3;
        		handler.sendMessage(msg);
            }
		}
	};
	
	private Thread checkUserThread = new Thread() {
		@Override
	    public void run() {
			String username = et_username.getText().toString();
			String password = et_password.getText().toString();
			boolean flag = checkUser(username, password);
			if (flag) {
        		Message msg = new Message();
        		msg.what = 4;
        		handler.sendMessage(msg);
			} else {
        		Message msg = new Message();
        		msg.what = 5;
        		handler.sendMessage(msg); 
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		internet.toAccessInternet();	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		splash = (LinearLayout) findViewById(R.id.loading);
        iv = (ImageView) findViewById(R.id.imgeView);
        iv.setBackgroundResource(R.anim.loading);
        frameAnimation = (AnimationDrawable) iv.getBackground();
        frameAnimation.start();
        
		getViewsAndSetListenners();
		
		if (checkNetwork()) {
			isRememberedThread.start();
		} else {
			stopSplash();
			initView();
			String hint = "没有网络~";
			showToast(hint);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void getViewsAndSetListenners() {
		et_username = (EditText) findViewById(R.id.email);
		et_password = (EditText) findViewById(R.id.password);
		bt_login = (Button) findViewById(R.id.sign_in_button);
		bt_register = (Button) findViewById(R.id.register_button);
		
		bt_login.setOnClickListener(this);
		bt_register.setOnClickListener(this);
	}
	
	/**
	 *显示存储的用户名
	 */
	public void initView() {
		SharedPreferences userInfo = getSharedPreferences("user_info", 0);  
        String username = userInfo.getString("username", "");
        et_username.setText(username);
	} 
	
	/**
	 * 停止Loading动画
	 */
	public void stopSplash() {
		frameAnimation.stop();
		splash.setVisibility(View.GONE);
	}
	
	public boolean checkInput() {
		boolean flag = false;
		String username = et_username.getText().toString();
		String password = et_password.getText().toString();
		View focusView = null;
		if (TextUtils.isEmpty(username)) {
			et_username.setError(getString(R.string.error_field_required));
			focusView = et_username;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(password)) {
			et_password.setError(getString(R.string.error_field_required));
			focusView = et_password;
			focusView.requestFocus();
		} else {
			flag = true;
		} 
		return flag;
	}
	
	public void toMain() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		this.startActivity(intent);
		finish();
	}
	
	@SuppressWarnings("static-access")
	public boolean checkNetwork() {
		return (networkstate.getAPNType(getApplicationContext()) > 0) ? true : false;
	}
	
	public void register() {
		if (!checkNetwork()) {
			Toast toast = Toast.makeText(getApplicationContext(), "没有网络~", Toast.LENGTH_SHORT);  
        	toast.show(); 
		} else {
			Intent intent = new Intent(this, RegisterActivity.class);
			this.startActivity(intent);	
		}
	}
	
	public void showToast(String hint) {
		Toast toast = Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT);  
    	toast.show();
	}
	
	public boolean checkUser(String username, String password) {
		boolean flag = false;
		int userid = db.checkUser(username, password);
		if (userid != 0) {
			SharedPreferences userInfo = getSharedPreferences("user_info", MODE_PRIVATE);  
            userInfo.edit().putInt("id", userid).commit();  
            userInfo.edit().putString("username", username).commit();  
            userInfo.edit().putString("password", password).commit();  
        	flag = true;
		}
		return flag;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sign_in_button: 
				if (checkInput() && checkNetwork()) {
					splash.setVisibility(View.VISIBLE);
					frameAnimation.start();
					checkUserThread.start();
				}
				break;
			case R.id.register_button: 
				register();
				break;
			default:
				break;
		}
	}
}