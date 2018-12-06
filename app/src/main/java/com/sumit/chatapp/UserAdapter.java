package com.sumit.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sumit.firebasechat.User;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<User> data;

    private Context mContext;

    public UserAdapter(Context activity, ArrayList<User> data1) {
        data = data1;
        mContext = activity;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int pos) {

       viewHolder.txt.setText(data.get(pos).getName());
       viewHolder.last.setText(data.get(pos).getLast_message());


       if(!data.get(pos).getProfile_pic().isEmpty()){
           Picasso.with(mContext).load(data.get(pos).getProfile_pic()).placeholder(R.drawable.place_holder).into(viewHolder.image);
       }else{
           viewHolder.image.setImageResource(R.drawable.place_holder);
       }



        viewHolder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent in=new Intent(mContext,SendMessageActivity.class);
                in.putExtra("receiver",data.get(pos).getUser_id());
                mContext.startActivity(in);


            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.row_user, viewGroup, false);
        return new ViewHolder(view);
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt)
        TextView txt;

        @BindView(R.id.last)
        TextView last;

        @BindView(R.id.image)
        CircleImageView image;

        public ViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);

        }
    }
}
