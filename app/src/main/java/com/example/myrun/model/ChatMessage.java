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
    
    public ChatMessage(String content, MessageType type) {
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
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
}