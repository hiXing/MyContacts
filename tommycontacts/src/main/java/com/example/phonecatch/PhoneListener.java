package com.example.phonecatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.pim.vcard.AppeSession;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tommy.application.MyApplication;
import com.tommy.util.NotifyUtil;

public class PhoneListener extends PhoneStateListener {
	private String incomeNumber; // 来电号码
	private Context context;
	private final String TAG = "PhoneListener";

	private SharedPreferences sharedPreferences;
	private MobilePhone mp;
	private AppeSession appSession;

	public PhoneListener(Context context) {
		super();
		this.context = context;
		mp = new MobilePhone(context);
		appSession = ((MyApplication) context.getApplicationContext()).getAppSession();
		sharedPreferences = context.getSharedPreferences("mycontact", Context.MODE_PRIVATE);
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) { // 获得电话的拦截状态
		String dianhua = this.sharedPreferences.getString("dianhua", "无");
		if (dianhua.equals("强")) {
			mp.endCall();// 挂断电话
			// 拦截消息
			NotifyUtil.notify(context, "拦截电话", "拦截电话", "有一个电话被拦截了！");
		} else if (dianhua.equals("无")) {
			return;
		}
		try {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING: // 来电
				incomeNumber = incomingNumber;
				if (dianhua.equals("弱")) {
					if (!NumberListUtil.getPhoneNum(context).contains(incomingNumber)) {
						mp.endCall();
						// 拦截消息
						NotifyUtil.notify(context, "拦截电话", "拦截电话", "有一个电话被拦截了！");
					}
				}
				Log.i(TAG, "LAI DIAN---->");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通电话

				break;
			case TelephonyManager.CALL_STATE_IDLE: // 挂掉电话

				break;

			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
