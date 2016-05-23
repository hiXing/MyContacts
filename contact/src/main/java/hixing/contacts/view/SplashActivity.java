package hixing.contacts.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import hixing.contacts.R;
import hixing.contacts.uitl.AbToastUtil;
import hixing.contacts.uitl.BaseIntentUtil;
import hixing.contacts.uitl.PropertiesUtil;
import hixing.contacts.view.other.MyConstant;


public class SplashActivity extends Activity {
	private static final String TAG = SplashActivity.class.getSimpleName();
	private String mPwdText;
	private String mNameText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 判断是否首次运行
		PropertiesUtil.intializePreference(SplashActivity.this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPwdText = PropertiesUtil.read(PropertiesUtil.USER_PWD, "");
		mNameText = PropertiesUtil.read(PropertiesUtil.USER_PHONE, "");

		Log.e(TAG, "onStart: Pwd:"+mPwdText );
		Log.e(TAG, "onStart: mNameText:"+mNameText );
//		if(isFirstRun){
//			BaseIntentUtil.intentDIY(SplashActivity.this,RegistActivity.class);
//			finish();
//			return;
//		}else {
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(24,10,24,10);
		final EditText uName = new EditText(this);
		uName.setHint(R.string.mobile_ask);
		uName.setInputType(InputType.TYPE_CLASS_PHONE);
		final EditText uPwd = new EditText(this);
		uPwd.setHint(R.string.prompt_password);
		uPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		uPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		linearLayout.addView(uName);
		linearLayout.addView(uPwd);

		if(!TextUtils.isEmpty(mNameText)){
			uName.setText(mNameText);
		}
		if(TextUtils.isEmpty(mPwdText)){
			uPwd.setText(mPwdText);
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("登录")
				.setView(linearLayout)
				.setCancelable(false)
				.setPositiveButton("确定",null)
				.setNeutralButton("去注册",null)
				.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						SplashActivity.this.finish();
					}
				})
				.create();
		alertDialog.show();

		alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseIntentUtil.intentDIY(SplashActivity.this,RegistActivity.class);
			}
		});
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name1 = uName.getText().toString();
				String mima = uPwd.getText().toString();
				if(TextUtils.isEmpty(name1)||TextUtils.isEmpty(mima)){
					AbToastUtil.shortShow(SplashActivity.this, "用户名或密码不能为空！");
					return;
				}
				if (mPwdText.equals(mima)&&mNameText.equals(name1)) {
					AbToastUtil.shortShow(SplashActivity.this, "登录成功！");
					alertDialog.cancel();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							enterHomeActivity();
						}
					}, 1500);
				} else {
					AbToastUtil.shortShow(SplashActivity.this, "用户名或密码错误！");
				}
			}
		});

//		}
	}

	private void enterHomeActivity() {
		BaseIntentUtil.intentDIY(SplashActivity.this,HomeTabHostAcitivity.class);
		finish();
	}
}
