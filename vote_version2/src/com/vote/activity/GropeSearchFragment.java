package com.vote.activity;

import android.R.menu;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.vote4.R;
import com.vote.activity.MyGroupsFragment.PullToRefreshListViewAdapter;
import com.vote.activity.MyGroupsFragment.PullToRefreshListViewAdapter.ViewHolder;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView; 
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GropeSearchFragment extends SherlockFragment implements 
OnItemClickListener, OnClickListener {
	
	private DBUtils db = new DBUtils();
	public EditText et_groupname = null;
	private int groupid;
	public View rootView;
	private int userid;
	private LinearLayout loading = null;
	private ArrayList<Group> groupList = new ArrayList<Group>();
	
	//Views
	public ListView lv;
	public Button bt_search = null;
	
	private Menu menu = null;
	private MenuInflater inflater = null;
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				break;
			case 2:
				initView(rootView);
				break;
			}
		}
	}; 
	
	 public void setActionBar() {
	    	menu.clear();
	    	inflater.inflate(R.menu.add_group, menu);
	    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	 @Override
	 public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    	this.menu = menu;
	    	this.inflater = inflater;
	    	setActionBar();
	 }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.group_search, container, false);
		
		loading = (LinearLayout) rootView.findViewById(R.id.loading);
		new Thread() {
			@Override
		    public void run() {
				SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);  
		        userid = userInfo.getInt("id", 0);  
		        groupList = db.getMyGroups(userid);
		        Message message=new Message();  
	            message.what = 2;  
	            handler.sendMessage(message);
		    }
		}.start();
		
		return rootView;
	}  
	
	public void initView(View view) {
		loading.setVisibility(View.GONE);
		
		et_groupname = (EditText) view.findViewById(R.id.groupname);
		bt_search = (Button) view.findViewById(R.id.search);
		bt_search.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.search:
			String groupname = et_groupname.getText().toString();
			if (TextUtils.isEmpty(groupname)) {
				et_groupname.setError(getString(R.string.search_group_require));
				View focusView = et_groupname;
				focusView.requestFocus();
			} else {
				Intent intent = new Intent(getActivity().getApplicationContext(), SearchGroupActivity.class);
				Bundle bundle = new Bundle();    
				bundle.putString("groupname", groupname);
				intent.putExtras(bundle);  
				startActivity(intent);
			}
			break;
	}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if(position < 0) {
			return ;
		}
		Group group = groupList.get(position);
		groupid = group.getId();
		Intent intent = new Intent(getActivity().getApplicationContext(), VoteListActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putString("groupid", groupid+"");
		intent.putExtras(bundle);  
		startActivity(intent);
	}
}
