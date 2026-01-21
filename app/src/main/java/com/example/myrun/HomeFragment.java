package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myrun.util.ToastUtil;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {
    
    private MaterialButton mBtnStartRun;
    private MaterialButton mBtnViewRecords;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // 初始化视图
        initViews(view);
        
        return view;
    }
    
    private void initViews(View view) {
        mBtnStartRun = view.findViewById(R.id.btn_start_run);
        mBtnViewRecords = view.findViewById(R.id.btn_view_records);
        
        // 设置点击监听器
        setListeners();
    }
    
    private void setListeners() {
        // 开始跑步按钮点击事件
        if (mBtnStartRun != null) {
            mBtnStartRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 切换到跑步界面
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        if (mainActivity.runFragment == null) {
                            mainActivity.initFragments();
                        }
                        mainActivity.switchToRunFragment();
                    }
                }
            });
        }
        
        // 查看全部记录按钮点击事件
        if (mBtnViewRecords != null) {
            mBtnViewRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看跑步记录功能
                    ToastUtil.showMsg(getActivity(), "查看记录功能开发中...");
                }
            });
        }
    }
}