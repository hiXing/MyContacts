package com.tommy.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.pim.vcard.AppeSession;
import android.view.LayoutInflater;
import android.view.View;

import com.tommy.R;
import com.tommy.bean.ContactBean;
import com.tommy.service.T9Service;

import java.util.List;

public class MyApplication extends android.app.Application {

	private List<ContactBean> contactBeanList;
	private AppeSession appSession;

	public List<ContactBean> getContactBeanList() {
		return contactBeanList;
	}

	public void setContactBeanList(List<ContactBean> contactBeanList) {
		this.contactBeanList = contactBeanList;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appSession = new AppeSession();

		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);

		/*
		 * Intent observerintent=new
		 * Intent(MyApplication.this,ObserverService.class);
		 * startService(observerintent);//启动服务
		 */}

	public AppeSession getAppSession() {
		return appSession;
	}

	public void setAppSession(AppeSession appSession) {
		this.appSession = appSession;
	}

	public void promptExit(final Context context) {
		LayoutInflater li = LayoutInflater.from(context);
		View exitV = li.inflate(R.layout.dialog_exit, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setView(exitV);
		ab.setPositiveButton(R.string.exit, new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				exitApp(context);
			}
		});
		ab.setNegativeButton(R.string.cancle, null);
		ab.show();
	}

	public void exitApp(Context context) {
		((Activity) context).finish();
	}
}
