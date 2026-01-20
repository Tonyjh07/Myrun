package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.RankingManager;
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
    private MaterialCardView mMenuRunRecords;
    private MaterialCardView mMenuSettings;
    private MaterialCardView mMenuAbout;
    private MaterialCardView mMenuLogout;
    private SlideMenu slideMenu;
    private MaterialCardView mSlideMenuSettings;
    private MaterialCardView mSlideMenuAbout;
    private MaterialCardView mSlideMenuLogout;
    private UserManager userManager;
    private RankingManager rankingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my);
        
        // 初始化UserManager和RankingManager
        userManager = UserManager.getInstance(this);
        rankingManager = RankingManager.getInstance(this);
        
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
        
        // 设置侧滑菜单监听器
        setupSlideMenu();
        
        // 设置返回键处理
        setupBackPressedHandler();
        
        // 添加空值检查，防止控件未找到导致的空指针异常
        if (mTvUsername == null || mTvUserInfo == null) {
            android.util.Log.e("MyActivity", "关键控件未找到，可能导致界面显示异常");
        }
    }
    
    /**
     * 初始化控件
     */
    private void initViews() {
        slideMenu = findViewById(R.id.slideMenu);
        mToolbar = findViewById(R.id.toolbar);
        mTvUsername = findViewById(R.id.tv_username);
        mTvUserInfo = findViewById(R.id.tv_user_info);
        mTvTotalRuns = findViewById(R.id.tv_total_runs);
        mTvTotalDistance = findViewById(R.id.tv_total_distance);
        mTvTotalTime = findViewById(R.id.tv_total_time);
        
        // 直接通过findViewById查找控件，避免通过子视图查找的问题
        mMenuRunRecords = findViewById(R.id.menu_run_records);
        mMenuSettings = findViewById(R.id.menu_settings);
        mMenuAbout = findViewById(R.id.menu_about);
        mMenuLogout = findViewById(R.id.menu_logout);
        
        // 侧边栏菜单项
        mSlideMenuSettings = findViewById(R.id.slide_menu_settings);
        mSlideMenuAbout = findViewById(R.id.slide_menu_about);
        mSlideMenuLogout = findViewById(R.id.slide_menu_logout);
    }
    
    /**
     * 设置用户信息
     */
    private void setupUserInfo() {
        if (mTvUsername != null && mTvUserInfo != null) {
            String username = userManager.getCurrentUsername();
            if (username != null && !username.isEmpty()) {
                mTvUsername.setText(username);
                mTvUserInfo.setText("跑步爱好者");
            } else {
                mTvUsername.setText("未登录");
                mTvUserInfo.setText("请先登录");
            }
        }
    }
    
    /**
     * 设置统计数据
     * 从RankingManager中读取真实的统计数据
     */
    private void setupStatistics() {
        if (userManager == null || rankingManager == null) {
            return;
        }
        
        String username = userManager.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            // 未登录，显示默认值
            if (mTvTotalRuns != null) {
                mTvTotalRuns.setText("0");
            }
            if (mTvTotalDistance != null) {
                mTvTotalDistance.setText("0.0");
            }
            if (mTvTotalTime != null) {
                mTvTotalTime.setText("00:00");
            }
            return;
        }
        
        
        // 获取用户的排行榜记录
        com.example.myrun.model.RankingRecord userRecord = rankingManager.getUserRecord(username);
        
        if (userRecord != null) {
            // 显示真实数据
            if (mTvTotalRuns != null) {
                mTvTotalRuns.setText(String.valueOf(userRecord.getTotalRuns()));
            }
            if (mTvTotalDistance != null) {
                mTvTotalDistance.setText(String.format("%.1f", userRecord.getTotalDistance()));
            }
            if (mTvTotalTime != null) {
                long totalTime = userRecord.getTotalTime();
                long hours = totalTime / 3600;
                long minutes = (totalTime % 3600) / 60;
                if (hours > 0) {
                    mTvTotalTime.setText(String.format("%d:%02d", hours, minutes));
                } else {
                    mTvTotalTime.setText(String.format("%02d", minutes));
                }
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 每次显示时刷新统计数据
        setupStatistics();
    }
    
    /**
     * 设置工具栏
     */
    private void setupToolbar() {
        if (mToolbar != null) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 打开侧滑菜单
                    if (slideMenu != null) {
                        slideMenu.switchMenu();
                    }
                }
            });
        }
    }
    
    /**
     * 设置菜单点击事件
     */
    private void setupMenuListeners() {
        // 查看跑步记录
        if (mMenuRunRecords != null) {
            mMenuRunRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到跑步记录页面
                    Intent recordsIntent = new Intent(MyActivity.this, RunRecordsActivity.class);
                    startActivity(recordsIntent);
                }
            });
        }
        
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
     * 设置侧滑菜单监听器
     */
    private void setupSlideMenu() {
        // 设置侧边栏菜单项点击事件
        if (mSlideMenuSettings != null) {
            mSlideMenuSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到设置页面
                    Intent settingsIntent = new Intent(MyActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    // 关闭侧滑菜单
                    if (slideMenu != null) {
                        slideMenu.closeMenu();
                    }
                }
            });
        }
        
        if (mSlideMenuAbout != null) {
            mSlideMenuAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到关于页面
                    Intent aboutIntent = new Intent(MyActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    // 关闭侧滑菜单
                    if (slideMenu != null) {
                        slideMenu.closeMenu();
                    }
                }
            });
        }
        
        if (mSlideMenuLogout != null) {
            mSlideMenuLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 关闭侧滑菜单
                    if (slideMenu != null) {
                        slideMenu.closeMenu();
                    }
                    // 显示退出登录确认对话框
                    showLogoutDialog();
                }
            });
        }
    }
    
    /**
     * 设置返回键处理
     */
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 如果侧滑菜单打开则先关闭菜单
                if (slideMenu != null && slideMenu.getScrollX() < 0) {
                    slideMenu.closeMenu();
                } else {
                    // 否则执行默认的返回操作
                    finish();
                }
            }
        });
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