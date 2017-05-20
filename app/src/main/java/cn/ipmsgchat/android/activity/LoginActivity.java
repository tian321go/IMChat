package cn.ipmsgchat.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import cn.ipmsgchat.android.BaseActivity;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.activity.maintabs.MainTabActivity;
import cn.ipmsgchat.android.entity.Users;
import cn.ipmsgchat.android.socket.udp.UDPSocketThread;
import cn.ipmsgchat.android.sql.SqlDBOperate;
import cn.ipmsgchat.android.sql.UserInfo;
import cn.ipmsgchat.android.util.DateUtils;
import cn.ipmsgchat.android.util.ImageUtils;
import cn.ipmsgchat.android.util.SessionUtils;
import cn.ipmsgchat.android.util.TextUtils;
import cn.ipmsgchat.android.util.WifiUtils;
import cn.ipmsgchat.android.view.HeaderLayout;
import cn.ipmsgchat.android.view.PagerScrollView;

public class LoginActivity extends BaseActivity implements OnClickListener{



    private String mDevice = getPhoneModel(); // 手机品牌型号
    private UserInfo mUserInfo; // 用户信息类实例
    private SqlDBOperate mSqlDBOperate;// 数据库操作实例,新
    private String localIPaddress; // 本地WifiIP

    private HeaderLayout mHeaderLayout;
    private PagerScrollView mLlayoutMain; // 首次登陆主界面
    private EditText mEtNickname;

    private LinearLayout mLlayoutExMain; // 二次登陆页面
    private ImageView mImgExAvatar;
    private TextView mTvExNickmame;
    private LinearLayout mLayoutExGender; // 性别根布局
    private ImageView mIvExGender;
    private TextView mTvExLogintime; // 上次登录时间

    private Button mBtnNext;
    private Button mBtnChangeUser;
    private RadioGroup mRgGender;
    private TelephonyManager mTelephonyManager;

    private int mAvatar;
    private String mGender;
    private String mIMEI;
    private String mLastLogintime; // 上次登录时间
    private String mNickname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWifiUtils = WifiUtils.getInstance(this);
        localIPaddress = mWifiUtils.getLocalIPAddress();
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.login_header);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle("登录", null);

        mEtNickname = (EditText) findViewById(R.id.login_et_nickname);
        mRgGender = (RadioGroup) findViewById(R.id.login_baseinfo_rg_gender);

        mBtnNext = (Button) findViewById(R.id.login_btn_next);
        mBtnChangeUser = (Button) findViewById(R.id.login_btn_changeUser);

        SharedPreferences mSharedPreferences = getSharedPreferences(GlobalSharedName,
                Context.MODE_PRIVATE);
        mNickname = mSharedPreferences.getString(Users.NICKNAME, "");

        // 若mNickname有内容，则读取本地存储的用户信息
        if (mNickname.length() != 0) {
            mTvExNickmame = (TextView) findViewById(R.id.login_tv_existName);
            mImgExAvatar = (ImageView) findViewById(R.id.login_img_existImg);
            mLayoutExGender = (LinearLayout) findViewById(R.id.login_layout_gender);
            mIvExGender = (ImageView) findViewById(R.id.login_iv_gender);
            mTvExLogintime = (TextView) findViewById(R.id.login_tv_lastlogintime);
            mLlayoutExMain = (LinearLayout) findViewById(R.id.login_linearlayout_existmain);
            mLlayoutMain = (PagerScrollView) findViewById(R.id.login_linearlayout_main);
            mLlayoutMain.setVisibility(View.GONE);
            mLlayoutExMain.setVisibility(View.VISIBLE);

            mAvatar = mSharedPreferences.getInt(Users.AVATAR, 0);
            mGender = mSharedPreferences.getString(Users.GENDER, "获取失败");
            mLastLogintime = mSharedPreferences.getString(Users.LOGINTIME, "获取失败");

            mImgExAvatar.setImageBitmap(ImageUtils.getAvatar(mApplication, this, Users.AVATAR
                    + mAvatar));
            mTvExNickmame.setText(mNickname);
            mTvExLogintime.setText(DateUtils.getBetweentime(mLastLogintime));
            if ("女".equals(mGender)) {
                mIvExGender.setBackgroundResource(R.drawable.ic_user_famale);
                mLayoutExGender.setBackgroundResource(R.drawable.bg_gender_famal);
            }
            else {
                mIvExGender.setBackgroundResource(R.drawable.ic_user_male);
                mLayoutExGender.setBackgroundResource(R.drawable.bg_gender_male);
            }
        }
    }

    @Override
    protected void initEvents() {
        mBtnNext.setOnClickListener(this);
        mBtnChangeUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // 更换用户,清空数据
            case R.id.login_btn_changeUser:
                mNickname = "";
                mGender = null;
                mIMEI = null;
                mAvatar = 0;
                SessionUtils.clearSession(); // 清空Session数据
                mLlayoutMain.setVisibility(View.VISIBLE);
                mLlayoutExMain.setVisibility(View.GONE);
                break;


            case R.id.login_btn_next:
                doLoginNext();
                break;
        }
    }



    /**
     * 登录资料完整性验证
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
            case R.id.login_baseinfo_rb_female:
                mGender = "女";
                break;
            case R.id.login_baseinfo_rb_male:
                mGender = "男";
                break;
            default:
                showShortToast(R.string.login_toast_sex);
                return false;
        }

        mNickname = mEtNickname.getText().toString().trim(); // 获取昵称
        mAvatar = (int) (Math.random() * 12 + 1); // 获取头像编号
        return true;
    }

    /**
     * 执行下一步跳转
     * 同时获取客户端的IMIE信息
     */
    private void doLoginNext() {
        if (mNickname.length() == 0) {
            if ((!isValidated())) {
                return;
            }
        }
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingDialog(getString(R.string.login_dialog_saveInfo));
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mIMEI = mTelephonyManager.getDeviceId(); // 获取IMEI

                    // 设置用户Session信息
                    SessionUtils.setIMEI(mIMEI);
                    SessionUtils.setNickname(mNickname);
                    SessionUtils.setGender(mGender);
                    SessionUtils.setAvatar(mAvatar);
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
                    doLogin();
                }
                else {
                    showShortToast(R.string.login_toast_loginfailue);
                }
            }
        });
    }

    /** 执行登陆 **/
    private void doLogin() {

        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mSqlDBOperate = new SqlDBOperate(mContext);
                    String IMEI = SessionUtils.getIMEI();
                    String nickname = SessionUtils.getNickname();
                    String gender = SessionUtils.getGender();
                    int avatar = SessionUtils.getAvatar();
                    String logintime = DateUtils.getNowtime();

                    // 录入数据库
                    // 若数据库中有IMEI对应的用户记录，则更新此记录; 无则创建新用户
                    if ((mUserInfo = mSqlDBOperate.getUserInfoByIMEI(IMEI)) != null) {
                        mUserInfo.setIPAddr(localIPaddress);
                        mUserInfo.setAvater(avatar);
                        mUserInfo.setName(nickname);
                        mUserInfo.setSex(gender);
                        mUserInfo.setDevice(mDevice);
                        mUserInfo.setLastDate(logintime);
                        mSqlDBOperate.updateUserInfo(mUserInfo);
                    }
                    else {
                        mUserInfo = new UserInfo(nickname, gender, IMEI, localIPaddress, avatar);
                        mUserInfo.setLastDate(logintime);
                        mUserInfo.setDevice(mDevice);
                        mSqlDBOperate.addUserInfo(mUserInfo);
                    }

                    int usserID = mSqlDBOperate.getIDByIMEI(IMEI); // 获取用户id
                    // 设置用户Session
                    SessionUtils.setLocalUserID(usserID);
                    SessionUtils.setDevice(mDevice);
                    SessionUtils.setLocalIPaddress(localIPaddress);
                    SessionUtils.setLoginTime(logintime);

                    // 在SD卡中存储登陆信息
                    SharedPreferences.Editor mEditor = getSharedPreferences(GlobalSharedName,
                            Context.MODE_PRIVATE).edit();
                    mEditor.putString(Users.IMEI, IMEI).putString(Users.DEVICE, mDevice)
                            .putString(Users.NICKNAME, nickname).putString(Users.GENDER, gender)
                            .putInt(Users.AVATAR, avatar)
                            .putString(Users.LOGINTIME, logintime);
                    mEditor.commit();

                    // UDPThread
                    mUDPSocketThread = UDPSocketThread.getInstance(mApplication,
                            getApplicationContext());
                    mUDPSocketThread.connectUDPSocket(); // 新建Socket线程
                    mUDPSocketThread.notifyOnline(); // 发送上线广播

                    return true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if (null != mSqlDBOperate) {
                        mSqlDBOperate.close();
                        mSqlDBOperate = null;
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                dismissLoadingDialog();
                if (result) {
                    startActivity(MainTabActivity.class);
                    finish();
                }
                else {
                    showShortToast("操作失败,请检查网络是否正常。");
                }
            }
        });
    }
    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }

    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        str2 = str1 + "_" + str2;
        return str2;
    }
}
