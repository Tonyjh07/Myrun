package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;

/**
 * 用户注册页面
 */
public class RegisterActivity extends AppCompatActivity {
    
    private EditText mEtUsername;
    private EditText mEtPassword;
    private EditText mEtConfirmPassword;
    private Button mBtnRegister;
    private Button mBtnBackToLogin;
    
    private UserManager userManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        // 初始化视图
        initViews();
        
        // 设置事件监听
        setupListeners();
    }
    
    private void initViews() {
        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnBackToLogin = findViewById(R.id.btn_back_to_login);
    }
    
    private void setupListeners() {
        if (mBtnRegister != null) {
            mBtnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUser();
                }
            });
        }
        
        if (mBtnBackToLogin != null) {
            mBtnBackToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backToLogin();
                }
            });
        }
    }
    
    private void registerUser() {
        if (mEtUsername == null || mEtPassword == null || mEtConfirmPassword == null || userManager == null) {
            return;
        }
        
        String username = mEtUsername.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String confirmPassword = mEtConfirmPassword.getText().toString().trim();
        
        // 验证输入
        if (username.isEmpty()) {
            ToastUtil.showMsg(this, "请输入用户名");
            return;
        }
        
        if (password.isEmpty()) {
            ToastUtil.showMsg(this, "请输入密码");
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            ToastUtil.showMsg(this, "请确认密码");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            ToastUtil.showMsg(this, "两次输入的密码不一致");
            return;
        }
        
        // 检查密码长度
        if (password.length() < 6) {
            ToastUtil.showMsg(this, "密码长度至少6位");
            return;
        }
        
        // 注册用户
        boolean success = userManager.register(username, password);
        
        if (success) {
            ToastUtil.showMsg(this, "注册成功！");
            // 注册成功后跳转到登录页面
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            // 传递用户名到登录页面，方便用户
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            ToastUtil.showMsg(this, "注册失败，用户名已存在");
        }
    }
    
    private void backToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}