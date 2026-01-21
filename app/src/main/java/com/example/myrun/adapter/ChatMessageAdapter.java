package com.example.myrun.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myrun.R;
import com.example.myrun.model.ChatMessage;

import java.util.List;

/**
 * 聊天消息适配器
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
    
    private List<ChatMessage> messageList;
    
    public ChatMessageAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ChatMessage.MessageType.USER.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ai_message, parent, false);
        }
        return new MessageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        holder.bind(message);
    }
    
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType().ordinal();
    }
    
    public void addMessage(ChatMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
        
        public void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            
            // 如果是流式消息且未完成，添加打字机效果
            if (message.isStreaming() && !message.isComplete()) {
                // 添加光标效果
                String currentText = message.getContent();
                if (!currentText.endsWith("▋")) {
                    messageText.setText(currentText + "▋");
                }
            }
        }
    }
}