package cn.ipmsgchat.android.activity.maintabs;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.ipmsgchat.android.BaseApplication;
import cn.ipmsgchat.android.BaseDialog;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.activity.SettingMyInfoActivity;
import cn.ipmsgchat.android.sql.SqlDBOperate;
import cn.ipmsgchat.android.util.ActivityCollectorUtils;
import cn.ipmsgchat.android.util.FileUtils;
import cn.ipmsgchat.android.view.HeaderLayout;
import cn.ipmsgchat.android.view.SettingSwitchButton;

public class SettingActivity extends TabItemActivity implements OnClickListener,
        OnCheckedChangeListener, DialogInterface.OnClickListener {

    private HeaderLayout mHeaderLayout;

    private Button mDeleteAllChattingInfoButton;
    private Button mExitApplicationButton;

    private ImageView mSettingInfoButton;
    private SettingSwitchButton mSoundSwitchButton;
    private SettingSwitchButton mVibrateSwitchButton;
    private RelativeLayout mSettingInfoLayoutButton;

    private BaseDialog mDeleteCacheDialog; // 提示窗口
    private BaseDialog mExitDialog;
    private SqlDBOperate mSqlDBOperate;

    private int mDialogFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settting_page);
        initViews();
        initEvents();
        init();
    }

    @Override
    protected void initViews() {
        // TODO Auto-generated method stub
        mHeaderLayout = (HeaderLayout) findViewById(R.id.setting_page_header);
        mHeaderLayout.init(HeaderLayout.HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(getString(R.string.maintab_text_setting), null);

        mSettingInfoButton = (ImageView) findViewById(R.id.btn_setting_my_information);
        mSettingInfoLayoutButton = (RelativeLayout) findViewById(R.id.setting_my_info_layout);
        mSoundSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_sound);
        mVibrateSwitchButton = (SettingSwitchButton) findViewById(R.id.checkbox_vibration);
        mDeleteAllChattingInfoButton = (Button) findViewById(R.id.btn_delete_all_chattinginfo);
        mExitApplicationButton = (Button) findViewById(R.id.btn_exit_application);
    }

    @Override
    protected void initEvents() {
        // TODO Auto-generated method stub
        mSettingInfoButton.setOnClickListener(this);
        mSettingInfoLayoutButton.setOnClickListener(this);
        mSoundSwitchButton.setOnCheckedChangeListener(this);
        mVibrateSwitchButton.setOnCheckedChangeListener(this);
        mDeleteAllChattingInfoButton.setOnClickListener(this);
        mExitApplicationButton.setOnClickListener(this);

    }

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        mDeleteCacheDialog = BaseDialog.getDialog(SettingActivity.this, R.string.dialog_tips,
                getString(R.string.setting_dialog_chatlog_delete_confirm),
                getString(R.string.setting_dialog_chatlog_delete_ok), this,
                getString(R.string.setting_dialog_chatlog_delete_cancel), this);
        mDeleteCacheDialog.setButton1Background(R.drawable.btn_default_popsubmit);

        mExitDialog = BaseDialog.getDialog(SettingActivity.this, R.string.dialog_tips,
                getString(R.string.setting_dialog_logout_confirm),
                getString(R.string.setting_dialog_logout_ok), this,
                getString(R.string.setting_dialog_logout_cancel), this);
        mExitDialog.setButton1Background(R.drawable.btn_default_popsubmit);

        mSoundSwitchButton.setChecked(BaseApplication.getSoundFlag());
        mVibrateSwitchButton.setChecked(BaseApplication.getVibrateFlag());
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

        // case R.id.btn_setting_my_information:
            case R.id.setting_my_info_layout:
                startActivity(SettingMyInfoActivity.class);
                break;

            case R.id.btn_delete_all_chattinginfo:
                mDialogFlag = 1;
                mDeleteCacheDialog.show();
                break;

            case R.id.btn_exit_application:
                mDialogFlag = 2;
                mExitDialog.show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        switch (buttonView.getId()) {
            case R.id.checkbox_sound:
                buttonView.setChecked(isChecked);
                BaseApplication.setSoundFlag(isChecked);
                break;

            case R.id.checkbox_vibration:
                buttonView.setChecked(isChecked);
                BaseApplication.setVibrateFlag(isChecked);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

        switch (mDialogFlag) {
            case 1:
                if (which == 0) {
                    setAsyncTask(1);
                }
                else if (which == 1) {
                    mDeleteCacheDialog.dismiss();
                }
                break;
            case 2:
                if (which == 0) {
                    setAsyncTask(2);
                }
                else if (which == 1) {
                    mExitDialog.dismiss();
                }
                break;
        }
    }

    private void setAsyncTask(final int flag) {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                switch (flag) {
                    case 1:
                        mDeleteCacheDialog.dismiss();
                        showLoadingDialog(getString(R.string.setting_dialog_chatlog_deleting));
                        break;
                    case 2:
                        mExitDialog.dismiss();
                        showLoadingDialog(getString(R.string.setting_dialog_logout_confirm));
                        break;
                    default:
                        break;
                }

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    switch (flag) {
                        case 1:
                            mSqlDBOperate = new SqlDBOperate(SettingActivity.this);
                            mSqlDBOperate.deteleAllChattingInfo(); // 删除所有聊天记录
                            mSqlDBOperate.close();
                            mApplication.clearMsgCache();
                            mApplication.clearUnReadMessages();
                            FileUtils.delAllFile(BaseApplication.SAVE_PATH);
                            break;

                        case 2:
                            break;

                        default:
                            break;
                    }
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
                    dismissLoadingDialog();
                    switch (flag) {
                        case 1:
                            showShortToast(R.string.setting_dialog_toast_delect_success);
                            break;

                        case 2:
                            ActivityCollectorUtils.finishAllActivities(mApplication,
                                    getApplicationContext());
                            break;

                        default:
                            break;
                    }

                }
                else {
                    showShortToast(R.string.setting_dialog_toast_delect_failue);
                }
            }
        });
    }

    @Override
    public void processMessage(Message msg) {
        // TODO Auto-generated method stub

    }
}
