package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class MyActivity extends AppCompatActivity {

    private MaterialToolbar mToolbar;
    private TextView mTvUsername;
    private TextView mTvUserInfo;
    private TextView mTvTotalRuns;
    private TextView mTvTotalDistance;
    private TextView mTvTotalTime;
    private MaterialCardView mMenuSettings;
    private MaterialCardView mMenuAbout;
    private MaterialCardView mMenuLogout;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my);
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.my), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 初始化控件
        initViews();
        
        // 设置用户信息
        setupUserInfo();
        
        // 设置统计数据
        setupStatistics();
        
        // 设置工具栏
        setupToolbar();
        
        // 设置菜单点击事件
        setupMenuListeners();
    }
    
    /**
     * 初始化控件
     */
    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mTvUsername = findViewById(R.id.tv_username);
        mTvUserInfo = findViewById(R.id.tv_user_info);
        mTvTotalRuns = findViewById(R.id.tv_total_runs);
        mTvTotalDistance = findViewById(R.id.tv_total_distance);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        mMenuSettings = findViewById(R.id.menu_settings);
        mMenuAbout = findViewById(R.id.menu_about);
        mMenuLogout = findViewById(R.id.menu_logout);
    }
    
    /**
     * 设置用户信息
     */
    private void setupUserInfo() {
        String username = userManager.getCurrentUser();
        if (username != null) {
            mTvUsername.setText(username);
            mTvUserInfo.setText("跑步爱好者");
        } else {
            mTvUsername.setText("未登录");
            mTvUserInfo.setText("请先登录");
        }
    }
    
    /**
     * 设置统计数据
     * 这里使用模拟数据，实际应该从数据库或SharedPreferences中读取
     */
    private void setupStatistics() {
        // TODO: 从数据库或SharedPreferences中读取真实的统计数据
        // 目前使用模拟数据
        mTvTotalRuns.setText("0");
        mTvTotalDistance.setText("0.0");
        mTvTotalTime.setText("00:00");
    }
    
    /**
     * 设置工具栏
     */
    private void setupToolbar() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }
    
    /**
     * 设置菜单点击事件
     */
    private void setupMenuListeners() {
        // 设置菜单项点击事件
        if (mMenuSettings != null) {
            mMenuSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到设置页面
                    Intent settingsIntent = new Intent(MyActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                }
            });
        }
        
        if (mMenuAbout != null) {
            mMenuAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到关于页面
                    Intent aboutIntent = new Intent(MyActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                }
            });
        }
        
        if (mMenuLogout != null) {
            mMenuLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 显示退出登录确认对话框
                    showLogoutDialog();
                }
            });
        }
    }
    
    /**
     * 显示退出登录确认对话框
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 执行退出登录操作
     */
    private void logout() {
        // 调用UserManager退出登录
        userManager.logout();
        
        // 显示提示信息
        ToastUtil.showMsg(this, "已退出登录");
        
        // 跳转到登录页面，并清除Activity栈
        Intent intent = new Intent(MyActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}