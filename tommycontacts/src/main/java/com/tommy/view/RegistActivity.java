package com.tommy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.tommy.R;
import com.tommy.util.AbToastUtil;
import com.tommy.util.MyCount;
import com.tommy.util.RegexUtil;
/**import com.tommy.R;
 * 用户注册
 * 
 * @author meiyajun 2015-4-14
 */
public class RegistActivity extends Activity implements OnClickListener {
	private EditText et_phone_num, et_pwd, et_code;
	private Button btn_getcode, btn_regist, btn_agreement;
	private CheckBox cb_agreement;

	private MyCount mc;

	private String mobile, pwd, verifyCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_register);
		initView();
		initClick();
	}

	private void initClick() {
		btn_agreement.setOnClickListener(this);
		btn_regist.setOnClickListener(this);
		btn_getcode.setOnClickListener(this);

	}

	private void initView() {
		et_phone_num = (EditText) findViewById(R.id.et_userregister_phoneNum);
		et_pwd = (EditText) findViewById(R.id.et_userregister_pwd);
		et_code = (EditText) findViewById(R.id.et_userregister_code);
		btn_agreement = (Button) findViewById(R.id.btn_userregister_agreement);
		btn_regist = (Button) findViewById(R.id.btn_userregister_register);
		btn_getcode = (Button) findViewById(R.id.btn_userregister_code);

		cb_agreement = (CheckBox) findViewById(R.id.cb_userregister_agreement);

		/**
		 * 用户服务协议按钮
		 */
		// 设置下划线
		btn_agreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		// 设置加粗
		btn_agreement.getPaint().setFakeBoldText(true);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 用户协议
		case R.id.btn_userregister_agreement:
			Intent agreement = new Intent(this, UserAgreementActivity.class);
			startActivity(agreement);
			overridePendingTransition(R.anim.slide_top_to_bottom, R.anim.alpha_action);
			break;
		// 注册按钮
		case R.id.btn_userregister_register:
			registFunction();
			break;
		// 获取验证码
		case R.id.btn_userregister_code:
			getCode();
			break;
		default:
			break;
		}

	}

	private void registFunction() {
		if (!dataValidation()) {
			return;
		} else if (verifyCode.isEmpty()) {
			// 短信验证码不能为空
			AbToastUtil.shortShow(this, R.string.verify_code_empty);
		} else if (verifyCode.length() != 6) {
			// 请输入6位短信验证码
			AbToastUtil.shortShow(this, R.string.verify_code_length);
		} else if (!cb_agreement.isChecked()) {
			// 用户服务协议未选
			AbToastUtil.shortShow(this, R.string.user_agreement_uncheck);
		} else {
			Editor editor = getSharedPreferences("user", Context.MODE_WORLD_READABLE).edit();
			editor.putString("mima", et_pwd.getText().toString().trim()).commit();
			Intent intent = new Intent(RegistActivity.this, SplashActivity.class);
			startActivity(intent);
			this.finish();
		}
	}

	private void getCode() {
		if (!dataValidation()) {
			return;
		} else {
			// 30秒倒计时
			mc = new MyCount(1000 * 60, 1000, btn_getcode);
			mc.start();
			// 注册获取短信验证码
		}

	}

	// 填写的数据进行校验
	private boolean dataValidation() {
		mobile = et_phone_num.getText().toString().trim();
		pwd = et_pwd.getText().toString().trim();
		verifyCode = et_code.getText().toString().trim();
		if (mobile.isEmpty()) {
			// 注册手机号不能为空
			AbToastUtil.shortShow(this, R.string.mobile_empty);
			return false;
		} else if (mobile.length() != 11 | !RegexUtil.isMobileNumber(mobile)) {
			// 请输入11位有效手机号码
			AbToastUtil.shortShow(this, R.string.mobile_length);
			return false;
		} else if (pwd.isEmpty()) {
			// 注册密码不能为空
			AbToastUtil.shortShow(this, R.string.password_empty);
			return false;
		} else if (pwd.length() < 6) {
			// 请输入6至18位密码
			AbToastUtil.shortShow(this, R.string.password_length);
			return false;
		}
		return true;
	}

}
