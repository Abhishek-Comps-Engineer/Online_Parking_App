package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.adapter.ChatAdapter;
import com.example.abhishek.financetracker.expensemanager.neopark.classes.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class HelpAndSupportActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText userMessageEditText;
    private Button sendMessageButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        userMessageEditText = findViewById(R.id.userMessageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        chatMessageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessageList);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = userMessageEditText.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    chatMessageList.add(new ChatMessage(userMessage, true)); // User message

                    String botReply = getBotResponse(userMessage); // Bot logic
                    chatMessageList.add(new ChatMessage(botReply, false));  // Bot response

                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                    userMessageEditText.setText("");
                }
            }
        });



        ImageView backButton = findViewById(R.id.vbutton);
        backButton.setOnClickListener(view -> onBackPressed());

    }

    private String getBotResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("available") && message.contains("slot")) {
            return "Yes, we have several parking slots available right now.";
        } else if (message.contains("how much") || message.contains("charge") || message.contains("fee")) {
            return "The parking fee is ₹200 per hour.";
        } else if (message.contains("location") || message.contains("where")) {
            return "We are located near the main city center, next to ABC Mall.";
        } else if (message.contains("book") || message.contains("reserve")) {
            return "You can reserve a parking slot using our 'Add Plot' feature on the map.";
        } else if (message.contains("open") || message.contains("timing")) {
            return "Our parking facility is open 24/7.";
        } else if (message.contains("security") || message.contains("safe")) {
            return "Yes, our parking area is under 24-hour CCTV surveillance.";
        } else if (message.contains("contact")) {
            return "You can contact our support at 7058746794.";
        } else {
            return "Sorry, I didn't understand that. Please ask something related to car parking.";
        }
    }

}
