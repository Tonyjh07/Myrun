package com.example.myrun;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.StatusBarUtil;

public class AIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_aiactivity);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(R.id.ai));
    }
}