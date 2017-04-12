package com.dmitrii.sbertest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

/**
 * Created by Dmitrii on 12.04.2017.
 */

public class MainPrefs {
    public static final String PREFS_NAME = "MainPrefs";

    public static final String KEY_LIST   = "KEY_LIST";


    public static final String KEY_VALUTE_FROM = "KEY_VALUTE_FROM";
    public static String getSelectedValuteFrom(@NonNull Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(KEY_VALUTE_FROM, "USD");
    }

    public static boolean saveSelectedValuteFrom(@NonNull Context context, String code) {
        return context.getSharedPreferences(PREFS_NAME, 0).edit().putString(KEY_VALUTE_FROM, code).commit();
    }

    public static final String KEY_VALUTE_TO = "KEY_VALUTE_TO";
    public static String getSelectedValuteTo(@NonNull Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0).getString(KEY_VALUTE_TO, "USD");
    }

    public static boolean saveSelectedValuteTo(@NonNull Context context, String code) {
        return context.getSharedPreferences(PREFS_NAME, 0).edit().putString(KEY_VALUTE_TO, code).commit();
    }


    public static boolean saveValutesList(@NonNull Context context, CbrXmlParser.ValuteList valuteList) {
        Parcel parcel = Parcel.obtain();
        valuteList.writeToParcel(parcel, 0);
        String str = Base64.encodeToString(parcel.marshall(), Base64.NO_WRAP);
        return context.getSharedPreferences(PREFS_NAME, 0).edit().putString(KEY_LIST, str).commit();
    }

    public static CbrXmlParser.ValuteList getValutesList(@NonNull Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String str = prefs.getString(KEY_LIST, null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        byte[] bytes = Base64.decode(str, Base64.NO_WRAP);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        CbrXmlParser.ValuteList list = CbrXmlParser.ValuteList.CREATOR.createFromParcel(parcel);
        return list;
    }

}
