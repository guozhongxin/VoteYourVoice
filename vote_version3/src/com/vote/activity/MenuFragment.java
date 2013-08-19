package com.vote.activity;
import com.example.vote4.R;
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
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuFragment extends Fragment implements OnItemClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_sliding_menu, null);
    	initView(view);
        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	public void initView(View view) {
        //²Ëµ¥ÁÐ±í
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
		switch (position) {
		case 0:
			newContent = new MyGroupsFragment();
			break;
		case 1:
			newContent = new OpenVotesFragment();
			break;
		case 2:
			logout();
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
}