package com.tommy.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class GetNumberList {

public static List getNumberList(Context context){
	List<String> numberList=new ArrayList<String>();
	SharedPreferences sharedPreferences2=context.getSharedPreferences("numberlist",Context.MODE_PRIVATE);
	int size=sharedPreferences2.getInt("number", 0);
	for(int i=0;i<size;i++){
		String number=sharedPreferences2.getString("number"+i, "");
		numberList.add(number);
	}
	return numberList;
}

public static List getNumberduanxinList(Context context){
	List<String> numberList=new ArrayList<String>();
	SharedPreferences sharedPreferences2=context.getSharedPreferences("duanxinlist",Context.MODE_PRIVATE);
	int size=sharedPreferences2.getInt("duanxin", 0);
	for(int i=0;i<size;i++){
		String number=sharedPreferences2.getString("duanxin"+i, "");
		numberList.add(number);
	}
	return numberList;
}
}
