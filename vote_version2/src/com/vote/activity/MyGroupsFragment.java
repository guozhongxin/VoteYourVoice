package com.vote.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.vote4.R;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
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

public class MyGroupsFragment extends SherlockFragment implements 
	OnItemClickListener, OnClickListener {
	
	private DBUtils db = new DBUtils();
	private int groupid;
	private int userid;
	public View rootView;
	
	//Views
	public ListView lv;
	public EditText et_groupname = null;
	public Button bt_search = null;
	private LinearLayout loading = null;
	
	private Menu menu = null;
	private MenuInflater inflater = null;
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private ArrayList<Group> groupList = new ArrayList<Group>();

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
	
	/**
	 * 设置actionbar 在这里修改我的小组fragment的actionbar
	 */
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
    	Log.d("menu", "创建组按钮");
    	setActionBar();
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_cg:
				Log.d("menu", "创建组");
				Intent intent = new Intent(getActivity().getApplicationContext(), CreateGroupActivity.class);
				startActivity(intent);
				break;
		}
        return true;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mygroups, container, false);
		
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
		//bt_search = (Button) view.findViewById(R.id.search);
		//bt_search.setOnClickListener(this);
		listView = (PullToRefreshListView) view.findViewById(R.id.lv);
		
        listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Thread() {
					@Override
				    public void run() {
			    		SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);  
			            userid = userInfo.getInt("id", 0);  
			            groupList = db.getMyGroups(userid);
			            adapter.loadData(groupList);
			            Message message = new Message();  
			            message.what = 1;  
			            handler.sendMessage(message);  
				    }
				}.start();
			}
		});

		adapter = new PullToRefreshListViewAdapter(groupList){};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
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
	
	public void deleteGroup() {
        if (db.deleteGroup(userid, groupid)) {
        	initView(rootView);
        	Toast.makeText(getActivity().getApplicationContext(), "delete successfully!", Toast.LENGTH_SHORT).show();  
        } else 
        	Toast.makeText(getActivity().getApplicationContext(), "failed!", Toast.LENGTH_SHORT).show();  
	}
	
	@Override
	public void onClick(View view) {
		/*switch (view.getId()) {
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
		}*/
	}
	
	public abstract class PullToRefreshListViewAdapter extends BaseAdapter {

		private ArrayList<Group> groups = new ArrayList<Group>();;
        
		public PullToRefreshListViewAdapter(ArrayList<Group> groups) {
            this.groups = groups;
        }
		
		public void loadData(ArrayList<Group> groups) {
			this.groups = groups;
		}
		public class ViewHolder {
			public String id;
			public TextView creator;
			public TextView groupname;
			public TextView description;
		}
		
		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			Group group = (Group) getItem(position);
			LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
			ViewHolder viewHolder = new ViewHolder();

			if (convertView == null){
				rowView = inflater.inflate(R.layout.listview_item_group,null);
				viewHolder.groupname = (TextView) rowView.findViewById(R.id.groupname);
				viewHolder.description = (TextView) rowView.findViewById(R.id.description);
				viewHolder.creator = (TextView) rowView.findViewById(R.id.creator);
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			holder.groupname.setText(group.getGroupname()); 
			holder.description.setText(group.getDescription()); 
			holder.creator.setText(group.getCreatorname()); 
			return rowView;
		}
	}
}