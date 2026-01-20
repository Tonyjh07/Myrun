package com.example.myrun;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrun.model.RankingRecord;
import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.example.myrun.util.RankingManager;

public class ProfileFragment extends Fragment {
    
    private TextView mTvUsername;
    private TextView mTvUserInfo;
    private TextView mTvTotalRuns;
    private TextView mTvTotalDistance;
    private TextView mTvTotalTime;
    private TextView mSlideTvUsername;
    private TextView mSlideTvUserInfo;
    
    private SlideMenu slideMenu;
    private UserManager userManager;
    private RankingManager rankingManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // 初始化管理器
        userManager = UserManager.getInstance(getContext());
        rankingManager = RankingManager.getInstance(getContext());
        
        // 初始化视图
        initViews(view);
        
        // 设置用户信息
        setupUserInfo();
        
        // 设置统计数据
        setupStatistics();
        
        // 设置工具栏
        setupToolbar(view);
        
        // 设置菜单监听器
        setupMenuListeners(view);
        
        // 设置侧滑菜单
        setupSlideMenu(view);
        
        return view;
    }
    
    private void initViews(View view) {
        // 主界面视图
        mTvUsername = view.findViewById(R.id.tv_username);
        mTvUserInfo = view.findViewById(R.id.tv_user_info);
        mTvTotalRuns = view.findViewById(R.id.tv_total_runs);
        mTvTotalDistance = view.findViewById(R.id.tv_total_distance);
        mTvTotalTime = view.findViewById(R.id.tv_total_time);
        
        // 侧边栏视图
        mSlideTvUsername = view.findViewById(R.id.slide_tv_username);
        mSlideTvUserInfo = view.findViewById(R.id.slide_tv_user_info);
        
        // SlideMenu
        slideMenu = view.findViewById(R.id.slideMenu);
    }
    
    private void setupUserInfo() {
        String username = userManager.getCurrentUsername();
        
        if (username != null && !username.isEmpty()) {
            // 用户已登录
            if (mTvUsername != null) {
                mTvUsername.setText(username);
            }
            if (mTvUserInfo != null) {
                mTvUserInfo.setText("跑步爱好者");
            }
            if (mSlideTvUsername != null) {
                mSlideTvUsername.setText(username);
            }
            if (mSlideTvUserInfo != null) {
                mSlideTvUserInfo.setText("跑步爱好者");
            }
        } else {
            // 用户未登录
            if (mTvUsername != null) {
                mTvUsername.setText("未登录");
            }
            if (mTvUserInfo != null) {
                mTvUserInfo.setText("点击登录");
            }
            if (mSlideTvUsername != null) {
                mSlideTvUsername.setText("未登录");
            }
            if (mSlideTvUserInfo != null) {
                mSlideTvUserInfo.setText("点击登录");
            }
        }
    }
    
    private void setupStatistics() {
        String username = userManager.getCurrentUsername();
        
        if (username == null || username.isEmpty()) {
            // 用户未登录，显示默认值
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
        RankingRecord userRecord = rankingManager.getUserRecord(username);
        
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
    
    private void setupToolbar(View view) {
        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> {
                // 打开侧滑菜单
                if (slideMenu != null) {
                    slideMenu.switchMenu();
                }
            });
        }
    }
    
    private void setupMenuListeners(View view) {
        // 查看跑步记录
        View menuRunRecords = view.findViewById(R.id.menu_run_records);
        if (menuRunRecords != null) {
            menuRunRecords.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), RunRecordsActivity.class);
                startActivity(intent);
            });
        }
        
        // 设置
        View menuSettings = view.findViewById(R.id.menu_settings);
        if (menuSettings != null) {
            menuSettings.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            });
        }
        
        // 关于
        View menuAbout = view.findViewById(R.id.menu_about);
        if (menuAbout != null) {
            menuAbout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            });
        }
        
        // 退出登录
        View menuLogout = view.findViewById(R.id.menu_logout);
        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                showLogoutDialog();
            });
        }
    }
    
    private void setupSlideMenu(View view) {
        // 侧边栏设置
        View slideMenuSettings = view.findViewById(R.id.slide_menu_settings);
        if (slideMenuSettings != null) {
            slideMenuSettings.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                if (slideMenu != null) {
                    slideMenu.closeMenu();
                }
            });
        }
        
        // 侧边栏关于
        View slideMenuAbout = view.findViewById(R.id.slide_menu_about);
        if (slideMenuAbout != null) {
            slideMenuAbout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                if (slideMenu != null) {
                    slideMenu.closeMenu();
                }
            });
        }
        
        // 侧边栏退出登录
        View slideMenuLogout = view.findViewById(R.id.slide_menu_logout);
        if (slideMenuLogout != null) {
            slideMenuLogout.setOnClickListener(v -> {
                if (slideMenu != null) {
                    slideMenu.closeMenu();
                }
                showLogoutDialog();
            });
        }
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void logout() {
        // 调用UserManager退出登录
        userManager.logout();
        
        // 显示提示信息
        ToastUtil.showMsg(getContext(), "已退出登录");
        
        // 刷新界面
        setupUserInfo();
        setupStatistics();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新统计数据
        setupUserInfo();
        setupStatistics();
    }
}