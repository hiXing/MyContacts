package com.example.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.phonecatch.PhoneCallObserver;
import com.example.phonecatch.PhoneListener;

public class ObserverService extends Service{


	private final String TAG="ObserverService";

	private PhoneCallObserver phoneCallObserver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "----onCreate");
	
	//listen the phoneCall state
	TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  
	telephonyManager.listen(new PhoneListener(this), PhoneStateListener.LISTEN_CALL_STATE);
	
	//register phoneCallObserver
	phoneCallObserver=new PhoneCallObserver(new Handler(), this);
	getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, phoneCallObserver);
	
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "------onDestroy");
		getContentResolver().unregisterContentObserver(phoneCallObserver);
	}
	
	
	
	}
	
