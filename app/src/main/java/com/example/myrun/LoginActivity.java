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

public class LoginActivity extends AppCompatActivity {

    //声明控件
    private Button mBtnLogin;
    private EditText mEtUser;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        //找到控件
        mBtnLogin = findViewById(R.id.btn_login);
        mEtUser = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);

        //匹配对应的用户名和密码才能进行登录操作
        mBtnLogin.setOnClickListener(this::onClick);
    }
    
    public void onClick(View v)
    {
        //需要获取输入的用户名和密码
        String username=mEtUser.getText().toString();
        String password=mEtPassword.getText().toString();
        //弹出的内容设置
        String ok="登录成功!";
        String fail="用户名或密码有误，请重新登录";
        Intent intent = null;

        //假设账号wbh，密码123456
        if(username.equals("wbh")&&password.equals("123456"))
        {
            //封装好的类
            ToastUtil.showMsg(LoginActivity.this,ok);

            //如果正确进行跳转
            intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            ToastUtil.showMsg(LoginActivity.this,fail);
        }
    }
}