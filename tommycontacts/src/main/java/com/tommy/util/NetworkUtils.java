package com.tommy.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

/**
 * @Description 网络工具类
 */
public class NetworkUtils {
	/** 未知网络 **/
	public static final byte NETTYPE_UNKNOWN = 0x00;
	/** WIFI网络 **/
	public static final byte NETTYPE_WIFI = 0x01;
	/** wap接入 **/
	public static final byte NETTYPE_CMWAP = 0x02;
	/** net接入 **/
	public static final byte NETTYPE_CMNET = 0x03;

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gps是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnable(Context context) {
		LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 * Wifi是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE)
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否3G网络
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			return true;
		}
		return false;
	}

	/**
	 * 判断网络是否为漫游
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null && tm.isNetworkRoaming()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @param context
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NETTYPE_UNKNOWN;
		}
		int type = networkInfo.getType();
		if (type == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!TextUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (type == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 发送请求标识
	 * 
	 * @param context
	 * @return
	 */
	public static String getUserAgent(Context context) {
		String userAgent = "ashine";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return userAgent;
	}

}
