package com.example.myrun;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myrun.util.AIConfigManager;
import com.example.myrun.util.AIService;
import com.example.myrun.util.StatusBarUtil;
import com.example.myrun.util.UserManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {
    
    private UserManager userManager;
    private AIConfigManager aiConfigManager;
    private TextInputEditText editApiEndpoint;
    private TextInputEditText editApiKey;
    private TextInputEditText editSystemPrompt;
    private TextInputEditText editModel;
    private MaterialButton btnSaveAISettings;
    private MaterialButton btnTestAISettings;
    private MaterialButton btnSelectModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_settings);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(R.id.main));
        
        // 初始化UserManager
        userManager = UserManager.getInstance(this);
        
        // 初始化AI配置管理器
        aiConfigManager = AIConfigManager.getInstance(this);
        
        // 设置用户信息
        setupUserInfo();
        
        // 设置注销按钮
        setupLogoutButton();
        
        // 设置AI配置
        setupAISettings();
        
        // 设置标题
        TextView title = findViewById(R.id.title);
        if (title != null) {
            title.setText("设置");
        }
        
        // 设置工具栏返回按钮
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
    
    private void testAISettings() {
        Log.d("SettingsActivity", "=== testAISettings 开始 ===");
        if (editApiEndpoint != null && editApiKey != null && editSystemPrompt != null) {
            String endpoint = editApiEndpoint.getText().toString().trim();
            String apiKey = editApiKey.getText().toString().trim();
            String systemPrompt = editSystemPrompt.getText().toString().trim();
            
            Log.d("SettingsActivity", "测试前 - endpoint: " + endpoint);
            Log.d("SettingsActivity", "测试前 - apiKey: " + (apiKey.isEmpty() ? "空" : "已设置"));
            Log.d("SettingsActivity", "测试前 - systemPrompt: " + systemPrompt);
            
            if (endpoint.isEmpty() || apiKey.isEmpty() || systemPrompt.isEmpty()) {
                Toast.makeText(this, "请先填写所有AI配置项", Toast.LENGTH_SHORT).show();
                Log.d("SettingsActivity", "测试失败：配置项为空");
                return;
            }
            
            // 验证API endpoint格式
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                Toast.makeText(this, "API Endpoint必须以http://或https://开头", Toast.LENGTH_SHORT).show();
                Log.d("SettingsActivity", "测试失败：API Endpoint格式错误");
                return;
            }
            
            Log.d("SettingsActivity", "测试前 - 配置验证通过，开始测试API");
            
            // 显示测试进度
            Toast.makeText(this, "正在测试API连接...", Toast.LENGTH_SHORT).show();
            
            // 创建AIService实例进行测试
            final AIService aiService = new AIService(SettingsActivity.this);
            
            // 获取模型列表来测试API可用性
            aiService.getModels(new AIService.ModelsCallback() {
                    @Override
                    public void onSuccess(List<String> models) {
                        runOnUiThread(new Runnable() {
                            @Override
                    public void run() {
                        Log.d("SettingsActivity", "API测试成功，找到模型数量: " + (models != null ? models.size() : 0));
                        if (models != null && !models.isEmpty()) {
                            String modelInfo = "找到 " + models.size() + " 个可用模型";
                            if (models.size() > 0) {
                                modelInfo += "，如: " + models.get(0);
                            }
                            Toast.makeText(SettingsActivity.this, "API测试成功！" + modelInfo, Toast.LENGTH_LONG).show();
                            
                            // 显示模型选择对话框，让用户自己选择
                            showModelSelectionDialog(models);
                        } else {
                            Toast.makeText(SettingsActivity.this, "API测试成功，但未找到可用模型", Toast.LENGTH_SHORT).show();
                        }
                        // 关闭AIService
                        aiService.shutdown();
                    }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.d("SettingsActivity", "API测试失败：" + error);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingsActivity.this, "API测试失败：" + error, Toast.LENGTH_LONG).show();
                                // 关闭AIService
                                aiService.shutdown();
                            }
                        });
                    }
                });
        }
    }
    
    private void selectModel() {
        if (editApiEndpoint == null || editApiKey == null) {
            return;
        }
        
        String endpoint = editApiEndpoint.getText().toString().trim();
        String apiKey = editApiKey.getText().toString().trim();
        
        if (endpoint.isEmpty() || apiKey.isEmpty()) {
            Toast.makeText(this, "请先填写API Endpoint和API Key", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示进度
        Toast.makeText(this, "正在获取模型列表...", Toast.LENGTH_SHORT).show();
        
        final AIService aiService = new AIService(SettingsActivity.this);
        aiService.getModels(new AIService.ModelsCallback() {
            @Override
            public void onSuccess(List<String> models) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aiService.shutdown();
                        showModelSelectionDialog(models);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        aiService.shutdown();
                        Toast.makeText(SettingsActivity.this, "获取模型列表失败：" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    
    private void showModelSelectionDialog(List<String> models) {
        if (models == null || models.isEmpty()) {
            Toast.makeText(this, "没有可用模型", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String[] modelArray = models.toArray(new String[0]);
        String currentModel = editModel != null ? editModel.getText().toString().trim() : "";
        int checkedItem = -1;
        
        // 找到当前选中的模型
        for (int i = 0; i < modelArray.length; i++) {
            if (modelArray[i].equals(currentModel)) {
                checkedItem = i;
                break;
            }
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("选择模型")
                .setSingleChoiceItems(modelArray, checkedItem, new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (editModel != null) {
                            editModel.setText(modelArray[which]);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    private void setupUserInfo() {
        TextView tvUserInfo = findViewById(R.id.tv_user_info);
        if (tvUserInfo != null) {
            if (userManager != null && userManager.isLoggedIn()) {
                String username = userManager.getCurrentUsername();
                tvUserInfo.setText("当前用户：" + username);
            } else {
                tvUserInfo.setText("当前用户：未登录");
            }
        }
    }
    
    private void setupLogoutButton() {
        MaterialButton btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userManager != null && userManager.isLoggedIn()) {
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
    
    private void setupAISettings() {
        editApiEndpoint = findViewById(R.id.edit_api_endpoint);
        editApiKey = findViewById(R.id.edit_api_key);
        editSystemPrompt = findViewById(R.id.edit_system_prompt);
        editModel = findViewById(R.id.edit_model);
        btnSaveAISettings = findViewById(R.id.btn_save_ai_settings);
        btnTestAISettings = findViewById(R.id.btn_test_ai_settings);
        btnSelectModel = findViewById(R.id.btn_select_model);
        
        // 加载现有的AI配置
        Log.d("SettingsActivity", "=== 加载现有配置 ===");
        if (editApiEndpoint != null) {
            String endpoint = aiConfigManager.getApiEndpoint();
            editApiEndpoint.setText(endpoint);
            Log.d("SettingsActivity", "加载配置 - endpoint: " + endpoint);
        }
        if (editApiKey != null) {
            String apiKey = aiConfigManager.getApiKey();
            editApiKey.setText(apiKey);
            Log.d("SettingsActivity", "加载配置 - apiKey: " + (apiKey.isEmpty() ? "空" : "已设置"));
        }
        if (editSystemPrompt != null) {
            String systemPrompt = aiConfigManager.getSystemPrompt();
            editSystemPrompt.setText(systemPrompt);
            Log.d("SettingsActivity", "加载配置 - systemPrompt: " + systemPrompt);
        }
        if (editModel != null) {
            String model = aiConfigManager.getModel();
            if (model != null && !model.isEmpty()) {
                editModel.setText(model);
                Log.d("SettingsActivity", "加载配置 - model: " + model);
            } else {
                Log.d("SettingsActivity", "加载配置 - model: 空");
            }
        }
        
        // 设置保存按钮点击事件
        if (btnSaveAISettings != null) {
            btnSaveAISettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAISettings();
                }
            });
        }
        
        // 设置测试按钮点击事件
        if (btnTestAISettings != null) {
            btnTestAISettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testAISettings();
                }
            });
        }
        
        // 设置模型选择按钮点击事件
        if (btnSelectModel != null) {
            btnSelectModel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectModel();
                }
            });
        }
    }
    
    private void saveAISettings() {
        Log.d("SettingsActivity", "=== saveAISettings 开始 ===");
        if (editApiEndpoint != null && editApiKey != null && editSystemPrompt != null) {
            String endpoint = editApiEndpoint.getText().toString().trim();
            String apiKey = editApiKey.getText().toString().trim();
            String systemPrompt = editSystemPrompt.getText().toString().trim();
            String model = editModel != null ? editModel.getText().toString().trim() : "";
            
            Log.d("SettingsActivity", "保存前 - endpoint: " + endpoint);
            Log.d("SettingsActivity", "保存前 - apiKey: " + (apiKey.isEmpty() ? "空" : "已设置"));
            Log.d("SettingsActivity", "保存前 - systemPrompt: " + systemPrompt);
            Log.d("SettingsActivity", "保存前 - model: " + (model.isEmpty() ? "空" : model));
            
            if (endpoint.isEmpty() || apiKey.isEmpty() || systemPrompt.isEmpty()) {
                Toast.makeText(this, "请填写所有AI配置项", Toast.LENGTH_SHORT).show();
                Log.d("SettingsActivity", "保存失败：配置项为空");
                return;
            }
            
            // 验证API endpoint格式
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                Toast.makeText(this, "API Endpoint必须以http://或https://开头", Toast.LENGTH_SHORT).show();
                Log.d("SettingsActivity", "保存失败：API Endpoint格式错误");
                return;
            }
            
            // 直接保存配置，模型可以为空
            completeSaveConfiguration(endpoint, apiKey, systemPrompt, model);
        }
    }
    
    private void completeSaveConfiguration(String endpoint, String apiKey, String systemPrompt, String model) {
        // 保存配置
        aiConfigManager.setApiEndpoint(endpoint);
        aiConfigManager.setApiKey(apiKey);
        aiConfigManager.setSystemPrompt(systemPrompt);
        aiConfigManager.setModel(model);
        
        Log.d("SettingsActivity", "配置已保存");
        Toast.makeText(this, "AI设置已保存", Toast.LENGTH_SHORT).show();
        
        // 检查配置是否完整
        boolean isComplete = aiConfigManager.isConfigComplete();
        Log.d("SettingsActivity", "配置完整性检查: " + isComplete);
        if (isComplete) {
            Toast.makeText(this, "配置验证通过，可以开始使用AI助手", Toast.LENGTH_SHORT).show();
            
            // 如果模型为空，提示用户可以选择模型
            if (model.isEmpty()) {
                Toast.makeText(this, "您可以选择一个模型，或直接开始使用默认模型", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "配置不完整，需要API Key和Endpoint", Toast.LENGTH_SHORT).show();
        }
    }
}