package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    //声明控件
    private MaterialButton mBtnLogin;
    private MaterialButton mBtnRegister;
    private TextInputEditText mEtUser;
    private TextInputEditText mEtPassword;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        //找到控件
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mEtUser = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);

        //设置点击监听器
        mBtnLogin.setOnClickListener(this::onLoginClick);
        mBtnRegister.setOnClickListener(this::onRegisterClick);

        // 检查是否已登录，如果已登录则直接跳转到主页面
        if (userManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    
    /**
     * 登录按钮点击事件
     */
    public void onLoginClick(View v)
    {
        //需要获取输入的用户名和密码
        String username = mEtUser.getText().toString().trim();
        String password = mEtPassword.getText().toString();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showMsg(LoginActivity.this, "请输入用户名");
            mEtUser.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showMsg(LoginActivity.this, "请输入密码");
            mEtPassword.requestFocus();
            return;
        }

        // 使用UserManager验证用户
        if (userManager.loginUser(username, password)) {
            ToastUtil.showMsg(LoginActivity.this, "登录成功！");
            // 如果正确进行跳转
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭登录页面
        } else {
            ToastUtil.showMsg(LoginActivity.this, "用户名或密码有误，请重新登录");
        }
    }

    /**
     * 注册按钮点击事件
     */
    private void onRegisterClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}