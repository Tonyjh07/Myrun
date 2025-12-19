package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用户管理类，用于处理用户登录状态和用户信息
 */
public class UserManager {
    
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static UserManager instance;
    private SharedPreferences sharedPreferences;
    
    private UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }
    
    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        // 简单的验证逻辑，实际项目中应该连接服务器验证
        if ("wbh".equals(username) && "123456".equals(password)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, username);
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();
            return true;
        }
        return false;
    }
    
    /**
     * 用户注销
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_USERNAME);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
    
    /**
     * 检查用户是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * 获取当前登录的用户名
     */
    public String getCurrentUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
    
    /**
     * 用户注册
     */
    public boolean register(String username, String password) {
        // 简单的注册逻辑，实际项目中应该连接服务器
        // 这里只是模拟注册成功
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
        return true;
    }
}