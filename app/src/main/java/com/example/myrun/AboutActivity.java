package com.example.myrun;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.StatusBarUtil;

import com.google.android.material.appbar.MaterialToolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_about);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(R.id.main));
        
        // 设置标题
        TextView title = findViewById(R.id.title);
        title.setText("关于");
        
        // 设置工具栏返回按钮
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}