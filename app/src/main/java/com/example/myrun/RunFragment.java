package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class RunFragment extends Fragment {

    private MaterialButton btnStartRun;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);
        
        // 初始化视图
        initViews(view);
        
        return view;
    }
    
    private void initViews(View view) {
        btnStartRun = view.findViewById(R.id.btn_start_run);
        
        // 设置按钮点击监听器
        if (btnStartRun != null) {
            btnStartRun.setOnClickListener(v -> startRunActivity());
        }
    }
    
    private void startRunActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RunActivity.class);
            startActivity(intent);
        }
    }
}