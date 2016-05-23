package com.tommy.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tommy.R;
import com.tommy.util.AbToastUtil;
import com.tommy.util.RegexUtil;

public class LoginAct extends Activity implements OnClickListener {
	private EditText et_user_name, et_user_pwd;
	private Button btn_login;

	public String Mobile;
	public String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_login);
		initView();
		initClick();
	}

	private void initClick() {

		btn_login.setOnClickListener(this);

	}

	private void initView() {
		et_user_name = (EditText) findViewById(R.id.et_userlogin_username);
		et_user_pwd = (EditText) findViewById(R.id.et_userlogin_password);
		btn_login = (Button) findViewById(R.id.btn_userlogin_login);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_userlogin_login:
			// 用户登录
			btnLogin();
			break;
		default:
			break;
		}

	}

	private void btnLogin() {
		Mobile = et_user_name.getText().toString();
		password = et_user_pwd.getText().toString();
		if (Mobile.isEmpty()) {
			// 手机号不能为空
			AbToastUtil.shortShow(this, R.string.mobile_empty);
		} else if (Mobile.length() != 11 || !RegexUtil.isMobileNumber(Mobile)) {
			// 请输入11位有效手机号码
			AbToastUtil.shortShow(this, R.string.mobile_length);
		} else if (password.isEmpty()) {
			// 密码不能为空
			AbToastUtil.shortShow(this, R.string.password_empty);
		} else if (password.length() < 6) {
			// 请输入6至18位密码
			AbToastUtil.shortShow(this, R.string.password_length);
		} else {

		}

	}

}
