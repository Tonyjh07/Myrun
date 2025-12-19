package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.UserManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // 设置用户信息
        setupUserInfo();
        
        // 设置注销按钮
        setupLogoutButton();
        
        // 设置标题
        TextView title = findViewById(R.id.title);
        title.setText("设置");
        
        // 设置工具栏返回按钮
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void setupUserInfo() {
        TextView tvUserInfo = findViewById(R.id.tv_user_info);
        if (userManager.isLoggedIn()) {
            String username = userManager.getCurrentUsername();
            tvUserInfo.setText("当前用户：" + username);
        } else {
            tvUserInfo.setText("当前用户：未登录");
        }
    }
    
    private void setupLogoutButton() {
        MaterialButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userManager.isLoggedIn()) {
                    userManager.logout();
                    // 跳转到登录页面
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}