package com.example.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.example.phonecatch.MobilePhone;
import com.example.phonecatch.NumberListUtil;
import com.tommy.util.GetNumberList;
import com.tommy.util.NotifyUtil;

public class PhoneStatReceiver extends BroadcastReceiver{  
	  
    String TAG = "tag";  
    TelephonyManager telMgr;
    private MobilePhone mp;
     
	 private SharedPreferences sharedPreferences;
    @Override  
    public void onReceive(Context context, Intent intent) { 
    	mp=new MobilePhone(context);
    	sharedPreferences= context.getSharedPreferences("mycontact", Context.MODE_PRIVATE);
        telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);  
        //获得电话的拦截状态
		String dianhua=sharedPreferences.getString("dianhua", "无");
		if(dianhua.equals("强")){
			mp.endCall();//挂断电话
			//拦截消息
			NotifyUtil.notify(context, "拦截电话", "拦截电话", "有一个电话被拦截了！");
		}
		else if(dianhua.equals("无")){
			return;
		}
               
        switch (telMgr.getCallState()) {  
              
            case TelephonyManager.CALL_STATE_RINGING:  
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);                
            	if(dianhua.equals("弱")){
    				if(!NumberListUtil.getPhoneNum(context).contains(number)){
    					mp.endCall();
    					//拦截消息
    					NotifyUtil.notify(context, "拦截电话", "拦截电话:"+number, "有一个电话被拦截了！");
    				}		
    			}
            	else if(dianhua.equals("自定义")){
            		if(GetNumberList.getNumberList(context).contains(number)){
            			mp.endCall();
            			//拦截消息
    					NotifyUtil.notify(context, "拦截电话", "拦截电话:"+number, "有一个电话被拦截了！");
            		}
            	}
                break;  
            case TelephonyManager.CALL_STATE_OFFHOOK:                                 
                break;  
            case TelephonyManager.CALL_STATE_IDLE:                                 
                break;  
        }  
          
    }  
}