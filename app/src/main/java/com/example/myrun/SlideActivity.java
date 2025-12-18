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
    private ImageView mIvHead;
    private SlideMenu slideMenu;
    private Button mBtnRun;
    private Button mBtnAI;
    private Button mBtnRankingList;
    private Button mBtnMy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slide);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.slideMenu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //找到控件
            mIvHead = findViewById(R.id.iv_head);
            slideMenu = findViewById(R.id.slideMenu);
            mBtnRun = findViewById(R.id.btn_main_1);
            mBtnAI = findViewById(R.id.btn_main_2);
            mBtnRankingList = findViewById(R.id.btn_main_3);
            mBtnMy = findViewById(R.id.btn_main_4);

            //实现侧滑的部分,点击加侧滑
            mIvHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slideMenu.switchMenu();
                    ToastUtil.showMsg(SlideActivity.this,"ok");
                }
            });
            setListener();
            return insets;
        });
    }
    private void setListener(){
       OnClick onClick = new OnClick();

        //对每个按钮进行setOnClickListener
        mBtnRun.setOnClickListener(onClick);
        mBtnAI.setOnClickListener(onClick);
        mBtnRankingList.setOnClickListener(onClick);
        mBtnMy.setOnClickListener(onClick);
    }
    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = null;
            if(v.getId() == R.id.btn_main_1){
                intent = new Intent(SlideActivity.this,RunActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_main_2){
                intent = new Intent(SlideActivity.this,AIActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_main_3){
                intent = new Intent(SlideActivity.this,RankingListActivity.class);
                startActivity(intent);
            }
            else if(v.getId() == R.id.btn_main_4){
                intent = new Intent(SlideActivity.this,MyActivity.class);
                startActivity(intent);
            }


        }
    }
}