package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.StatusBarUtil;
import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;

public class LoginActivity extends AppCompatActivity {

    //声明控件
    private Button mBtnLogin;
    private EditText mEtUser;
    private EditText mEtPassword;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_login);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(android.R.id.content));
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        //找到控件
        mBtnLogin = findViewById(R.id.btn_login);
        mEtUser = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);

        //匹配对应的用户名和密码才能进行登录操作
        if (mBtnLogin != null) {
            mBtnLogin.setOnClickListener(this::onClick);
        }
        
        // 设置注册按钮点击事件
        Button btnRegister = findViewById(R.id.btn_register);
        if (btnRegister != null) {
            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        // 检查是否有从注册页面传递过来的用户名
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("username")) {
            String username = intent.getStringExtra("username");
            if (mEtUser != null && username != null) {
                mEtUser.setText(username);
            }
        }
    }
    
    public void onClick(View v)
    {
        if (mEtUser == null || mEtPassword == null || userManager == null) {
            return;
        }
        
        //需要获取输入的用户名和密码
        String username = mEtUser.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        
        // 验证输入
        if (username.isEmpty()) {
            ToastUtil.showMsg(LoginActivity.this, "请输入用户名");
            return;
        }
        
        if (password.isEmpty()) {
            ToastUtil.showMsg(LoginActivity.this, "请输入密码");
            return;
        }
        
        //弹出的内容设置
        String ok = "登录成功!";
        String fail = "用户名或密码有误，请重新登录";

        // 使用UserManager进行登录验证
        if (userManager.login(username, password)) {
            //封装好的类
            ToastUtil.showMsg(LoginActivity.this, ok);

            //如果正确进行跳转
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // 关闭登录页面
        } else {
            ToastUtil.showMsg(LoginActivity.this, fail);
        }
    }
}