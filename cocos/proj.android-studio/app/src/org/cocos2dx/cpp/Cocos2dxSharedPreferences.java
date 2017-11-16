package org.cocos2dx.cpp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.cocos2dx.lib.Cocos2dxHelper;

import java.util.Map;
import java.util.Set;

public class Cocos2dxSharedPreferences {
    public static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences(Cocos2dxHelper.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void saveValue(Context context, String key, Object value){
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        if(value instanceof String){
            editor.putString(key, (String) value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer) value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float) value);
        }else if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean) value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long) value);
        }else if(value instanceof Double){
            long val = Double.doubleToRawLongBits((Double) value);
            editor.putLong(key, val);
        }
        editor.commit();
    }

    public static void saveValues(Context context, Map<String, Object> keyValue) {
        for (Map.Entry<String, Object> e : keyValue.entrySet()) {
            saveValue(context, e.getKey(), e.getValue());
        }
    }

    public static void saveValues(Context context, Bundle keyValue){
        Set<String> keys = keyValue.keySet();
        for(String key : keys) {
            saveValue(context, key, keyValue.get(key));
        }
    }

    public static double getDouble(SharedPreferences sp, String key, double defaultValue){
        if(!sp.contains(key)){
            return defaultValue;
        }
        return Double.longBitsToDouble(sp.getLong(key, 0));
    }
}
