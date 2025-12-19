package com.example.myrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.ToastUtil;

public class SlideActivity extends AppCompatActivity {
    //声明控件
    private SlideMenu slideMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slide);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.slideMenu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //找到控件
            slideMenu = findViewById(R.id.slideMenu);
            
            // 实现侧滑的部分
            // 由于相关控件已删除，侧滑功能暂时禁用
            return insets;
        });
    }
}