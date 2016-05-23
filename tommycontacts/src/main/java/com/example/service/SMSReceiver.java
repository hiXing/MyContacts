package com.example.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.phonecatch.NumberListUtil;
import com.tommy.util.GetNumberList;
import com.tommy.util.NotifyUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSReceiver extends BroadcastReceiver {

	private String TAG="SMSReceiver";
	
	 private SharedPreferences sharedPreferences;
    //广播消息类型
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    //覆盖onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	sharedPreferences= context.getSharedPreferences("mycontact", Context.MODE_PRIVATE);
    	//获得短信的拦截状态
    	String duanxin=sharedPreferences.getString("duanxin", "无");
    	if(duanxin.equals("强")){
    		//对于特定的内容,取消广播
            abortBroadcast();
			NotifyUtil.notify(context, "拦截短信", "拦截短信", "有一条短信被拦截了！");
		}
		else if(duanxin.equals("无")){
			return;
		}
        //先判断广播消息
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action))
        {
            //获取intent参数
            Bundle bundle=intent.getExtras();
            //判断bundle内容
            if (bundle!=null)
            {
                //取pdus内容,转换为Object[]
                Object[] pdus=(Object[])bundle.get("pdus");
                //解析短信
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i=0;i<messages.length;i++)
                {
                    byte[] pdu=(byte[])pdus[i];
                    messages[i]=SmsMessage.createFromPdu(pdu);
                }    
                //解析完内容后分析具体参数
                for(SmsMessage msg:messages)
                {
                    //获取短信内容
                    String content=msg.getMessageBody();
                    String sender=msg.getOriginatingAddress();
                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sendTime = sdf.format(date);
                    //TODO:根据条件判断,然后进一般处理
                    if(duanxin.equals("弱")){
        				if(!NumberListUtil.getPhoneNum(context).contains(sender)){
        					 abortBroadcast();
        					//拦截消息
        					NotifyUtil.notify(context, "拦截短信", "拦截短信:"+sender, "有一条短信被拦截了！");
        				}		
        			}
                	else if(duanxin.equals("自定义")){
                		if(GetNumberList.getNumberduanxinList(context).contains(sender)){
                			abortBroadcast();
                			//拦截消息
        					NotifyUtil.notify(context, "拦截短信", "拦截短信:"+sender, "有一条短信被拦截了！");
                		}
                	}
                }
                
            }
        }//if 判断广播消息结束
    }
}