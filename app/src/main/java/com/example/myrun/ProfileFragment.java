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
        

        
        // 设置菜单监听器
        setupMenuListeners(view);
        
        return view;
    }
    
    private void initViews(View view) {
        // 主界面视图
        mTvUsername = view.findViewById(R.id.tv_username);
        mTvUserInfo = view.findViewById(R.id.tv_user_info);

        

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
        } else {
            // 用户未登录
            if (mTvUsername != null) {
                mTvUsername.setText("未登录");
            }
            if (mTvUserInfo != null) {
                mTvUserInfo.setText("点击登录");
            }
        }
    }
    
    private void setupStatistics() {
        // 已移除运动统计功能
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
        
        // 重定向到LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新统计数据
        setupUserInfo();
        setupStatistics();
    }
}