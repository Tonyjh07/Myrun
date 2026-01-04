package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // 直接跳转到"我的"界面
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MyActivity.class);
            startActivity(intent);
            // 延迟关闭当前Fragment的显示（虽然会立即跳转）
        }
        
        return view;
    }
}