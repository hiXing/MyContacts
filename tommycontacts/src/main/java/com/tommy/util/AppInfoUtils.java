package com.tommy.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.tommy.R;


public class AppInfoUtils {

	/**
	 * 获取版本号名称
	 * 
	 */
	public static String getVersionName(Context ctx) {
		String currVer = null;
		try {
			PackageManager manager = ctx.getPackageManager();
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			currVer = info.versionName;
		} catch (Exception e) {
			currVer = "";
			e.printStackTrace();
		}
		return "V" + currVer;
	}

	public static String getAppName(Context ctx) {
		return ctx.getString(R.string.app_name);
	}

	public static String getSvnVersionName(Context ctx) {
		return ctx.getString(R.string.svn_version);
	}

}
