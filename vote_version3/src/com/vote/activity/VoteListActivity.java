package com.vote.activity;

import java.util.ArrayList;
import com.example.vote4.R;
import com.vote.activity.VoteListActivity;
import com.vote.pojo.Vote;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.AdapterView; 
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VoteListActivity extends Activity implements OnItemClickListener {
	
	private DBUtils db = new DBUtils();
	private GetNetworkState networkstate = new GetNetworkState();
	private int groupid;
	
	private LinearLayout loading = null;
	private PullToRefreshListView listView;
	private VotesAdapter adapter;
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
				initView();
				break;
			case 3:
				hint = "没有网络~";
				showToast(hint);
				initView();
				break;
			case 4:
				listView.onRefreshComplete();
				hint = "没有网络~";
				showToast(hint);
				break;
			}
		}
	};  
	
	private Runnable getVoteListRum = new Runnable() {
		@Override
		public void run() {
			if (!checkNetwork()) {
	            Message message = new Message();  
	            message.what = 3;  
	            handler.sendMessage(message); 
				return ;
			}
			
			Bundle bundle = getIntent().getExtras();  
			groupid = Integer.parseInt(bundle.getString("groupid"));  
			voteList = db.getGroupVotes(groupid);
            Message message=new Message();  
            message.what = 2;  
            handler.sendMessage(message);  
		}
		
	};
	
	private Runnable refreshRun = new Runnable() {
		@Override
		public void run() {
			if (!checkNetwork()) {
	            Message message=new Message();  
	            message.what = 4;  
	            handler.sendMessage(message); 
				return ;
			}
			
			voteList = db.getGroupVotes(groupid);
            adapter.loadData(voteList);
            Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_votelist);

		loading = (LinearLayout) findViewById(R.id.loading);
		new Thread(getVoteListRum).start();
	}

	public void initView() {
		loading.setVisibility(View.GONE);
		
		listView = (PullToRefreshListView) findViewById(R.id.lv);
		adapter = new VotesAdapter(voteList){};
		listView.setAdapter(adapter);
        listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Thread(refreshRun).start();
			}
		});
        listView.setOnItemClickListener(this);	
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position < 0) {
			return ;
		}
		Vote vote = voteList.get(position);
		int voteid = vote.getId();
		Intent intent = new Intent(this, VoteActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putInt("voteid", voteid);
		intent.putExtras(bundle);  
		startActivity(intent);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getActionBar().setDisplayHomeAsUpEnabled(true);
    	getMenuInflater().inflate(R.menu.vote_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.create:
    		Intent intent = new Intent(this, CreateVoteActivity.class);
    		Bundle bundle = new Bundle();    
    		bundle.putString("groupid", groupid+"");
    		intent.putExtras(bundle);  
    		this.startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
    }
    
	public void showToast(String hint) {
		Toast toast = Toast.makeText(getApplicationContext(), hint, Toast.LENGTH_SHORT);  
    	toast.show();
	}
	
	@SuppressWarnings("static-access")
	public boolean checkNetwork() {
		return (networkstate.getAPNType(getApplicationContext()) > 0) ? true : false;
	}
    
    public abstract class VotesAdapter extends BaseAdapter {
    	private ArrayList<Vote> votes = new ArrayList<Vote>();;
        
    	public VotesAdapter(ArrayList<Vote> votes) {
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
    		LayoutInflater inflater = VoteListActivity.this.getLayoutInflater();
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