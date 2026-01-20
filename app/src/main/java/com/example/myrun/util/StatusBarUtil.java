package com.example.myrun.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 状态栏工具类
 * 用于统一管理应用的状态栏样式
 */
public class StatusBarUtil {

    /**
     * 设置沉浸式状态栏（全屏模式）
     * 用于RunActivity等需要沉浸式体验的场景
     */
    public static void setImmersiveStatusBar(Activity activity) {
        try {
            Window window = activity.getWindow();
            if (window == null) {
                return;
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11 及以上版本
                WindowInsetsController insetsController = window.getInsetsController();
                if (insetsController != null) {
                    // 隐藏状态栏
                    insetsController.hide(WindowInsets.Type.statusBars());
                    // 设置系统栏行为，允许通过滑动显示状态栏
                    insetsController.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    );
                }
            } else {
                // Android 10 及以下版本
                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    );
                }
            }
            
            // 设置状态栏为黑色半透明，适合运动场景
            window.setStatusBarColor(android.graphics.Color.argb(128, 0, 0, 0));
            // 设置导航栏为黑色半透明
            window.setNavigationBarColor(android.graphics.Color.argb(128, 0, 0, 0));
        } catch (Exception e) {
            android.util.Log.e("StatusBarUtil", "设置沉浸式状态栏失败: " + e.getMessage());
        }
    }

    /**
     * 设置系统状态栏让出空间模式
     * 用于MainActivity等其他界面
     */
    public static void setSystemStatusBar(Activity activity) {
        try {
            Window window = activity.getWindow();
            if (window == null) {
                return;
            }
            
            // 设置状态栏颜色为深灰色，确保文字清晰可见
            window.setStatusBarColor(android.graphics.Color.parseColor("#333333"));
            
            // 设置导航栏颜色为深灰色
            window.setNavigationBarColor(android.graphics.Color.parseColor("#333333"));
            
            // 设置状态栏文字和图标为白色，与黑色背景形成对比
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    // 清除亮色模式标志，使用深色模式（白色文字）
                    decorView.setSystemUiVisibility(0);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("StatusBarUtil", "设置系统状态栏失败: " + e.getMessage());
        }
    }

    /**
     * 为根布局设置系统栏内边距
     * 防止内容被系统栏遮挡
     */
    public static void setupRootLayoutPadding(Activity activity, int rootLayoutId) {
        View rootView = activity.findViewById(rootLayoutId);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                // 获取状态栏和导航栏高度
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                
                // 为根布局设置内边距，避免内容被系统栏遮挡
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                
                return insets;
            });
        }
    }

    /**
     * 为指定视图设置系统栏内边距
     */
    public static void setupViewPadding(View view) {
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    /**
     * 显示状态栏（退出沉浸式模式）
     */
    public static void showStatusBar(Activity activity) {
        try {
            Window window = activity.getWindow();
            if (window == null) {
                return;
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController insetsController = window.getInsetsController();
                if (insetsController != null) {
                    insetsController.show(WindowInsets.Type.statusBars());
                }
            } else {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    );
                }
            }
        } catch (Exception e) {
            android.util.Log.e("StatusBarUtil", "显示状态栏失败: " + e.getMessage());
        }
    }

    /**
     * 隐藏状态栏（进入沉浸式模式）
     */
    public static void hideStatusBar(Activity activity) {
        try {
            Window window = activity.getWindow();
            if (window == null) {
                return;
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController insetsController = window.getInsetsController();
                if (insetsController != null) {
                    insetsController.hide(WindowInsets.Type.statusBars());
                }
            } else {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    );
                }
            }
        } catch (Exception e) {
            android.util.Log.e("StatusBarUtil", "隐藏状态栏失败: " + e.getMessage());
        }
    }
}