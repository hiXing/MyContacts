package hixing.contacts.application;

import java.util.List;

import hixing.contacts.bean.AppeSession;
import hixing.contacts.bean.ContactBean;
import hixing.contacts.service.T9Service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

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
		System.out.println("项目启动");
		appSession = new AppeSession();
		Intent startService = new Intent(MyApplication.this, T9Service.class);
		startService(startService);
	}
	public AppeSession getAppSession() {
		return appSession;
	}

	public void setAppSession(AppeSession appSession) {
		this.appSession = appSession;
	}

	public void promptExit(final Context context) {
		AlertDialog.Builder ab = new AlertDialog.Builder(context).setTitle("退出");
		ab.setPositiveButton("退出", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				exitApp(context);
			}
		});
		ab.setNegativeButton("取消", null);
		ab.show();
	}

	public void exitApp(Context context) {
		((Activity) context).finish();
	}
}
