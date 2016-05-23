package com.example.phonecatch;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MobilePhone {
	private Context context;
	private boolean isCalling = false;

	public MobilePhone(Context context) {
		this.context = context;
	}

	public boolean isCalling() {
		return isCalling;
	}

	public void setCalling(boolean isCalling) {
		this.isCalling = isCalling;
	}

	public void dial(String number) {
		ITelephony iTelephony = getITelephony();
		Method dial;
		try {
			dial = iTelephony.getClass().getDeclaredMethod("dial", String.class);
			dial.invoke(iTelephony, number);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void call(String number) {

		ITelephony iTelephony = getITelephony();

		try {
			Method dial = iTelephony.getClass().getDeclaredMethod("call", String.class);
			dial.invoke(iTelephony, number);
			setCalling(true);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void endCall() {
		try {
			getITelephony().endCall();
			setCalling(false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ITelephony getITelephony() {

		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		ITelephony iTelephony = null;

		try {
			getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			iTelephony = (ITelephony) getITelephonyMethod.invoke(tManager, (Object[]) null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iTelephony;
	}
}
