package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户管理类，用于处理用户登录状态和用户信息
 * 使用SharedPreferences存储用户信息
 */
public class UserManager {
    
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_USERS = "users"; // 存储所有用户名
    private static final String KEY_CURRENT_USER = "current_user"; // 当前登录用户
    private static final String PASSWORD_PREFIX = "password_"; // 密码前缀
    
    private SharedPreferences sharedPreferences;
    private static UserManager instance;
    
    private UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 获取UserManager单例
     */
    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @return true表示注册成功，false表示用户名已存在
     */
    public boolean register(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        
        // 获取所有用户名集合（需要创建新的HashSet，因为getStringSet返回的是不可变的集合）
        Set<String> users = new HashSet<>(sharedPreferences.getStringSet(KEY_USERS, new HashSet<>()));
        
        // 检查用户名是否已存在
        if (users.contains(username)) {
            return false;
        }
        
        // 添加新用户
        users.add(username);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_USERS, users);
        editor.putString(PASSWORD_PREFIX + username, password);
        editor.apply();
        
        return true;
    }
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return true表示验证成功，false表示用户名或密码错误
     */
    public boolean login(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        
        // 检查用户名是否存在
        Set<String> users = new HashSet<>(sharedPreferences.getStringSet(KEY_USERS, new HashSet<>()));
        if (!users.contains(username)) {
            return false;
        }
        
        // 验证密码
        String storedPassword = sharedPreferences.getString(PASSWORD_PREFIX + username, "");
        if (password.equals(storedPassword)) {
            // 保存当前登录用户
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_CURRENT_USER, username);
            editor.apply();
            return true;
        }
        
        return false;
    }
    
    /**
     * 获取当前登录的用户名
     * @return 当前登录的用户名，如果未登录则返回空字符串
     */
    public String getCurrentUsername() {
        return sharedPreferences.getString(KEY_CURRENT_USER, "");
    }
    
    /**
     * 退出登录
     */
    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_CURRENT_USER);
        editor.apply();
    }
    
    /**
     * 检查用户是否已登录
     * @return true表示已登录，false表示未登录
     */
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getCurrentUsername());
    }
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true表示存在，false表示不存在
     */
    public boolean userExists(String username) {
        Set<String> users = new HashSet<>(sharedPreferences.getStringSet(KEY_USERS, new HashSet<>()));
        return users.contains(username);
    }
    
    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return true表示修改成功，false表示旧密码错误或用户不存在
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            return false;
        }
        
        // 验证旧密码
        String storedPassword = sharedPreferences.getString(PASSWORD_PREFIX + username, "");
        if (!oldPassword.equals(storedPassword)) {
            return false;
        }
        
        // 更新密码
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PASSWORD_PREFIX + username, newPassword);
        editor.apply();
        
        return true;
    }
}