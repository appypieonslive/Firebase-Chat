package com.sumit.chatapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.sumit.firebasechat.FirebaseChatApp;
import com.sumit.firebasechat.Message;
import com.sumit.firebasechat.PrefsHelper;
import com.sumit.firebasechat.onRetrieveMessage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendMessageActivity extends AppCompatActivity implements onRetrieveMessage {


    private DatabaseReference mDatabase;
    MessageAdapter messageAdapter;

    @BindView(R.id.message_listing)
    RecyclerView listing;

    String senderId;
    String receiverId;

    @BindView(R.id.input)
    EditText input;

    ArrayList<Message> messageList;

    PrefsHelper helper;

    @OnClick(R.id.submit)
    public void onClick(){
        String message=input.getText().toString();
        FirebaseChatApp.sendMessage(senderId,receiverId,message);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);


        init();

    }

    private void init() {

        receiverId=getIntent().getStringExtra("receiver");
        messageList=new ArrayList<>();
        final LinearLayoutManager manager = new LinearLayoutManager(SendMessageActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listing.setLayoutManager(manager);

        helper=new PrefsHelper(SendMessageActivity.this);
        senderId=helper.getPref(Constant.DEVICE_ID,"");


        messageAdapter=new MessageAdapter(messageList,SendMessageActivity.this,senderId);
        listing.setAdapter(messageAdapter);

        retrieveMessage();
    }

    private void retrieveMessage() {
        FirebaseChatApp.retrieveMessage(this,senderId,receiverId);
    }



    @Override
    public void pnRetriverUserAdd(Message message) {
        messageList.add(message);
        messageAdapter.notifyDataSetChanged();
        listing.scrollToPosition(messageAdapter.getItemCount()-1);
    }

    @Override
    public void pnRetriverUserRemove(Message message) {
        messageList.remove(message);
        messageAdapter.notifyDataSetChanged();
        listing.scrollToPosition(messageAdapter.getItemCount()-1);
    }
}
