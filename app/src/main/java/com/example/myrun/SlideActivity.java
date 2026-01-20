package com.example.myrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.StatusBarUtil;
import com.example.myrun.util.ToastUtil;

public class SlideActivity extends AppCompatActivity {
    //声明控件
    private SlideMenu slideMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_slide);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(R.id.slideMenu));
        
        //找到控件
        slideMenu = findViewById(R.id.slideMenu);
        
        // 实现侧滑的部分
        // 由于相关控件已删除，侧滑功能暂时禁用
    }
}