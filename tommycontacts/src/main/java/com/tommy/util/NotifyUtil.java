package com.tommy.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tommy.R;
import com.tommy.view.HomeContactActivity;
public class NotifyUtil {

	public static void notify(Context context, String mtickerText, String mcontentTitle, String mcontentText) {
		// 定义NotificationManager

		String ns = Context.NOTIFICATION_SERVICE;

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		// 定义通知栏展现的内容信息

		int icon = R.drawable.ic_launcher;

		CharSequence tickerText = mtickerText;

		long when = System.currentTimeMillis();


		// 定义下拉通知栏时要展现的内容信息

		Context mcontext = context.getApplicationContext();

		CharSequence contentTitle = mcontentTitle;

		CharSequence contentText = mcontentText;

		Intent notificationIntent = new Intent(context, HomeContactActivity.class);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		Notification.Builder builder = new Notification.Builder(mcontext);
		Notification notification  = builder.setContentTitle(contentTitle)
				.setContentText(contentText)
				.setSmallIcon(icon)
				.setTicker(tickerText)
				.setWhen(when)
				.setContentIntent(contentIntent)
				.build();

		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知

		mNotificationManager.notify(1, notification);
	}
}
