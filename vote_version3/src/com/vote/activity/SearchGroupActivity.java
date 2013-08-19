package com.vote.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import com.example.vote4.R;
import com.vote.activity.SearchGroupActivity;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;
import com.vote.utils.GetNetworkState;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView; 
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchGroupActivity extends Activity implements OnItemClickListener {
	//get DBUtils
	private DBUtils db = new DBUtils();
	private GetNetworkState networkstate = new GetNetworkState();
	private int groupid;
	private String groupname;
	//Views
	private LinearLayout loading = null;
	private ListView lv;
	private TextView tv_noresult = null;
	
	private ArrayList<Group> groupList = null;
	
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
	
	private Thread searchGroup = new Thread() {
		@Override
	    public void run() {
			if (!checkNetwork()) {
				Message message=new Message();  
	            message.what = 2;  
	            handler.sendMessage(message);  
				return;
			}
			groupList = db.searchGroup(groupname);
			Message message=new Message();  
            message.what = 1;  
            handler.sendMessage(message);  
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_group);
		
		Bundle bundle = getIntent().getExtras();  
		groupname = bundle.getString("groupname");
		loading = (LinearLayout) findViewById(R.id.loading);
		searchGroup.start();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.create :
    		Intent intent = new Intent(this, CreateVoteActivity.class);
    		Bundle bundle = new Bundle();    
    		bundle.putString("groupid", groupid+"");
    		intent.putExtras(bundle);  
    		this.startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
    }
	
	public void initView() {
		tv_noresult = (TextView) findViewById(R.id.noresult);
		
        if (groupList.size() > 0) {
            lv = (ListView) findViewById(R.id.lv);
            GroupsAdapter adapter = new GroupsAdapter(groupList){};
            lv.setAdapter(adapter);				
    		lv.setOnItemClickListener(this);
    		tv_noresult.setVisibility(View.GONE);
        } else {
        	tv_noresult.setText("没有找到小组~");
        }

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Group group = (Group) arg0.getItemAtPosition(arg2);
		groupid = group.getId();
		Intent intent = new Intent(this, GroupInfoActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putInt("groupid", groupid);
		intent.putExtras(bundle);  
		startActivity(intent);
	}
    
    public abstract class GroupsAdapter extends BaseAdapter {
    	private ArrayList<Group> groups = new ArrayList<Group>();;
        
    	public GroupsAdapter(ArrayList<Group> groups) {
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
    		LayoutInflater inflater = SearchGroupActivity.this.getLayoutInflater();
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