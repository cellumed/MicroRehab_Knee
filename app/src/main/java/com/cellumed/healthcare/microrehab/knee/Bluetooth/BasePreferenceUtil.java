package com.cellumed.healthcare.microrehab.knee.Bluetooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

public class BasePreferenceUtil {
    private static SharedPreferences preference;


    public static SharedPreferences instance() {
        if (preference == null)
            preference = PreferenceManager.getDefaultSharedPreferences(ContextUtil.CONTEXT);
        return preference;
    }


    public static SharedPreferences instance(Context $context) {
        ContextUtil.CONTEXT = $context;

        if (preference == null)
            preference = PreferenceManager.getDefaultSharedPreferences($context);
        return preference;
    }


    /**
     * key 수동 설정
     *
     * @param key   키 값
     * @param value 내용
     */
    public static void put(String key, String value) {
        SharedPreferences p = instance();
        SharedPreferences.Editor editor = p.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * String 값 가져오기
     *
     * @param key 키 값
     * @return String (기본값 null)
     */
    public static String get(String key) {
        SharedPreferences p = instance();
        return p.getString(key, null);
    }


    /**
     * String 값 가져오기
     *
     * @param key 키 값
     * @return String (기본값 "")
     */
    public static String getWithNullToBlank(String key) {
        SharedPreferences p = instance();
        return p.getString(key, "");
    }


    /**
     * key 설정
     *
     * @param key   키 값
     * @param value 내용
     */
    public static void put(String key, boolean value) {
        SharedPreferences p = instance();
        SharedPreferences.Editor editor = p.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    /**
     * Boolean 값 가져오기
     *
     * @param key 키 값
     * @param def 기본값
     * @return Boolean
     */
    public static boolean get(String key, boolean def) {
        SharedPreferences p = instance();
        return p.getBoolean(key, def);
    }


    /**
     * key 설정
     *
     * @param key   키 값
     * @param value 내용
     */
    public static void put(String key, int value) {
        SharedPreferences p = instance();
        SharedPreferences.Editor editor = p.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    /**
     * int 값 가져오기
     *
     * @param key 키 값
     * @param def 기본값
     * @return int
     */
    public static int get(String key, int def) {
        SharedPreferences p = instance();
        return p.getInt(key, def);
    }


    public static void put(String key, Set<String> set) {
        SharedPreferences p = instance();
        SharedPreferences.Editor editor = p.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }


    public static Set<String> getStringSet(String key) {
        SharedPreferences p = instance();
        return p.getStringSet(key, null);
    }
}