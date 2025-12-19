package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class RunActivity extends AppCompatActivity {
    
    private TextView tvTimer, tvDistance, tvPace, tvCalories;
    private MaterialButton btnPause, btnStop;
    
    private boolean isRunning = false;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private Handler handler = new Handler();
    private Runnable timerRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        
        initViews();
        setupClickListeners();
        startRunning();
    }
    
    private void initViews() {
        tvTimer = findViewById(R.id.tv_timer);
        tvDistance = findViewById(R.id.tv_distance);
        tvPace = findViewById(R.id.tv_pace);
        tvCalories = findViewById(R.id.tv_calories);
        btnPause = findViewById(R.id.btn_pause);
        btnStop = findViewById(R.id.btn_stop);
        
        // 初始化计时器
        initTimer();
    }
    
    private void setupClickListeners() {
        if (btnPause != null) {
            btnPause.setOnClickListener(v -> togglePauseResume());
        }
        if (btnStop != null) {
            btnStop.setOnClickListener(v -> stopRunning());
        }
    }
    
    private void initTimer() {
        timerRunnable = new Runnable() {
            public void run() {
                if (isRunning) {
                    timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    updatedTime = timeSwapBuff + timeInMilliseconds;
                    
                    int secs = (int) (updatedTime / 1000);
                    int mins = secs / 60;
                    int hours = mins / 60;
                    secs = secs % 60;
                    mins = mins % 60;
                    
                    if (tvTimer != null) {
                        tvTimer.setText(String.format("%02d:%02d:%02d", hours, mins, secs));
                    }
                    
                    // 更新跑步数据
                    updateRunningData(secs);
                    
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }
    
    private void startRunning() {
        if (!isRunning) {
            isRunning = true;
            startTime = SystemClock.uptimeMillis();
            if (timerRunnable != null) {
                handler.postDelayed(timerRunnable, 0);
            }
            if (btnPause != null) {
                btnPause.setText("暂停");
            }
        }
    }
    
    private void togglePauseResume() {
        if (isRunning) {
            // 暂停
            isRunning = false;
            timeSwapBuff += timeInMilliseconds;
            if (timerRunnable != null) {
                handler.removeCallbacks(timerRunnable);
            }
            if (btnPause != null) {
                btnPause.setText("继续");
            }
        } else {
            // 继续
            isRunning = true;
            startTime = SystemClock.uptimeMillis();
            if (timerRunnable != null) {
                handler.postDelayed(timerRunnable, 0);
            }
            if (btnPause != null) {
                btnPause.setText("暂停");
            }
        }
    }
    
    private void stopRunning() {
        isRunning = false;
        if (timerRunnable != null) {
            handler.removeCallbacks(timerRunnable);
        }
        
        // 更新跑步数据
        updateRunningData((int) (updatedTime / 1000));
        
        // 延迟1秒后返回MainActivity，让用户看到最终数据
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RunActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }, 1000);
    }
    
    private void updateRunningData(int seconds) {
        // 模拟距离计算（每秒增加0.002公里，约7.2公里/小时的速度）
        double distance = seconds * 0.002;
        
        if (tvDistance != null) {
            tvDistance.setText(String.format("%.2f", distance));
        }
        
        // 计算配速（分/公里）
        if (distance > 0 && tvPace != null) {
            double paceMinutes = seconds / 60.0 / distance;
            int paceMin = (int) paceMinutes;
            int paceSec = (int) ((paceMinutes - paceMin) * 60);
            tvPace.setText(String.format("%d:%02d", paceMin, paceSec));
        }
        
        // 计算卡路里（约60卡/公里）
        if (tvCalories != null) {
            int calories = (int) (distance * 60);
            tvCalories.setText(String.valueOf(calories));
        }
    }
}