package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.ToastUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RunActivity extends AppCompatActivity {

    private TextView tvTimer, tvDistance, tvPace, tvCalories, tvGpsStatus;
    private Button btnPause, btnStop;
    private BottomNavigationView bottomNavigation;
    
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    
    private boolean isRunning = false;
    private boolean isPaused = false;
    
    // 跑步数据
    private double distance = 0.0; // 公里
    private int calories = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_run);
        
        // 初始化视图
        initViews();
        
        // 设置窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tv_gps_status), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 初始化计时器
        initTimer();
        
        // 模拟GPS定位
        simulateGpsLocation();
    }
    
    private void initViews() {
        tvTimer = findViewById(R.id.tv_timer);
        tvDistance = findViewById(R.id.tv_distance);
        tvPace = findViewById(R.id.tv_pace);
        tvCalories = findViewById(R.id.tv_calories);
        tvGpsStatus = findViewById(R.id.tv_gps_status);
        btnPause = findViewById(R.id.btn_pause);
        btnStop = findViewById(R.id.btn_stop);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // 设置按钮点击监听器
        btnPause.setOnClickListener(v -> togglePause());
        btnStop.setOnClickListener(v -> stopRunning());
        
        // 设置底部导航栏
        setupBottomNavigation();
        
        // 开始跑步
        startRunning();
    }
    
    private void initTimer() {
        timerRunnable = new Runnable() {
            public void run() {
                if (isRunning && !isPaused) {
                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    updatedTime = timeSwapBuff + timeInMilliseconds;
                    
                    int secs = (int) (updatedTime / 1000);
                    int mins = secs / 60;
                    int hours = mins / 60;
                    secs = secs % 60;
                    mins = mins % 60;
                    
                    tvTimer.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
                    
                    // 更新距离和卡路里（模拟数据）
                    updateRunningData(secs);
                    
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }
    
    private void startRunning() {
        isRunning = true;
        isPaused = false;
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(timerRunnable, 0);
        
        btnPause.setText("暂停");
        // 移除setIconResource调用，Button类没有这个方法
    }
    
    private void togglePause() {
        if (isPaused) {
            // 恢复跑步
            isPaused = false;
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(timerRunnable, 0);
            btnPause.setText("暂停");
            // 移除setIconResource调用，Button类没有这个方法
        } else {
            // 暂停跑步
            isPaused = true;
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(timerRunnable);
            btnPause.setText("继续");
            // 移除setIconResource调用，Button类没有这个方法
        }
    }
    
    private void stopRunning() {
        // 保存跑步记录
        saveRunningRecord();
        
        // 结束跑步并返回主页
        isRunning = false;
        isPaused = false;
        handler.removeCallbacks(timerRunnable);
        
        // 返回主页
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    
    private void updateRunningData(int seconds) {
        // 模拟距离计算（每秒增加0.002公里，约7.2公里/小时的速度）
        distance = seconds * 0.002;
        tvDistance.setText(String.format("%.2f", distance));
        
        // 计算配速（分/公里）
        if (distance > 0) {
            double paceMinutes = seconds / 60.0 / distance;
            int paceMin = (int) paceMinutes;
            int paceSec = (int) ((paceMinutes - paceMin) * 60);
            tvPace.setText(String.format("%d:%02d", paceMin, paceSec));
        }
        
        // 计算卡路里（约60卡/公里）
        calories = (int) (distance * 60);
        tvCalories.setText(String.valueOf(calories));
    }
    
    private void simulateGpsLocation() {
        // 模拟GPS定位过程
        handler.postDelayed(() -> {
            tvGpsStatus.setText("GPS定位成功");
            tvGpsStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }, 3000);
    }
    
    private void saveRunningRecord() {
        // 这里应该实现保存跑步记录到数据库的逻辑
        ToastUtil.showMsg(this, "跑步记录已保存：距离 " + String.format("%.2f", distance) + " 公里");
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_home) {
                // 切换到主页，结束当前跑步活动
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 结束当前Activity，确保导航栏状态正确更新
                return true;
            } else if (itemId == R.id.navigation_run) {
                // 已经在跑步页面，不需要切换
                return true;
            } else if (itemId == R.id.navigation_ranking) {
                // 切换到排行榜页面，不结束跑步
                ToastUtil.showMsg(this, "切换到排行榜页面，跑步在后台继续");
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // 切换到个人资料页面，不结束跑步
                ToastUtil.showMsg(this, "切换到个人资料页面，跑步在后台继续");
                return true;
            }
            
            return false;
        });
        
        // 设置当前选中的项为跑步
        bottomNavigation.setSelectedItemId(R.id.navigation_run);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(timerRunnable);
        }
    }
}