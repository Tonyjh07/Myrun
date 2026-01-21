package com.example.myrun.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

/**
 * AI服务类，用于处理与AI API的通信
 */
public class AIService {
    
    private static final String TAG = "AIService";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final ExecutorService executorService;
    private final Handler mainHandler;
    private final AIConfigManager configManager;
    
    public AIService(Context context) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.configManager = AIConfigManager.getInstance(context);
    }
    
    /**
     * 获取可用模型列表
     */
    public void getModels(final ModelsCallback callback) {
        Log.d(TAG, "开始获取模型列表");
        
        if (!configManager.isConfigComplete()) {
            Log.e(TAG, "配置不完整");
            mainHandler.post(() -> callback.onError("请先配置API设置"));
            return;
        }
        
        executorService.execute(() -> {
            try {
                String apiEndpoint = configManager.getApiEndpoint();
                String apiKey = configManager.getApiKey();
                
                // 构建模型列表请求URL
                String modelsEndpoint = apiEndpoint;
                if (apiEndpoint.endsWith("/chat/completions")) {
                    modelsEndpoint = apiEndpoint.replace("/chat/completions", "/models");
                } else if (!apiEndpoint.endsWith("/models")) {
                    // 如果endpoint不是以/models结尾，添加/models
                    if (apiEndpoint.endsWith("/")) {
                        modelsEndpoint = apiEndpoint + "models";
                    } else {
                        modelsEndpoint = apiEndpoint + "/models";
                    }
                }
                
                Log.d(TAG, "模型列表请求URL: " + modelsEndpoint);
                
                Request request = new Request.Builder()
                        .url(modelsEndpoint)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .get()
                        .build();
                
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "网络请求失败: " + e.getMessage(), e);
                        mainHandler.post(() -> callback.onError("网络错误: " + e.getMessage()));
                    }
                    
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d(TAG, "模型列表响应码: " + response.code());
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.d(TAG, "模型列表响应体: " + responseBody);
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                
                                if (jsonResponse.has("data")) {
                                    JSONArray modelsArray = jsonResponse.getJSONArray("data");
                                    List<String> models = new ArrayList<>();
                                    
                                    for (int i = 0; i < modelsArray.length(); i++) {
                                        JSONObject modelObj = modelsArray.getJSONObject(i);
                                        if (modelObj.has("id")) {
                                            String modelId = modelObj.getString("id");
                                            models.add(modelId);
                                        }
                                    }
                                    
                                    mainHandler.post(() -> callback.onSuccess(models));
                                } else {
                                    mainHandler.post(() -> callback.onError("模型列表响应格式错误"));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON解析失败: " + e.getMessage(), e);
                                mainHandler.post(() -> callback.onError("解析模型列表失败: " + e.getMessage()));
                            }
                        } else {
                            String errorBody = response.body().string();
                            Log.e(TAG, "模型列表API错误响应: " + errorBody);
                            
                            // 尝试解析错误信息
                            String errorMessage = "API错误: " + response.code() + " " + response.message();
                            try {
                                JSONObject errorJson = new JSONObject(errorBody);
                                if (errorJson.has("error")) {
                                    JSONObject errorObj = errorJson.getJSONObject("error");
                                    if (errorObj.has("message")) {
                                        errorMessage = errorObj.getString("message");
                                    } else if (errorObj.has("type")) {
                                        errorMessage = errorObj.getString("type") + ": " + errorObj.optString("message", response.message());
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "无法解析错误响应: " + e.getMessage());
                            }
                            
                            final String finalErrorMessage = errorMessage;
                            mainHandler.post(() -> callback.onError(finalErrorMessage));
                        }
                    }
                });
                
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("构建请求失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 发送消息到AI API
     */
    public void sendMessage(String message, AIResponseCallback callback) {
        Log.d(TAG, "开始发送消息: " + message);
        
        if (!configManager.isConfigComplete()) {
            Log.e(TAG, "配置不完整");
            mainHandler.post(() -> callback.onError("请先配置API设置"));
            return;
        }
        
        Log.d(TAG, "API Endpoint: " + configManager.getApiEndpoint());
        Log.d(TAG, "API Key: " + (configManager.getApiKey().isEmpty() ? "空" : "已设置"));
        Log.d(TAG, "System Prompt: " + configManager.getSystemPrompt());
        
        executorService.execute(() -> {
            try {
                String apiEndpoint = configManager.getApiEndpoint();
                String apiKey = configManager.getApiKey();
                String systemPrompt = configManager.getSystemPrompt();
                
                JSONObject requestBody = new JSONObject();
                
                // 使用配置的模型，如果没有配置则使用默认模型
                String model = configManager.getModel();
                if (model == null || model.isEmpty()) {
                    // 使用默认模型
                    model = "gpt-3.5-turbo";
                    Log.d(TAG, "模型为空，使用默认模型: " + model);
                } else {
                    Log.d(TAG, "使用配置模型: " + model);
                }
                requestBody.put("model", model);
                
                JSONArray messages = new JSONArray();
                
                // 添加系统提示
                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messages.put(systemMessage);
                
                // 添加用户消息
                JSONObject userMessage = new JSONObject();
                userMessage.put("role", "user");
                userMessage.put("content", message);
                messages.put(userMessage);
                
                requestBody.put("messages", messages);
                requestBody.put("max_tokens", 1000);
                requestBody.put("temperature", 0.7);
                
                RequestBody body = RequestBody.create(requestBody.toString(), JSON);
                
                Log.d(TAG, "请求体: " + requestBody.toString());
                
                Request request = new Request.Builder()
                        .url(apiEndpoint)
                        .addHeader("Authorization", "Bearer " + apiKey)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
                
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e(TAG, "网络请求失败: " + e.getMessage(), e);
                        mainHandler.post(() -> callback.onError("网络错误: " + e.getMessage()));
                    }
                    
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d(TAG, "响应码: " + response.code());
                        if (response.isSuccessful()) {
                            String responseBody = response.body().string();
                            Log.d(TAG, "响应体: " + responseBody);
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                
                                // 检查是否有错误信息
                                if (jsonResponse.has("error")) {
                                    JSONObject errorObj = jsonResponse.getJSONObject("error");
                                    String errorMessage = errorObj.getString("message");
                                    mainHandler.post(() -> callback.onError("API错误: " + errorMessage));
                                    return;
                                }
                                
                                JSONArray choices = jsonResponse.getJSONArray("choices");
                                if (choices.length() > 0) {
                                    String aiMessage = choices.getJSONObject(0)
                                            .getJSONObject("message")
                                            .getString("content");
                                    mainHandler.post(() -> callback.onSuccess(aiMessage.trim()));
                                } else {
                                    mainHandler.post(() -> callback.onError("AI响应格式错误"));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON解析失败: " + e.getMessage(), e);
                                mainHandler.post(() -> callback.onError("解析响应失败: " + e.getMessage()));
                            }
                        } else {
                            String errorBody = response.body().string();
                            Log.e(TAG, "API错误响应: " + errorBody);
                            
                            // 尝试解析错误信息
                            String errorMessage = "API错误: " + response.code() + " " + response.message();
                            try {
                                JSONObject errorJson = new JSONObject(errorBody);
                                if (errorJson.has("error")) {
                                    JSONObject errorObj = errorJson.getJSONObject("error");
                                    if (errorObj.has("message")) {
                                        errorMessage = errorObj.getString("message");
                                    } else if (errorObj.has("type")) {
                                        errorMessage = errorObj.getString("type") + ": " + errorObj.optString("message", response.message());
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "无法解析错误响应: " + e.getMessage());
                            }
                            
                            final String finalErrorMessage = errorMessage;
                            mainHandler.post(() -> callback.onError(finalErrorMessage));
                        }
                    }
                });
                
            } catch (JSONException e) {
                mainHandler.post(() -> callback.onError("构建请求失败: " + e.getMessage()));
            }
        });
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        executorService.shutdown();
    }
    
    /**
     * AI响应回调接口
     */
    public interface AIResponseCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    /**
     * 模型列表回调接口
     */
    public interface ModelsCallback {
        void onSuccess(List<String> models);
        void onError(String error);
    }
}