package com.example.phonecatch;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class NumberListUtil {

	public static ArrayList<String> getPhoneNum(Context context) {
		ArrayList<String> numList = new ArrayList<String>();

		ContentResolver cr = context.getContentResolver();

		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		while (cursor.moveToNext()) {

			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

			while (phone.moveToNext()) {
				String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				numList.add(strPhoneNumber);
				Log.v("tag", "strPhoneNumber:" + strPhoneNumber);
			}

			phone.close();
		}
		cursor.close();
		return numList;
	}

}
