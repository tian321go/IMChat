package cn.ipmsgchat.android.activity.maintabs;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;

import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.view.HeaderLayout;

public class SessionListActivity extends TabItemActivity {

    private HeaderLayout mHeaderLayout;
    private SessionPeopleFragment mPeopleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessionlist);
        initViews();
        init();
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        MainTabActivity.sendEmptyMessage();
    }    
    
    @Override
    public void onResume(){
        super.onResume();
        MainTabActivity.sendEmptyMessage();
        mPeopleFragment.refreshAdapter();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.session_header);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(getString(R.string.maintab_text_message), null);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected void init() {
        mPeopleFragment = new SessionPeopleFragment(mApplication, this, this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.session_layout_content, mPeopleFragment).commit();
    }

    @Override
    public void processMessage(Message msg) {
        mPeopleFragment.refreshAdapter();
    }

}
