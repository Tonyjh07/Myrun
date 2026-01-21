package com.example.myrun.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * AI配置管理类，用于管理AI对话的配置信息
 */
public class AIConfigManager {
    
    private static final String PREF_NAME = "ai_config_prefs";
    private static final String KEY_API_ENDPOINT = "api_endpoint";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_SYSTEM_PROMPT = "system_prompt";
    private static final String KEY_MODEL = "model";
    
    private static final String DEFAULT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_SYSTEM_PROMPT = "你是一个智能跑步助手，可以帮助用户解答关于跑步、健身、健康等方面的问题。请用中文回答，回答要简洁友好。";
    
    private SharedPreferences sharedPreferences;
    private static AIConfigManager instance;
    
    private AIConfigManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * 获取AIConfigManager单例
     */
    public static synchronized AIConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new AIConfigManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * 获取API Endpoint
     */
    public String getApiEndpoint() {
        return sharedPreferences.getString(KEY_API_ENDPOINT, DEFAULT_API_ENDPOINT);
    }
    
    /**
     * 设置API Endpoint
     */
    public void setApiEndpoint(String apiEndpoint) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_ENDPOINT, apiEndpoint);
        editor.apply();
    }
    
    /**
     * 获取API Key
     */
    public String getApiKey() {
        return sharedPreferences.getString(KEY_API_KEY, "");
    }
    
    /**
     * 设置API Key
     */
    public void setApiKey(String apiKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_API_KEY, apiKey);
        editor.apply();
    }
    
    /**
     * 获取System Prompt
     */
    public String getSystemPrompt() {
        return sharedPreferences.getString(KEY_SYSTEM_PROMPT, DEFAULT_SYSTEM_PROMPT);
    }
    
    /**
     * 设置System Prompt
     */
    public void setSystemPrompt(String systemPrompt) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SYSTEM_PROMPT, systemPrompt);
        editor.apply();
    }
    
    /**
     * 获取模型
     */
    public String getModel() {
        return sharedPreferences.getString(KEY_MODEL, "");
    }
    
    /**
     * 设置模型
     */
    public void setModel(String model) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MODEL, model);
        editor.apply();
    }
    
    /**
     * 检查配置是否完整 - 只需要API Key和Endpoint，模型可以为空
     */
    public boolean isConfigComplete() {
        String apiKey = getApiKey();
        String endpoint = getApiEndpoint();
        String model = getModel();
        
        Log.d("AIConfigManager", "检查配置完整性:");
        Log.d("AIConfigManager", "API Key: " + (apiKey.isEmpty() ? "空" : "已设置"));
        Log.d("AIConfigManager", "Endpoint: " + (endpoint.isEmpty() ? "空" : endpoint));
        Log.d("AIConfigManager", "Model: " + (model.isEmpty() ? "空" : model));
        
        // 只需要API Key和Endpoint，模型可以为空
        boolean isComplete = !apiKey.isEmpty() && !endpoint.isEmpty();
        Log.d("AIConfigManager", "配置完整性: " + isComplete + " (只需要API Key和Endpoint)");
        
        return isComplete;
    }
}