package com.example.myrun;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrun.model.RankingRecord;
import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.example.myrun.util.RankingManager;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    
    private TextView mTvUsername;
    private TextView mTvUserInfo;
    private TextView mTvHeight;
    private TextView mTvWeight;
    private View mMenuBasicInfo;
    
    private UserManager userManager;
    private RankingManager rankingManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        try {
            // 初始化管理器
            if (getContext() != null) {
                userManager = UserManager.getInstance(getContext());
                rankingManager = RankingManager.getInstance(getContext());
            }
            
            // 初始化视图
            initViews(view);
            
            // 设置用户信息
            setupUserInfo();
            
            // 设置基础信息
            setupBasicInfo();
            
            // 设置菜单监听器
            setupMenuListeners(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return view;
    }
    
    private void initViews(View view) {
        // 主界面视图
        mTvUsername = view.findViewById(R.id.tv_username);
        mTvUserInfo = view.findViewById(R.id.tv_user_info);
        mTvHeight = view.findViewById(R.id.tv_height);
        mTvWeight = view.findViewById(R.id.tv_weight);
        mMenuBasicInfo = view.findViewById(R.id.menu_basic_info);
    }
    
    private void setupUserInfo() {
        if (userManager == null) {
            return;
        }
        
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setupStatistics() {
        // 已移除运动统计功能
    }
    
    /**
     * 设置基础信息显示
     */
    private void setupBasicInfo() {
        if (userManager == null) {
            return;
        }
        
        try {
            String username = userManager.getCurrentUsername();
            if (username == null || username.isEmpty()) {
            if (mTvHeight != null) {
                mTvHeight.setText("-- cm");
            }
            if (mTvWeight != null) {
                mTvWeight.setText("-- kg");
            }
                return;
            }
            
            float height = userManager.getHeight(username);
            float weight = userManager.getWeight(username);
            
            if (mTvHeight != null) {
                if (height > 0) {
                    mTvHeight.setText(String.format("%.0f cm", height));
                } else {
                    mTvHeight.setText("-- cm");
                }
            }
            
            if (mTvWeight != null) {
                if (weight > 0) {
                    mTvWeight.setText(String.format("%.1f kg", weight));
                } else {
                    mTvWeight.setText("-- kg");
                }
            }
            
            // 设置基础信息菜单项点击事件
            if (mMenuBasicInfo != null) {
                mMenuBasicInfo.setOnClickListener(v -> showEditBasicInfoDialog());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 显示编辑基础信息对话框
     */
    private void showEditBasicInfoDialog() {
        if (getContext() == null || userManager == null) {
            return;
        }
        
        try {
            String username = userManager.getCurrentUsername();
            if (username == null || username.isEmpty()) {
                ToastUtil.showMsg(getContext(), "请先登录");
                return;
            }
            
            float currentHeight = userManager.getHeight(username);
            float currentWeight = userManager.getWeight(username);
            
            // 创建输入对话框
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_basic_info, null);
            EditText etHeight = dialogView.findViewById(R.id.et_height);
            EditText etWeight = dialogView.findViewById(R.id.et_weight);
            
            // 设置当前值
            if (currentHeight > 0) {
                etHeight.setText(String.valueOf((int)currentHeight));
            }
            if (currentWeight > 0) {
                etWeight.setText(String.valueOf(currentWeight));
            }
            
            // 设置输入类型
            etHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etWeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            
            new AlertDialog.Builder(getContext())
                    .setTitle("编辑基础信息")
                    .setView(dialogView)
                    .setPositiveButton("保存", (dialog, which) -> {
                        try {
                            String heightStr = etHeight.getText().toString().trim();
                            String weightStr = etWeight.getText().toString().trim();
                            
                            float height = 0f;
                            float weight = 0f;
                            
                            if (!heightStr.isEmpty()) {
                                height = Float.parseFloat(heightStr);
                                if (height < 50 || height > 250) {
                                    ToastUtil.showMsg(getContext(), "身高范围：50-250cm");
                                    return;
                                }
                            }
                            
                            if (!weightStr.isEmpty()) {
                                weight = Float.parseFloat(weightStr);
                                if (weight < 20 || weight > 200) {
                                    ToastUtil.showMsg(getContext(), "体重范围：20-200kg");
                                    return;
                                }
                            }
                            
                            // 保存信息
                            if (height > 0) {
                                userManager.saveHeight(username, height);
                            }
                            if (weight > 0) {
                                userManager.saveWeight(username, weight);
                            }
                            
                            ToastUtil.showMsg(getContext(), "保存成功");
                            
                            // 刷新显示
                            setupBasicInfo();
                        } catch (NumberFormatException e) {
                            ToastUtil.showMsg(getContext(), "请输入有效的数字");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.showMsg(getContext(), "保存失败");
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMsg(getContext(), "打开编辑对话框失败");
        }
    }
    

    private void setupMenuListeners(View view) {
        try {
            // 基础信息已在setupBasicInfo中设置点击事件
            
            // 查看跑步记录
            View menuRunRecords = view.findViewById(R.id.menu_run_records);
            if (menuRunRecords != null && getActivity() != null) {
                menuRunRecords.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), RunRecordsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMsg(getContext(), "打开跑步记录失败");
                    }
                });
            }
            
            // 设置
            View menuSettings = view.findViewById(R.id.menu_settings);
            if (menuSettings != null && getActivity() != null) {
                menuSettings.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMsg(getContext(), "打开设置失败");
                    }
                });
            }
            
            // 关于
            View menuAbout = view.findViewById(R.id.menu_about);
            if (menuAbout != null && getActivity() != null) {
                menuAbout.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(getActivity(), AboutActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMsg(getContext(), "打开关于页面失败");
                    }
                });
            }
            
            // 退出登录
            View menuLogout = view.findViewById(R.id.menu_logout);
            if (menuLogout != null) {
                menuLogout.setOnClickListener(v -> {
                    showLogoutDialog();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void showLogoutDialog() {
        if (getContext() == null) {
            return;
        }
        
        try {
            new AlertDialog.Builder(getContext())
                    .setTitle("退出登录")
                    .setMessage("确定要退出登录吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        logout();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void logout() {
        try {
            if (userManager == null || getActivity() == null || getContext() == null) {
                return;
            }
            
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 每次显示时刷新统计数据
        setupUserInfo();
        setupBasicInfo();
        setupStatistics();
    }
}