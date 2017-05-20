package  cn.ipmsgchat.android.dialog;


import android.content.Context;

import cn.ipmsgchat.android.BaseDialog;
import cn.ipmsgchat.android.R;
import cn.ipmsgchat.android.view.FlippingImageView;
import cn.ipmsgchat.android.view.HandyTextView;


public class FlippingLoadingDialog extends BaseDialog {

	private FlippingImageView mFivIcon;
	private HandyTextView mHtvText;
	private String mText;

	public FlippingLoadingDialog(Context context, String text) {
		super(context);
		mText = text;
		init();
	}

	private void init() {
		setContentView(R.layout.common_flipping_loading_diloag);
		mFivIcon = (FlippingImageView) findViewById(R.id.loadingdialog_fiv_icon);
		mHtvText = (HandyTextView) findViewById(R.id.loadingdialog_htv_text);
		mFivIcon.startAnimation();
		mHtvText.setText(mText);
	}

	public void setText(String text) {
		mText = text;
		mHtvText.setText(mText);
	}

	@Override
	public void dismiss() {
		if (isShowing()) {
			super.dismiss();
		}
	}


}
