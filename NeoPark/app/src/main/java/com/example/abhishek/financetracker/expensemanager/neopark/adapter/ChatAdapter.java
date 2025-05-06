package com.example.abhishek.financetracker.expensemanager.neopark.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.financetracker.expensemanager.neopark.classes.ChatMessage;
import com.example.abhishek.financetracker.expensemanager.neopark.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessageList;

    public ChatAdapter(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        if (chatMessage.isUser()) {
            holder.userMessageTextView.setVisibility(View.VISIBLE);
            holder.userMessageTextView.setText(chatMessage.getMessage());
            holder.botMessageTextView.setVisibility(View.GONE);
        } else {
            holder.botMessageTextView.setVisibility(View.VISIBLE);
            holder.botMessageTextView.setText(chatMessage.getMessage());
            holder.userMessageTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageTextView, botMessageTextView;

        ChatViewHolder(View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.userMessageTextView);
            botMessageTextView = itemView.findViewById(R.id.botMessageTextView);
        }
    }
}
