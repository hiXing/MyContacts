package com.tommy.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.EditText;

import com.tommy.util.AbToastUtil;
import com.tommy.R;
public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否设置密码
		SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
		boolean isMima = preferences.getBoolean("isMima", false);
		final String mima = preferences.getString("mima", "");
		if (TextUtils.isEmpty(mima)) {
			Intent intent = new Intent(this, RegistActivity.class);
			startActivity(intent);
			finish();
			return;
		} else {
			final EditText et = new EditText(this);
			new AlertDialog.Builder(this).setTitle("请输入密码").setIcon(android.R.drawable.ic_dialog_info).setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String mima1 = et.getText().toString();
					if (mima.equals(mima1)) {
						AbToastUtil.shortShow(SplashActivity.this, "密码正确！");
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								enterHomeActivity();
							}
						}, 2000);
					} else {
						AbToastUtil.shortShow(SplashActivity.this, "密码错误！");
						SplashActivity.this.finish();
					}

				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SplashActivity.this.finish();
				}
			}).setCancelable(false).show();

		}

		// 如果不是第一次启动app，则正常显示启动屏
		setContentView(R.layout.activity_splash);

	}

	private void enterHomeActivity() {
		Intent intent = new Intent(this, HomeTabHostAcitivity.class);
		startActivity(intent);
		finish();
	}
}
