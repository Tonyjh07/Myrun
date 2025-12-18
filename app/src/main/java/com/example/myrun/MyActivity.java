package com.example.myrun;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myrun.util.UserManager;

public class MyActivity extends AppCompatActivity {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private Button btnChangeAvatar;

    // 头像选择启动器
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            ivAvatar.setImageURI(imageUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.my), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets.inset(systemBars);
        });

        // 初始化视图
        initViews();

        // 设置用户信息
        setUserInfo();

        // 设置点击事件
        setClickListeners();
    }

    private void initViews() {
        ivAvatar = findViewById(R.id.iv_avatar);
        tvUsername = findViewById(R.id.tv_username);
        btnChangeAvatar = findViewById(R.id.btn_change_avatar);
    }

    private void setUserInfo() {
        UserManager userManager = UserManager.getInstance(this);
        String username = userManager.getUsername();
        if (username != null && !username.isEmpty()) {
            tvUsername.setText(username);
        } else {
            tvUsername.setText("用户名");
        }
    }

    private void setClickListeners() {
        // 更换头像
        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        // 设置
        findViewById(R.id.card_settings).setOnClickListener(v -> {
            Intent intent = new Intent(MyActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}