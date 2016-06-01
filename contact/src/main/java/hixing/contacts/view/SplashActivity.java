package hixing.contacts.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class SplashActivity extends Activity {
	private static final String TAG = SplashActivity.class.getSimpleName();
	private String mPwdText;
//	private String mNameText;
	private boolean isFirstRun;
//	private EditText uName;
	private EditText uPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 判断是否首次运行
		PropertiesUtil.intializePreference(SplashActivity.this);
		isFirstRun = PropertiesUtil.read(PropertiesUtil.ISFIRST,true);
		if(isFirstRun){
			BaseIntentUtil.intentDIY(SplashActivity.this,RegistActivity.class);
			finish();
			return;
		}
		showLogin();
	}

	private void showLogin() {

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(24,10,24,10);
//		uName = new EditText(this);
//		uName.setHint(R.string.mobile_ask);
//		uName.setInputType(InputType.TYPE_CLASS_PHONE);
		uPwd = new EditText(this);
		uPwd.setHint(R.string.prompt_password);
		uPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		uPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//		linearLayout.addView(uName);
		linearLayout.addView(uPwd);


		final AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("登录")
				.setView(linearLayout)
				.setCancelable(false)
				.setPositiveButton("确定",null)
				.setNeutralButton("忘记密码？",null)
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
//				BaseIntentUtil.intentDIY(SplashActivity.this,RegistActivity.class);
				AbToastUtil.longShow(SplashActivity.this,"呵呵，重新安装软件吧！");
			}
		});
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				String name1 = uName.getText().toString();
				String mima = uPwd.getText().toString();
				if(TextUtils.isEmpty(mima)){
					AbToastUtil.shortShow(SplashActivity.this, "密码不能为空！");
					return;
				}
				/*if(TextUtils.isEmpty(name1)){
					AbToastUtil.shortShow(SplashActivity.this, "用户名不能为空！");
					return;
				}
				&&mNameText.equals(name1)
				*/
				if (mPwdText.equals(mima)) {
					AbToastUtil.longShow(SplashActivity.this, "登录成功！正在进入...");
					alertDialog.dismiss();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							enterHomeActivity();
						}
					}, 1000);
				} else {
					AbToastUtil.shortShow(SplashActivity.this, "密码错误！");
				}
			}
		});

//		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getUser();
//		setUser(uName,uPwd);
	}
	private void getUser(){
		if(TextUtils.isEmpty(mPwdText)){
			mPwdText = PropertiesUtil.read(PropertiesUtil.USER_PWD, "");
		}
//		if(TextUtils.isEmpty(mNameText)){
//			mNameText = PropertiesUtil.read(PropertiesUtil.USER_PHONE, "");
//		}

		Log.e(TAG, "onStart: Pwd:"+mPwdText );
//		Log.e(TAG, "onStart: mNameText:"+mNameText );
	}
//	private void setUser(EditText tv_name,EditText tv_pwd){
//		if(tv_name==null || tv_pwd==null){
//			return;
//		}
//		if(!TextUtils.isEmpty(mNameText)){
//			tv_name.setText(mNameText);
//		}
//		if(TextUtils.isEmpty(mPwdText)){
//			tv_pwd.setText(mPwdText);
//		}
//	}
	private void enterHomeActivity() {
		BaseIntentUtil.intentDIY(SplashActivity.this,HomeTabHostAcitivity.class);
		finish();
	}
}
