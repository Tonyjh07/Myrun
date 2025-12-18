package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private static UserManager instance;
    private SharedPreferences sharedPreferences;

    // 单例模式
    private UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 登录验证
     * @param username 用户名
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean loginUser(String username, String password) {
        // 这里可以添加真实的登录验证逻辑
        // 暂时简化处理，只要用户名和密码不为空就认为登录成功
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            // 保存登录状态
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USERNAME, username);
            editor.apply();
            return true;
        }
        return false;
    }

    /**
     * 退出登录
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putString(KEY_USERNAME, null);
        editor.apply();
    }

    /**
     * 检查是否已登录
     * @return 是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 获取当前登录的用户名
     * @return 用户名
     */
    public String getCurrentUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    /**
     * 获取当前登录的用户名（别名）
     * @return 用户名
     */
    public String getUsername() {
        return getCurrentUsername();
    }

    /**
     * 退出登录（别名）
     */
    public void logoutUser() {
        logout();
    }
}