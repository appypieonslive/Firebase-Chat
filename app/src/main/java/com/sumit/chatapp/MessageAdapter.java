package com.sumit.chatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumit.firebasechat.Message;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    ArrayList<Message> data;
    String myId;
    private Context mContext;

    public MessageAdapter(ArrayList<Message> data1, SendMessageActivity activity, String senderId) {
        data = data1;
        mContext = activity;
        myId=senderId;
    }

    @Override
    public void onBindViewHolder(final MessageAdapter.ViewHolder viewHolder, final int pos) {

        viewHolder.sender.setVisibility(View.GONE);
        viewHolder.receiver.setVisibility(View.GONE);


        if(myId.equals(data.get(pos).getFrom_user())){
            viewHolder.sender.setVisibility(View.VISIBLE);
            viewHolder.sender.setText(data.get(pos).getMessage());
        }else{
            viewHolder.receiver.setVisibility(View.VISIBLE);
            viewHolder.receiver.setText(data.get(pos).getMessage());
        }



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.row_message, viewGroup, false);
        return new MessageAdapter.ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.sender)
        TextView sender;

        @BindView(R.id.receiver)
        TextView receiver;

        public ViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);

        }
    }
}
