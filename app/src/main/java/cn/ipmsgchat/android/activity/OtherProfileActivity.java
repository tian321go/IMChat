package cn.ipmsgchat.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.ipmsgchat.android.BaseActivity;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.activity.message.ChatActivity;
import cn.ipmsgchat.android.entity.Users;
import cn.ipmsgchat.android.util.ImageUtils;
import cn.ipmsgchat.android.view.HandyTextView;
import cn.ipmsgchat.android.view.HeaderLayout;

public class OtherProfileActivity extends BaseActivity implements OnClickListener {

    private HeaderLayout mHeaderLayout;// 标题栏
    private LinearLayout mLayoutChat;// 对话
    private ImageView mIvAvatar; // 头像

    private LinearLayout mLayoutGender;// 性别根布局
    private ImageView mIvGender;// 性别
    private HandyTextView mHtvTime;// 登陆时间
    private HandyTextView mHtvIPaddress; // IP地址
    private HandyTextView mHtvDevice; // 设备品牌型号

    private Users mPeople;// 用户实体

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherprofile);
        initViews();
        initEvents();
        init();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.otherprofile_header);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mLayoutChat = (LinearLayout) findViewById(R.id.otherprofile_bottom_layout_chat);
        mIvAvatar = (ImageView) findViewById(R.id.header_iv_logo);

        mLayoutGender = (LinearLayout) findViewById(R.id.otherprofile_layout_gender);
        mIvGender = (ImageView) findViewById(R.id.otherprofile_iv_gender);
        mHtvTime = (HandyTextView) findViewById(R.id.otherprofile_htv_time);
        mHtvIPaddress = (HandyTextView) findViewById(R.id.otherprofile_htv_ipaddress);
        mHtvDevice = (HandyTextView) findViewById(R.id.otherprofile_htv_device);

    }

    @Override
    protected void initEvents() {
        mLayoutChat.setOnClickListener(this);
    }

    private void init() {
        getProfile();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(OtherProfileActivity.this, ChatActivity.class);
        intent.putExtra(Users.ENTITY_PEOPLE, mPeople);
        startActivity(intent);
        finish();
    }

    private void getProfile() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.dialog_loading));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Intent intent = getIntent();
                mPeople = intent.getParcelableExtra(Users.ENTITY_PEOPLE);
                if (mPeople == null) {
                    return false;
                }
                else {
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                dismissLoadingDialog();
                if (!result) {
                    showShortToast(R.string.dialog_loading_failue);
                }
                else {
                    mHeaderLayout.setDefaultTitle(mPeople.getNickname(), null);
                    mIvAvatar.setImageBitmap(ImageUtils.getAvatar(mApplication, mContext,
                            Users.AVATAR + mPeople.getAvatar()));
                    initProfile();
                }
            }

        });
    }

    private void initProfile() {
        mLayoutGender.setBackgroundResource(mPeople.getGenderBgId());
        mIvGender.setImageResource(mPeople.getGenderId());
        mHtvTime.setText(mPeople.getLogintime());
        mHtvIPaddress.setText(mPeople.getIpaddress());
        mHtvDevice.setText(mPeople.getDevice());
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
