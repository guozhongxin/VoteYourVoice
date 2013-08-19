package com.vote.activity;

import com.example.vote4.R;
import com.vote.pojo.User;
import com.vote.utils.DBUtils;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private DBUtils db = new DBUtils();
	private EditText et_username = null;  
	private EditText et_password = null; 
	private EditText et_email = null; 
	private EditText et_password_confirm = null; 
	private static Button bt_register = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getViewsAndSetListeners();
	    bt_register = (Button)findViewById(R.id.register_button2);
	    bt_register.setOnClickListener(new View.OnClickListener() {
	    	@Override
	    	public void onClick(View arg0){
	    		registerUser();
	    	}
	    });
	}
	    /*
	public void onFocusChange(View v, boolean hasFocus){
		
	}*/
/*	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {  
		switch (item.getItemId()) {
			case R.id.register :
				registerUser();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
*/	
	public void getViewsAndSetListeners() {
		et_username 	= (EditText)findViewById(R.id.username);  
	    et_password 	= (EditText)findViewById(R.id.password);  
	    et_email 	= (EditText)findViewById(R.id.email);  
	    et_password_confirm = (EditText)findViewById(R.id.password_confirm);  
		et_username.setOnFocusChangeListener(new OnFocusChangeListener(){  

	       		@Override            
			public void onFocusChange(View v, boolean hasFocus) {               
				if(hasFocus){                    
					if(v.getId() == R.id.username){
						et_username.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background_change));
						et_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));						
						et_password_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));						
						et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background2));
					}  
					}               
			}
			});
		et_password.setOnFocusChangeListener(new OnFocusChangeListener(){            
			@Override            
			public void onFocusChange(View v, boolean hasFocus) {               
				if(hasFocus){                    
					if(v.getId() == R.id.password){
						et_username.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background));						
						et_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background_change));
						et_password_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));
						et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background2));
					}  
					}               
			}
			});
		et_password_confirm.setOnFocusChangeListener(new OnFocusChangeListener(){            
			@Override            
			public void onFocusChange(View v, boolean hasFocus) {               
				if(hasFocus){                    
					if(v.getId() == R.id.password_confirm){
						et_username.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background));
						et_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));
						et_password_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background_change));
						et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background2));
					}  
					}               
			}
			});
		et_email.setOnFocusChangeListener(new OnFocusChangeListener(){            
			@Override            
			public void onFocusChange(View v, boolean hasFocus) {               
				if(hasFocus){                    
					if(v.getId() == R.id.email){
					et_username.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background));
					et_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));
						et_password_confirm.setBackgroundDrawable(getResources().getDrawable(R.drawable.password_background));
						et_email.setBackgroundDrawable(getResources().getDrawable(R.drawable.email_background2_change));
					}  
					}               
			}
			});

	}
	
	public void registerUser() {
		String username	= et_username.getText().toString();
		String email 	= et_email.getText().toString();
		String password = et_password.getText().toString();
		String pasword_confirm = et_password_confirm.getText().toString();
		
		View focusView = null;
		if (TextUtils.isEmpty(username)) {
			et_username.setError(getString(R.string.error_field_required));
			focusView = et_username;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(email)) {
			et_email.setError(getString(R.string.error_field_required));
			focusView = et_email;
			focusView.requestFocus();
		} else if (!email.contains("@")) {
			et_email.setError(getString(R.string.error_invalid_email));
			focusView = et_email; 
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(password)) {
			et_password.setError(getString(R.string.error_field_required));
			focusView = et_password;
			focusView.requestFocus();
		} else if (TextUtils.isEmpty(pasword_confirm)) {
			et_password_confirm.setError(getString(R.string.error_field_required));
			focusView = et_password_confirm;
			focusView.requestFocus();
		} else if (password.equals(pasword_confirm)) {
			User user = new User();
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			int status = db.registerUser(user);
			if (status == 0) {
	        	Toast toast = Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_SHORT);  
	        	toast.show();  
				finish();	
			} else if (status == 1){
				et_username.setError(getString(R.string.error_field_user_exists));
				focusView = et_username;
				focusView.requestFocus(); 
			} else {
				et_email.setError(getString(R.string.error_field_email_exists));
				focusView = et_email;
				focusView.requestFocus(); 
			}
		} else {
			et_password_confirm.setError(getString(R.string.error_field_password_confirm));
			focusView = et_password_confirm;
			focusView.requestFocus();
		}
	}
}