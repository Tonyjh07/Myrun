package com.example.myrun;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrun.adapter.RunRecordAdapter;
import com.example.myrun.model.RunRecord;
import com.example.myrun.util.RunRecordManager;
import com.example.myrun.util.StatusBarUtil;
import com.example.myrun.util.ToastUtil;
import com.example.myrun.util.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

/**
 * 跑步记录历史页面
 */
public class RunRecordsActivity extends AppCompatActivity {

    private RecyclerView rvRecords;
    private TextView tvEmpty;
    private RunRecordAdapter adapter;
    private RunRecordManager runRecordManager;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            // 设置系统状态栏让出空间
            StatusBarUtil.setSystemStatusBar(this);
            setContentView(R.layout.activity_run_records);
            
            // 为根布局设置系统栏内边距
            View mainView = findViewById(R.id.main);
            if (mainView != null) {
                StatusBarUtil.setupViewPadding(mainView);
                ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
            }

            // 初始化管理器
            userManager = UserManager.getInstance(this);
            runRecordManager = RunRecordManager.getInstance(this);

            // 初始化视图
            initViews();

            // 加载数据
            loadRecords();

            // 设置工具栏
            setupToolbar();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showMsg(this, "打开跑步记录失败");
            finish();
        }
    }

    private void initViews() {
        try {
            rvRecords = findViewById(R.id.rv_records);
            tvEmpty = findViewById(R.id.tv_empty);

            // 设置RecyclerView
            if (rvRecords != null) {
                rvRecords.setLayoutManager(new LinearLayoutManager(this));
                adapter = new RunRecordAdapter(null);
                rvRecords.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecords() {
        if (userManager == null || runRecordManager == null) {
            showEmpty();
            return;
        }

        try {
            String username = userManager.getCurrentUsername();
            if (username == null || username.isEmpty()) {
                showEmpty();
                return;
            }

            List<RunRecord> records = runRecordManager.getRecords(username);

            if (records == null || records.isEmpty()) {
                showEmpty();
            } else {
                showRecords(records);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmpty();
        }
    }

    private void showEmpty() {
        if (rvRecords != null) {
            rvRecords.setVisibility(View.GONE);
        }
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void showRecords(List<RunRecord> records) {
        if (rvRecords != null) {
            rvRecords.setVisibility(View.VISIBLE);
        }
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
        if (adapter != null) {
            adapter.updateData(records);
        }
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新数据
        loadRecords();
    }
}