package cn.ipmsgchat.android.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cn.ipmsgchat.android.BaseActivity;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.entity.Users;
import cn.ipmsgchat.android.socket.udp.UDPSocketThread;
import cn.ipmsgchat.android.util.ImageUtils;
import cn.ipmsgchat.android.util.SessionUtils;
import cn.ipmsgchat.android.util.TextUtils;
import cn.ipmsgchat.android.view.HeaderLayout;

public class SettingMyInfoActivity extends BaseActivity implements OnClickListener {

    private static final int REQUEST_CODE = 1;

    private HeaderLayout mHeaderLayout;

    private EditText mEtNickname;
    private ImageView mIvAvater;

    private RadioGroup mRgGender;
    private RadioButton mRbGirl;
    private RadioButton mRbBoy;
    private Button mBtnBack;
    private Button mBtnNext;

    private int mAvatar;
    private String mGender;
    // private String mLastLogintime; // 上次登录时间
    private String mNickname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        initViews();
        initData();
        initEvents();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.my_information_page_header);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("编辑个人资料", null);

        mIvAvater = (ImageView) findViewById(R.id.setting_my_avater_img);
        mEtNickname = (EditText) findViewById(R.id.setting_my_nickname);
        mRgGender = (RadioGroup) findViewById(R.id.setting_baseinfo_rg_gender);

        mRbBoy = (RadioButton) findViewById(R.id.setting_baseinfo_rb_male);
        mRbGirl = (RadioButton) findViewById(R.id.setting_baseinfo_rb_female);

        mBtnBack = (Button) findViewById(R.id.setting_btn_back);
        mBtnNext = (Button) findViewById(R.id.setting_btn_next);

    }

    @Override
    protected void initEvents() {
        mBtnBack.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mIvAvater.setOnClickListener(this);
    }

    private void initData() {
        mAvatar = SessionUtils.getAvatar();
        mGender = SessionUtils.getGender();
        // mLastLogintime = SessionUtils.getLoginTime(); // 上次登录时间


        if (mGender.equals("女")) {
            mRbGirl.setChecked(true);
        }
        else {
            mRbBoy.setChecked(true);
        }

        mIvAvater.setImageBitmap(ImageUtils.getAvatar(mApplication, this,
                Users.AVATAR + mAvatar));
        mEtNickname.setText(SessionUtils.getNickname());
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.setting_btn_back:
                finish();
                break;

            case R.id.setting_btn_next:
                doNext();
                break;
            case R.id.setting_my_avater_img:
                Intent intent = new Intent(this, ChooseAvatarActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }

    }

    /**
     * 登录资料完整性验证，不完整则无法登陆，完整则记录输入的信息。
     * 
     * @return boolean 返回是否为完整， 完整(true),不完整(false)
     */
    private boolean isValidated() {
        mNickname = "";
        mGender = null;
        if (TextUtils.isNull(mEtNickname)) {
            showShortToast(R.string.login_toast_nickname);
            mEtNickname.requestFocus();
            return false;
        }

        switch (mRgGender.getCheckedRadioButtonId()) {
            case R.id.setting_baseinfo_rb_female:
                mGender = "女";
                break;
            case R.id.setting_baseinfo_rb_male:
                mGender = "男";
                break;
            default:
                showShortToast(R.string.login_toast_sex);
                return false;
        }

        mNickname = mEtNickname.getText().toString().trim(); // 获取昵称
        return true;
    }

    private void doNext() {
        if ((!isValidated())) {
            return;
        }
        setAsyncTask();

    }

    private void setAsyncTask() {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.login_dialog_saveInfo));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {                  

                    // 设置用户Session信息
                    SessionUtils.setNickname(mNickname);
                    SessionUtils.setGender(mGender);
                    SessionUtils.setAvatar(mAvatar);

                    // 在SD卡中存储登陆信息
                    SharedPreferences.Editor mEditor = getSharedPreferences(GlobalSharedName,
                            Context.MODE_PRIVATE).edit();
                    mEditor.putString(Users.NICKNAME, mNickname)
                            .putString(Users.GENDER, mGender)
                            .putInt(Users.AVATAR, mAvatar);
                    mEditor.commit();
                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    UDPSocketThread.getInstance(mApplication, SettingMyInfoActivity.this)
                            .notifyOnline();
                    finish();
                }
                else {
                    showShortToast("操作失败,请尝试重启程序。");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int result = data.getExtras().getInt("result");

                mAvatar = result + 1;
                mIvAvater.setImageBitmap(ImageUtils.getAvatar(mApplication, this,
                        Users.AVATAR + mAvatar));
            }
        }

    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
