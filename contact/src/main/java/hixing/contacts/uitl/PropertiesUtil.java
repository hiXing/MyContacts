package hixing.contacts.uitl;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ZHX .
 * Date from 2016/5/9 .
 */
public class PropertiesUtil {
    public static final String ISFIRST = "isFirst";
    public static final String USER_ID = "user.id";
    public static final String USER_NAME= "user.name";
    public static final String USER_EMAIL= "user.email";
    public static final String USER_AGE= "user.age";
    public static final String USER_NIKENAME= "user.nikname";
    public static final String USER_SEX= "user.sex";
    public static final String USER_BIRTHYDAY= "user.birthday";
    public static final String USER_FACE= "user.face";
    public static final String USER_PHONE= "user.phone";
    public static final String USER_PWD= "user.pwd";


    private static final String NAME = "user";

    private static SharedPreferences mUserPreferences;

    public static void clearData(){
        if(mUserPreferences!=null) {
            SharedPreferences.Editor editor = mUserPreferences.edit();
            if(editor != null)
                editor.clear();
            mUserPreferences = null;
        }
    }
    public static void intializePreference(Context context) {
        if (mUserPreferences != null) {
            return;
        }
        mUserPreferences = context.getSharedPreferences(NAME, 0);
    }

    public static void save(String key, String value) {
        SharedPreferences.Editor editor = mUserPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, int value) {
        SharedPreferences.Editor editor = mUserPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(String key, boolean value) {
        SharedPreferences.Editor editor = mUserPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, long value) {
        SharedPreferences.Editor editor = mUserPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void save(String key, Float value) {
        SharedPreferences.Editor editor = mUserPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static String read(String key, String defaultvalue) {
        return mUserPreferences.getString(key, defaultvalue);
    }

    public static int read(String key, int defaultvalue) {
        return mUserPreferences.getInt(key, defaultvalue);
    }

    public static long read(String key, long defaultvalue) {
        return mUserPreferences.getLong(key, defaultvalue);
    }

    public static float read(String key, float defaultvalue) {
        return mUserPreferences.getFloat(key, defaultvalue);
    }


    public static boolean read(String key, boolean defaultvalue) {
        return mUserPreferences.getBoolean(key, defaultvalue);
    }

}
