package com.example.myrun.model;

/**
 * 聊天消息模型类
 */
public class ChatMessage {
    
    public enum MessageType {
        USER,
        AI
    }
    
    private String content;
    private MessageType type;
    private long timestamp;
    private boolean isStreaming; // 是否为流式消息
    private boolean isComplete; // 流式消息是否完成
    
    public ChatMessage(String content, MessageType type) {
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isStreaming = false;
        this.isComplete = true;
    }
    
    public ChatMessage(String content, MessageType type, boolean isStreaming) {
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isStreaming = isStreaming;
        this.isComplete = !isStreaming;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public boolean isStreaming() {
        return isStreaming;
    }
    
    public void setStreaming(boolean streaming) {
        isStreaming = streaming;
    }
    
    public boolean isComplete() {
        return isComplete;
    }
    
    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}