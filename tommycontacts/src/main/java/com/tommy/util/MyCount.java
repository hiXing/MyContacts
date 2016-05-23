package com.tommy.util;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 
 * 获取验证码时 按钮上倒计时
 * 
 * @author meiyajun 2015-4-15
 */
public class MyCount extends CountDownTimer {
	private long millisInFuture;
	private Button btn;

	public MyCount(long millisInFuture, long countDownInterval, Button btn) {
		super(millisInFuture, countDownInterval);
		this.millisInFuture = millisInFuture;
		this.btn = btn;
	}

	public MyCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		btn.setClickable(true);
		btn.setText("重试");
	}

	@Override
	public void onTick(long millisUntilFinished) {
		btn.setClickable(false);
		btn.setText(String.valueOf(millisUntilFinished / 1000) + "秒后重试");

	}
}