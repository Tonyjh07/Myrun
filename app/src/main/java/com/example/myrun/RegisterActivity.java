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

public class RegisterActivity extends AppCompatActivity {

    //声明控件
    private MaterialButton mBtnRegister;
    private MaterialButton mBtnBackToLogin;
    private TextInputEditText mEtUser;
    private TextInputEditText mEtPassword;
    private TextInputEditText mEtConfirmPassword;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        //找到控件
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnBackToLogin = findViewById(R.id.btn_back_to_login);
        mEtUser = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mEtConfirmPassword = findViewById(R.id.et_confirm_password);

        //设置点击监听器
        mBtnRegister.setOnClickListener(this::onRegisterClick);
        mBtnBackToLogin.setOnClickListener(this::onBackToLoginClick);
    }
    
    /**
     * 注册按钮点击事件
     */
    public void onRegisterClick(View v) {
        //需要获取输入的用户名和密码
        String username = mEtUser.getText().toString().trim();
        String password = mEtPassword.getText().toString();
        String confirmPassword = mEtConfirmPassword.getText().toString();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showMsg(RegisterActivity.this, "请输入用户名");
            mEtUser.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.showMsg(RegisterActivity.this, "请输入密码");
            mEtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.showMsg(RegisterActivity.this, "请确认密码");
            mEtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            ToastUtil.showMsg(RegisterActivity.this, "两次输入的密码不一致");
            mEtConfirmPassword.requestFocus();
            return;
        }

        // 这里可以添加用户注册逻辑
        // 暂时简化处理，直接注册成功并跳转到登录页面
        ToastUtil.showMsg(RegisterActivity.this, "注册成功！请登录");
        
        // 跳转到登录页面
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // 关闭注册页面
    }

    /**
     * 返回登录按钮点击事件
     */
    private void onBackToLoginClick(View v) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // 关闭注册页面
    }
}