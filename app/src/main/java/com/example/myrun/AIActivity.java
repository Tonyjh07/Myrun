package com.example.myrun;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrun.adapter.ChatMessageAdapter;
import com.example.myrun.model.ChatMessage;
import com.example.myrun.util.AIConfigManager;
import com.example.myrun.util.AIService;
import com.example.myrun.util.StatusBarUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AIActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private TextInputEditText editTextMessage;
    private MaterialButton buttonSend;
    private ChatMessageAdapter messageAdapter;
    private List<ChatMessage> messageList;
    private AIService aiService;
    private MaterialToolbar toolbar;
    private AIConfigManager aiConfigManager;

    private static final String KEY_CONVERSATION_HISTORY = "conversation_history";
    private static final String KEY_CONVERSATION_TIMESTAMP = "conversation_timestamp";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 设置系统状态栏让出空间
        StatusBarUtil.setSystemStatusBar(this);
        setContentView(R.layout.activity_aiactivity);
        
        // 为根布局设置系统栏内边距
        StatusBarUtil.setupViewPadding(findViewById(R.id.ai));
        
        // 初始化视图
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);
        
        // 设置窗口插入处理，确保输入法不会遮挡内容
        setupWindowInsets();
        
        // 初始化AI配置管理器
        aiConfigManager = AIConfigManager.getInstance(this);
        
        // 调试：显示当前配置状态
        Log.d("AIActivity", "=== AI配置状态 ===");
        Log.d("AIActivity", "API Key: " + (aiConfigManager.getApiKey().isEmpty() ? "空" : "已设置"));
        Log.d("AIActivity", "Endpoint: " + aiConfigManager.getApiEndpoint());
        Log.d("AIActivity", "Model: " + aiConfigManager.getModel());
        Log.d("AIActivity", "System Prompt: " + aiConfigManager.getSystemPrompt());
        
        // 检查AI配置是否完整
        if (!aiConfigManager.isConfigComplete()) {
            Log.d("AIActivity", "配置不完整，跳转到设置页面");
            Toast.makeText(this, "请先配置AI服务", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        Log.d("AIActivity", "配置完整，继续初始化");
        
        // 初始化AI服务
        aiService = new AIService(this);
        
        // 初始化视图
        initViews();
        
        // 设置工具栏
        setupToolbar();
        
        // 初始化聊天列表
        setupChatList();
        
        // 恢复对话历史
        restoreConversationHistory();
        
        // 设置事件监听器
        setupListeners();
        
        // 如果没有历史消息，添加欢迎消息
        if (messageList.isEmpty()) {
            addWelcomeMessage();
        }
    }
    
    private void initViews() {
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);
        toolbar = findViewById(R.id.toolbar);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupChatList() {
        messageList = new ArrayList<>();
        messageAdapter = new ChatMessageAdapter(messageList);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }
    
    private void setupListeners() {
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // 设置回车键发送消息
        editTextMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
        
        // 监听输入框焦点变化，获得焦点时滚动到底部
        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && messageAdapter.getItemCount() > 0) {
                Log.d("AIActivity", "输入框获得焦点，滚动到底部");
                recyclerViewMessages.postDelayed(() -> {
                    recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                }, 300);
            }
        });
        
        // 监听输入框文本变化，自动滚动到底部
        editTextMessage.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(android.text.Editable s) {
                // 当输入框内容变化时，确保能看到最新消息
                if (messageAdapter.getItemCount() > 0) {
                    recyclerViewMessages.postDelayed(() -> {
                        recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }, 100);
                }
            }
        });
        
        // 注意：软键盘处理已移至setupWindowInsets()方法，使用WindowInsets API更可靠
    }
    
    private void addWelcomeMessage() {
        ChatMessage welcomeMessage = new ChatMessage(
            "你好！我是你的AI跑步助手，可以帮你解答关于跑步、健身和健康的问题。有什么可以帮助你的吗？",
            ChatMessage.MessageType.AI
        );
        messageList.add(welcomeMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        
        // 保存对话历史
        saveConversationHistory();
    }
    
    private void saveConversationHistory() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // 将消息列表转换为JSON字符串
        JSONArray jsonArray = new JSONArray();
        for (ChatMessage message : messageList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("content", message.getContent());
                jsonObject.put("type", message.getType().name());
                jsonObject.put("timestamp", message.getTimestamp());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e("AIActivity", "保存消息失败: " + e.getMessage());
            }
        }
        
        editor.putString(KEY_CONVERSATION_HISTORY, jsonArray.toString());
        editor.putLong(KEY_CONVERSATION_TIMESTAMP, System.currentTimeMillis());
        editor.apply();
    }
    
    private void restoreConversationHistory() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_CONVERSATION_HISTORY, null);
        long lastTimestamp = prefs.getLong(KEY_CONVERSATION_TIMESTAMP, 0);
        
        // 如果超过24小时，清除历史记录
        if (System.currentTimeMillis() - lastTimestamp > 24 * 60 * 60 * 1000) {
            prefs.edit().remove(KEY_CONVERSATION_HISTORY).remove(KEY_CONVERSATION_TIMESTAMP).apply();
            return;
        }
        
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String content = jsonObject.getString("content");
                    ChatMessage.MessageType type = ChatMessage.MessageType.valueOf(jsonObject.getString("type"));
                    long timestamp = jsonObject.getLong("timestamp");
                    
                    ChatMessage message = new ChatMessage(content, type);
                    message.setTimestamp(timestamp);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
                
                // 滚动到底部
                if (messageList.size() > 0) {
                    recyclerViewMessages.postDelayed(() -> {
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }, 100);
                }
            } catch (JSONException e) {
                Log.e("AIActivity", "恢复消息失败: " + e.getMessage());
            }
        }
    }
    
    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        
        if (message.isEmpty()) {
            return;
        }
        
        // 隐藏软键盘
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextMessage.getWindowToken(), 0);
        
        // 添加用户消息到列表
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.MessageType.USER);
        messageList.add(userMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        
        // 保存对话历史
        saveConversationHistory();
        
        // 清空输入框
        editTextMessage.setText("");
        
        // 滚动到底部，延迟稍长确保键盘收起后再滚动
        recyclerViewMessages.postDelayed(() -> {
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            Log.d("AIActivity", "发送消息后滚动到底部");
        }, 200);
        
        // 显示加载状态
        showLoadingMessage();
        
        // 发送消息到AI（使用流式回复）
        aiService.sendStreamingMessage(message, messageList, new AIService.StreamingAIResponseCallback() {
            private ChatMessage streamingMessage;
            private int messagePosition;
            
            @Override
            public void onStreamChunk(String chunk) {
                runOnUiThread(() -> {
                    if (streamingMessage == null) {
                        // 第一次收到数据，移除加载消息并创建流式消息
                        removeLoadingMessage();
                        streamingMessage = new ChatMessage(chunk, ChatMessage.MessageType.AI, true);
                        messageList.add(streamingMessage);
                        messagePosition = messageList.size() - 1;
                        messageAdapter.notifyItemInserted(messagePosition);
                        recyclerViewMessages.scrollToPosition(messagePosition);
                    } else {
                        // 追加内容到现有消息
                        String currentContent = streamingMessage.getContent();
                        streamingMessage.setContent(currentContent + chunk);
                        messageAdapter.notifyItemChanged(messagePosition);
                        recyclerViewMessages.scrollToPosition(messagePosition);
                    }
                });
            }
            
            @Override
            public void onStreamComplete() {
                runOnUiThread(() -> {
                    if (streamingMessage != null) {
                        streamingMessage.setComplete(true);
                        streamingMessage.setStreaming(false);
                        messageAdapter.notifyItemChanged(messagePosition);
                        Log.d("AIActivity", "流式回复完成");
                    }
                });
            }
            
            @Override
            public void onStreamError(String error) {
                runOnUiThread(() -> {
                    removeLoadingMessage();
                    // 显示更友好的错误提示
                    String friendlyError = "抱歉，AI助手暂时无法回复。";
                    if (error.contains("网络错误") || error.contains("流式连接失败")) {
                        friendlyError = "网络连接失败，请检查网络连接。";
                    } else if (error.contains("API错误")) {
                        friendlyError = "AI服务暂时不可用，请稍后再试。";
                    } else if (error.contains("配置")) {
                        friendlyError = "请在设置中配置AI服务。";
                    }
                    
                    ChatMessage errorMessage = new ChatMessage(friendlyError, ChatMessage.MessageType.AI);
                    messageList.add(errorMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.postDelayed(() -> {
                        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                    }, 100);
                });
            }
        });
    }
    
    private void showLoadingMessage() {
        ChatMessage loadingMessage = new ChatMessage("正在思考...", ChatMessage.MessageType.AI);
        messageList.add(loadingMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.postDelayed(() -> {
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        }, 100);
    }
    
    private void removeLoadingMessage() {
        if (!messageList.isEmpty() && 
            messageList.get(messageList.size() - 1).getContent().equals("正在思考...")) {
            messageList.remove(messageList.size() - 1);
            messageAdapter.notifyItemRemoved(messageList.size());
        }
    }
    
    private void clearConversationHistory() {
        // 清除内存中的消息列表
        messageList.clear();
        messageAdapter.notifyDataSetChanged();
        
        // 清除SharedPreferences中的历史记录
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        prefs.edit()
            .remove(KEY_CONVERSATION_HISTORY)
            .remove(KEY_CONVERSATION_TIMESTAMP)
            .apply();
        
        // 添加欢迎消息
        addWelcomeMessage();
        
        // 显示提示
        Toast.makeText(this, "已清除对话历史", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ai_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // 跳转到设置页面
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_clear_history) {
            // 清除历史记录
            clearConversationHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ai), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int imeHeight = imeInsets.bottom;

            // 仅调整输入框容器的padding，移除对RecyclerView的动态调整
            View inputContainer = findViewById(R.id.input_container);
            inputContainer.setPadding(
                inputContainer.getPaddingLeft(),
                inputContainer.getPaddingTop(),
                inputContainer.getPaddingRight(),
                Math.max(imeHeight, systemBarsInsets.bottom)
            );

            // 确保RecyclerView滚动到底部
            if (imeHeight > 0) {
                recyclerViewMessages.post(() -> {
                    if (messageAdapter.getItemCount() > 0) {
                        recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                });
            }

            return insets;
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aiService != null) {
            aiService.shutdown();
        }
    }
}