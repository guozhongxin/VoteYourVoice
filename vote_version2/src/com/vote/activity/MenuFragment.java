package com.vote.activity;

import java.util.ArrayList;

import com.example.vote4.R;
import com.vote.pojo.Group;
import com.vote.utils.DBUtils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.view.Menu;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuFragment extends Fragment implements OnItemClickListener {
    
	private DBUtils db = new DBUtils();
	private String groups = null;
	private String[] children = null;
	private ArrayList<Group> alist = null;
	
	public static int currentGroupId = 0;
	/*private TextView view1 ;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private static String[] m;*/
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_sliding_menu, null);
    	view.getBackground().setAlpha(85);
    	initView(view);
        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void initView(View view) {
		SharedPreferences userInfo = getActivity().getSharedPreferences("user_info", 0);  
        int userid = userInfo.getInt("id", 0);  
        alist = db.getMyGroups(userid);
        String[] str = new String[alist.size()];
        for (int i=0; i<alist.size(); i++) {
        	Group group =  alist.get(i);
        	str[i] = group.getGroupname();
        }
        String[][] strr = {str,{}};
       
        /*m=str;
        view1 = (TextView) view.findViewById(R.id.spinnerText);
		spinner = (Spinner) view.findViewById(R.id.Spinner01);
		//将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,android.R.id.text1,m);
		
		//设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//将adapter 添加到spinner中
		spinner.setAdapter(adapter);
		
		//添加事件Spinner事件监听  
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		
		//设置默认值
		spinner.setVisibility(View.VISIBLE);*/


        ExpandableListView elv = (ExpandableListView) view.findViewById(R.id.list);
        elv.setAdapter(new groupListAdapter(strr));
        
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Nothing here ever fires
                System.err.println("child clicked");
                Toast.makeText(getActivity().getApplicationContext(), groupPosition+"", Toast.LENGTH_SHORT).show();
                // togroup(childPosition);
                
        		Group item =  alist.get(childPosition);
        		currentGroupId = Integer.parseInt(String.valueOf(item.getId()));
                Fragment newContent = null;
        		newContent = new VoteListFragment();
        		if (newContent != null)
        			switchFragment(childPosition+10, newContent);
                
                //
                return true;
            }
        });
               
        //菜单列表
        String[] menus = getResources().getStringArray(R.array.menus);
        for(int i=0; i<menus.length; i++) {
        	Log.e("menu", menus[i]);
        }
        
		ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, android.R.id.text1, menus);
        
		ListView lv = (ListView) view.findViewById(R.id.menulist);
		lv.setAdapter(menuAdapter);
		lv.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		// TODO Auto-generated method stub
		Fragment newContent = null;
		// MainActivity act = new MainActivity();
		switch (position) {
			case 0:
				newContent = new OpenVotesFragment();
				break;
			case 1:
				newContent = new ComingSoonFragment();
				break;
			case 2:
				logout();
				break;
			case 3:
				newContent = new GropeSearchFragment();//GropeSearchFragment
				break;
		}
		if (newContent != null)
			switchFragment(position, newContent);
	}

	private void switchFragment(int clicked, Fragment fragment) {
		if (getActivity() == null)
			return;
		
		if (getActivity() instanceof MainActivity) {
			MainActivity act = (MainActivity) getActivity();
			act.switchContent(clicked, fragment);
		} 
	}
	
	public void logout() {
		SharedPreferences userInfo = getActivity().getApplicationContext().getSharedPreferences("user_info", 0);  
        userInfo.edit().clear().commit();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
	}
	/* 
	public void togroup(int group) {
		Group item =  alist.get(group);
		int groupid = Integer.parseInt(String.valueOf(item.getId()));
		Intent intent = new Intent(getActivity(), VoteListActivity.class);
		Bundle bundle = new Bundle();    
		bundle.putString("groupid", groupid+"");
		intent.putExtras(bundle);  
		startActivity(intent);
    }
	*/
    public class groupListAdapter extends BaseExpandableListAdapter {
    	 
        private String[] groups = {"      我的小组"};     // 下拉菜单的标题
        private String[][] children = null;
 
        public groupListAdapter(String[][] children) {
            this.children = children;
        }
        
        @Override
        public int getGroupCount() {
            return groups.length;
        }
 
        @Override
        public int getChildrenCount(int i) {
            return children[i].length;
        }
 
        @Override
        public Object getGroup(int i) {
            return groups[i];
        }
 
        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }
 
        @Override
        public long getGroupId(int i) {
            return i;
        }
 
        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }
 
        @Override
        public boolean hasStableIds() {
            return true;
        }
 
        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(MenuFragment.this.getActivity());
            textView.setText(getGroup(i).toString());
            textView.setTextSize(18);
            textView.setTextColor(0xFF010101);
            textView.setHeight(70);
            textView.setGravity(20);
            return textView;
        }
 
        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(MenuFragment.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            textView.setTextSize(18);
            textView.setTextColor(0xFF010101);
            textView.setHeight(70);
            textView.setGravity(20);
            return textView;
        }
 
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
    /*
	class SpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
				//view1.setText(m[arg2]);
				togroup(arg2);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}*/
}