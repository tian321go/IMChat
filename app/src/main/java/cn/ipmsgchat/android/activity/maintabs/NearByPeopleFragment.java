package cn.ipmsgchat.android.activity.maintabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ipmsgchat.android.BaseApplication;
import cn.ipmsgchat.android.BaseFragment;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.activity.message.ChatActivity;
import cn.ipmsgchat.android.adapter.NearByPeopleAdapter;
import cn.ipmsgchat.android.entity.Users;
import cn.ipmsgchat.android.view.MoMoRefreshListView;
import cn.ipmsgchat.android.view.MoMoRefreshListView.OnCancelListener;
import cn.ipmsgchat.android.view.MoMoRefreshListView.OnRefreshListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class NearByPeopleFragment extends BaseFragment implements OnItemClickListener,
        OnRefreshListener, OnCancelListener {

    private static List<Users> mNearByPeoples; // 在线用户列表

    private MoMoRefreshListView mMmrlvList;
    private NearByPeopleAdapter mAdapter;
    private TextView mTvListEmpty;

    public NearByPeopleFragment() {
        super();
    }

    public NearByPeopleFragment(BaseApplication application, Activity activity, Context context) {
        super(application, activity, context);
    }

    @Override
    public View
            onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_nearbypeople, container, false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initViews() {
        mMmrlvList = (MoMoRefreshListView) findViewById(R.id.nearby_people_mmrlv_list);
        mTvListEmpty = (TextView) findViewById(R.id.nearby_people_mmrlv_empty);
    }

    @Override
    protected void initEvents() {
        mMmrlvList.setOnItemClickListener(this);
        mMmrlvList.setOnRefreshListener(this);
        mMmrlvList.setOnCancelListener(this);
        mMmrlvList.setEmptyView(mTvListEmpty);
    }

    @Override
    protected void init() {
        getPeoples();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        int position = (int) arg3;
        Users people = mNearByPeoples.get(position);
        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Users.ENTITY_PEOPLE, people);
        startActivity(intent);
    }

    @Override
    public void onCancel() {
        clearAsyncTask();
        mMmrlvList.onRefreshComplete();
    }

    /**
     * 将用户表HashMap转成ArrayList 以便加载ListView Adapter
     *
     */
    private void initMaptoList() {
        HashMap<String, Users> mMap = mApplication.getOnlineUserMap();
        mNearByPeoples = new ArrayList<Users>(mMap.size());
        for (Map.Entry<String, Users> entry : mMap.entrySet()) {
            mNearByPeoples.add(entry.getValue());
        }
    }

    /** 刷新用户在线列表UI **/
    public void refreshAdapter() {
        mAdapter.setData(mNearByPeoples); // Adapter加载List数据
        mAdapter.notifyDataSetChanged();
    }

    /** 设置显示起始位置 **/
    public void setLvSelection(int position) {
        mMmrlvList.setSelection(position);
    }

    /** 获取在线用户 */
    private void getPeoples() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.dialog_loading));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                initMaptoList();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    mAdapter = new NearByPeopleAdapter(mApplication, mContext, mNearByPeoples);
                    mMmrlvList.setAdapter(mAdapter);
                }
                else {
                    showCustomToast(getString(R.string.dialog_loading_failue));
                }
            }

        });

    }

    /**
     * 下拉刷新数据处理
     *
     * @see cn.ipmsgchat.android.view.MoMoRefreshListView.OnRefreshListener#onRefresh()
     */
    @Override
    public void onRefresh() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    Thread.sleep(1000); // 停顿1S
                    if (mApplication.getOnlineUserMap().isEmpty()) {
                        return false;
                    }
                    initMaptoList();
                    return true;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mMmrlvList.onRefreshComplete();
                refreshAdapter();
                setLvSelection(0);
                super.onPostExecute(result);
            }
        });
    }

    public void onManualRefresh() {
        mMmrlvList.onManualRefresh();
    }
}
