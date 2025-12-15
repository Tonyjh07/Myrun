package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.ToastUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //声明控件
    private MaterialButton mBtnStartRun;
    private MaterialButton mBtnViewRecords;
    private BottomNavigationView mBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        //找到控件
        mBtnStartRun = findViewById(R.id.btn_start_run);
        mBtnViewRecords = findViewById(R.id.btn_view_records);
        mBottomNavigation = findViewById(R.id.bottom_navigation);

        //设置点击监听器
        setListeners();
        
        //设置底部导航监听器
        setupBottomNavigation();
    }
    
    private void setListeners(){
        //开始跑步按钮点击事件
        if (mBtnStartRun != null) {
            mBtnStartRun.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 开始跑步功能
                    ToastUtil.showMsg(MainActivity.this, "开始跑步功能");
                }
            });
        }
        
        //查看全部记录按钮点击事件
        if (mBtnViewRecords != null) {
            mBtnViewRecords.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 查看跑步记录功能
                    ToastUtil.showMsg(MainActivity.this, "查看跑步记录功能");
                }
            });
        }
    }
    
    private void setupBottomNavigation() {
        if (mBottomNavigation != null) {
            mBottomNavigation.setOnNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // 已经在首页，无需跳转
                    ToastUtil.showMsg(MainActivity.this, "首页");
                    return true;
                } else if (itemId == R.id.navigation_run) {
                    // 跳转到跑步界面
                    Intent runIntent = new Intent(MainActivity.this, RunActivity.class);
                    startActivity(runIntent);
                    return true;
                } else if (itemId == R.id.navigation_ranking) {
                    // 跳转到排行榜界面
                    Intent rankingIntent = new Intent(MainActivity.this, RankingListActivity.class);
                    startActivity(rankingIntent);
                    return true;
                } else if (itemId == R.id.navigation_profile) {
                    // 跳转到个人中心界面
                    Intent profileIntent = new Intent(MainActivity.this, MyActivity.class);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            });
        }
    }
}