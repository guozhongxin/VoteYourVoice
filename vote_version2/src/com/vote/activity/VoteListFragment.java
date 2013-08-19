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
import com.vote.activity.VoteListActivity.VotesAdapter;
import com.vote.activity.VoteListActivity.VotesAdapter.ViewHolder;
import com.vote.pojo.Group;
import com.vote.pojo.Vote;
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

public class VoteListFragment extends SherlockFragment implements 
	OnItemClickListener, OnClickListener {
	
	private DBUtils db = new DBUtils();
	private int groupid;
	public View rootView;
	
	
	private Menu menu = null;
	private MenuInflater inflater = null;
	private LinearLayout loading = null;
	private PullToRefreshListView listView;
	private PullToRefreshListViewAdapter adapter;
	private ArrayList<Vote> voteList = new ArrayList<Vote>();
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Log.d("groupid", "groupid: handleMessage  "+ groupid);
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				break;
			case 2:
				initView(rootView);
			}
		}
	};  
	/*
	private Thread getVoteListThread = new Thread() {
		@Override
	    public void run() {
			//Bundle bundle = getIntent().getExtras();                              //////////////////////////////!! !! !! !! !! !!
			//groupid = Integer.parseInt(bundle.getString("groupid"));  
			groupid = MenuFragment.currentGroupId;
			Log.d("groupid", "groupid: 赋值  "+ groupid);
			voteList = db.getGroupVotes(groupid);
            Message message=new Message();  
            message.what = 2;  
            handler.sendMessage(message);  
	    }
	};
	*/
	
	
	/**
	 * 设置actionbar 在这里修改我的小组fragment的actionbar
	 */

    public void setActionBar() {
    	menu.clear();
    	inflater.inflate(R.menu.add_vote, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_cv:
				Intent intent = new Intent(getActivity().getApplicationContext(), CreateVoteActivity.class);
				startActivity(intent);
				break;
		}
        return true;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("groupid", "groupid: onCreateView_S  "+ groupid);
		
		rootView = inflater.inflate(R.layout.fragment_votelist, container, false);
		
		loading = (LinearLayout) rootView.findViewById(R.id.loading);
		new Thread() {
			@Override
		    public void run() {
				groupid = MenuFragment.currentGroupId;
				Log.d("groupid", "groupid: 赋值  "+ groupid);

				voteList = db.getGroupVotes(groupid);     //  group id

	            
	            Message message=new Message();  
	            
	            message.what = 2;  
	            handler.sendMessage(message);  

		    }
		}.start();
		return rootView;
	}
	
	public void initView(View view) {
		
		Log.d("groupid", "groupid: initView_S  "+ groupid);
		loading.setVisibility(View.GONE);

		listView = (PullToRefreshListView) view.findViewById(R.id.lv);
		
        listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Thread() {
					@Override
				    public void run() {
						voteList = db.getGroupVotes(groupid);
			            adapter.loadData(voteList);                                        ///////////////// ! ! ! 
			            Message message=new Message();  
			            message.what = 1;  
			            handler.sendMessage(message);  
				    }
				}.start();
			}
			
		});

		adapter = new PullToRefreshListViewAdapter(voteList){};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		Log.d("groupid", "groupid: initView_E  "+ groupid);
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position < 0) {
			return ;
		}
		Vote vote = voteList.get(position);
		int voteid = vote.getId();
		Intent intent = new Intent(getActivity().getApplicationContext(), VoteActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putInt("voteid", voteid);
		intent.putExtras(bundle);  
		startActivity(intent);
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

		private ArrayList<Vote> votes = new ArrayList<Vote>();
        
		public PullToRefreshListViewAdapter(ArrayList<Vote> votes) {
            this.votes = votes;
        }
		
		public void loadData(ArrayList<Vote> votes) {
			this.votes = votes;
		}
		public class ViewHolder {
    		public String id;
    		public TextView votename;
    		public TextView groupname;
    		public TextView voteinfo;
		}
		
    	@Override
    	public int getCount() {
    		return votes.size();
    	}

    	@Override
    	public Object getItem(int position) {
    		return votes.get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		return position;
    	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
    		Vote vote = (Vote) getItem(position);
    		LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
    		ViewHolder viewHolder = new ViewHolder();

    		if (convertView == null){
    			rowView = inflater.inflate(R.layout.listview_item_vote,null);
    			viewHolder.groupname = (TextView) rowView.findViewById(R.id.groupname);
    			viewHolder.votename = (TextView) rowView.findViewById(R.id.votename);
    			viewHolder.voteinfo = (TextView) rowView.findViewById(R.id.voteinfo);
    			rowView.setTag(viewHolder);
    		}

    		final ViewHolder holder = (ViewHolder) rowView.getTag();

    		holder.groupname.setText(vote.getGroupname()); 
    		holder.votename.setText(vote.getVotename()); 
    		holder.voteinfo.setText(vote.getVoetinfo()); 
    		return rowView;
		}
	}
}