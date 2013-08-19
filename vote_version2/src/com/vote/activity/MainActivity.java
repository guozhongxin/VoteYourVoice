package com.vote.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.vote4.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;

public class MainActivity extends SherlockFragmentActivity implements OnClosedListener,OnOpenedListener {

	private Fragment mContent;
	private int now = 0;
	private SlidingMenu slidingmenu;
	private Menu menu =  null;
	
	/**
	 * 菜单划出时，修改actionbar
	 */
	@Override
	public void onOpened() {
		menu.clear();
		setTitle("vote");
		getActionBar().setDisplayHomeAsUpEnabled(false);
	}

	/**
	 * 当菜单关闭时，根据当前fragment修改actionbar
	 */
	@Override
	public void onClosed() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	    Fragment frag = getSupportFragmentManager().findFragmentById(R.id.content_frame);
	    if (frag instanceof MyGroupsFragment) {
	    	setTitle("我的小组");
	        ((MyGroupsFragment) frag).setActionBar();
	    } else if (frag instanceof ComingSoonFragment) {
	    	setTitle("coming");
	    } else if (frag instanceof OpenVotesFragment) {
			setTitle("开放投票");
		} else if (frag instanceof GropeSearchFragment) {
			setTitle("搜索小组");
		}
	    
	   
	    //TODO 在这里添加其他菜单选项，修改对应frament的actionbar
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("开放投票");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initView(savedInstanceState);
	}

	private void initView(Bundle savedInstanceState){	
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new OpenVotesFragment();	
		
		// 设置主视图界面
		setContentView(R.layout.main_fragment_container);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		
		initSlidingMenu();
	}

	public void initSlidingMenu() {
		//滑动菜单
		slidingmenu = new SlidingMenu(this);
        slidingmenu.setMode(SlidingMenu.LEFT);
        slidingmenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingmenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingmenu.setShadowDrawable(R.drawable.shadow);
        slidingmenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingmenu.setFadeDegree(0.35f);
        slidingmenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingmenu.setMenu(R.layout.sliding_menu_container);
        
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuFragment()).commit();
        
        slidingmenu.setOnClosedListener(this);
        slidingmenu.setOnOpenedListener(this);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			slidingmenu.toggle();
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void switchContent(int clicked, Fragment fragment) {
		if (now == clicked) {
			slidingmenu.showContent();
		} else {
			now = clicked;
			mContent = fragment;
			slidingmenu.toggle();
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
}