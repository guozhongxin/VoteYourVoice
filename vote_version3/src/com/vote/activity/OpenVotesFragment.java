package com.vote.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import com.example.vote4.R;
import com.vote.pojo.Vote;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class OpenVotesFragment extends Fragment implements 
	OnItemClickListener, OnClickListener {
	
	private DBUtils db = new DBUtils();
	private GetNetworkState networkstate = new GetNetworkState();
	private int voteid;
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
	private ArrayList<Vote> voteList = new ArrayList<Vote>();

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String hint = null;
			switch (msg.what) {
			case 1:
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				break;
			case 2:
				initView(rootView);
				break;
			case 3:
				hint = "没有网络~";
				showToast(hint);
				initView(rootView);
				break;
			case 4:
				hint = "没有网络~";
				listView.onRefreshComplete();
				showToast(hint);
				break;
			}
		}
	}; 
	
	private Runnable getGroupsRun = new Runnable() {
		@Override
		public void run() {
			if (!checkNetwork()) {
	            Message message=new Message();  
	            message.what = 3;  
	            handler.sendMessage(message);  
				return ;
			}
 
			voteList = db.getOpenVotes();
            Message message = new Message();  
            message.what = 2;  
            handler.sendMessage(message);  
		}
		
	};
	
	private Runnable refreshRun = new Runnable() {
		@Override
		public void run() {
			if (!checkNetwork()) {
	            Message message = new Message();  
	            message.what = 4;  
	            handler.sendMessage(message);  
				return ;
			}
			 
            voteList = db.getOpenVotes();
            adapter.loadData(voteList);
            Message message = new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
		}
	};
	
    public void setActionBar() {
    	menu.clear();
    	//inflater.inflate(R.menu.add_group, menu);
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		new Thread(getGroupsRun).start();
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
		}
        return true;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_openvotes, container, false);
		
		loading = (LinearLayout) rootView.findViewById(R.id.loading);
		return rootView;
	}
	
	public void initView(View view) {
		loading.setVisibility(View.GONE);
		
		listView = (PullToRefreshListView) view.findViewById(R.id.lv);
		
        listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Thread(refreshRun).start();
			}
		});

		adapter = new PullToRefreshListViewAdapter(voteList){};
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position < 0) {
			return ;
		}

		Vote vote = voteList.get(position);
		voteid = vote.getId();
		Intent intent = new Intent(getActivity().getApplicationContext(), VoteActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putInt("voteid", voteid);
		intent.putExtras(bundle);  
		startActivity(intent);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		
		}
	}
	
	public void showToast(String hint) {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), hint, Toast.LENGTH_SHORT);  
    	toast.show();
	}
	
	@SuppressWarnings("static-access")
	public boolean checkNetwork() {
		return (networkstate.getAPNType(getActivity().getApplicationContext()) > 0) ? true : false;
	}
	
	public abstract class PullToRefreshListViewAdapter extends BaseAdapter {

		private ArrayList<Vote> votes = new ArrayList<Vote>();;
        
		public PullToRefreshListViewAdapter(ArrayList<Vote> votes) {
            this.votes = votes;
        }
		
		public void loadData(ArrayList<Vote> votes) {
			this.votes = votes;
		}
		public class ViewHolder {
			public String id;
			public TextView votename;
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
			LayoutInflater inflater = getActivity().getLayoutInflater();
			ViewHolder viewHolder = new ViewHolder();

			if (convertView == null){
				rowView = inflater.inflate(R.layout.listview_item_openvote,null);
				viewHolder.votename = (TextView) rowView.findViewById(R.id.votename);
				rowView.setTag(viewHolder);
			}

			final ViewHolder holder = (ViewHolder) rowView.getTag();

			holder.votename.setText(vote.getVotename()); 
			return rowView;
		}
	}
}