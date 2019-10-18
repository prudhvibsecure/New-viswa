package com.adi.exam.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.adi.exam.SriVishwa;
import com.adi.exam.utils.TraceUtils;

import java.util.Map;

public class AppPreferences {

    private SharedPreferences pref = null;

    private static AppPreferences appPref = null;

    public static AppPreferences getInstance(Context context) {

        if (appPref == null)

            appPref = new AppPreferences(context);

        return appPref;

    }

    private AppPreferences(Context context) {

        if (pref == null) {

            pref = context.getSharedPreferences("ooredoo", 0);

        }

    }

    public void addToStore(String key, String value, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        try {

            if (value.length() > 0) {

                value = AESEncryptionDecryption.encrypt(value);

            }

            /*if(key.equalsIgnoreCase("test::::::")) {
                TraceUtils.logE(key, value);
            }*/

        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        editor.putString(key, value);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public String getFromStore(String key) {

        String val = pref.getString(key, "");

        if (val.trim().length() > 0) {

            try {

                val = AESEncryptionDecryption.decrypt(val);

                /*if(key.equalsIgnoreCase("test::::::")) {
                    TraceUtils.logE(key, val);
                }*/

            } catch (Exception e) {
                TraceUtils.logException(e);
            }
        }

        return val;

    }

    public void addBooleanToStore(String key, boolean value, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public boolean getBooleanFromStore(String key) {
        return pref.getBoolean(key, false);
    }

    public void addFloatToStore(String key, float value, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.putFloat(key, value);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public float getFloatFromStore(String key) {
        return pref.getFloat(key, 0);
    }

    public void addIntegerToStore(String key, int value, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public int getIntegerFromStore(String key) {
        return pref.getInt(key, 0);
    }


    public long getLongFromStore(String key) {
        return pref.getLong(key, 0);
    }

    public void addLongToStore(String key, long value, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public void clearSharedPreferences(boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.clear();

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public void removeFromStore(String key, boolean mode) {

        SharedPreferences.Editor editor = pref.edit();

        editor.remove(key);

        if (mode) {

            editor.apply();

            return;

        }

        editor.commit();

    }

    public Map<String,?> getPref(){
        return pref.getAll();

    }

}
