package com.sumit.chatapp;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.sumit.firebasechat.FirebaseChatApp;
import com.sumit.firebasechat.PrefsHelper;
import com.sumit.firebasechat.User;
import com.sumit.firebasechat.onRetrieveUserList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements onRetrieveUserList {


    @BindView(R.id.progress)
    ProgressBar progress;

    ArrayList<User> userList;
    PrefsHelper helper;
    String device_id;


    UserAdapter adapter;


    @BindView(R.id.recyclerView)
    RecyclerView listing;
    private Set<User> arrayList=new HashSet<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();

    }

    private void init() {

        userList=new ArrayList<User>();
        helper=new PrefsHelper(MainActivity.this);
        device_id=helper.getPref(Constant.DEVICE_ID,"");

        final LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(android.support.v7.widget.LinearLayoutManager.VERTICAL);
        listing.setLayoutManager(manager);

        adapter=new UserAdapter(MainActivity.this,userList);
        listing.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        userList.clear();
        arrayList.clear();
        progress.setVisibility(View.VISIBLE);
        FirebaseChatApp.retrieveUserList(this,device_id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRetriverUser(User user, String message) {

        if(progress.getVisibility()==View.VISIBLE)
                    progress.setVisibility(View.GONE);

        //userList.add(user);


        if(userList.isEmpty()){
            userList.add(user);
        } else{

                int m=-1;
                for(int i=0;i<userList.size();i++){
                    if(user.getUser_id().equals(userList.get(i).getUser_id())){
                        m=i;
                    }
                }

                if(m==-1) {
                    userList.add(user);
                }else {
                    userList.remove(m);
                    userList.add(m,user);

                }
                adapter.notifyDataSetChanged();
            }


        }





    @Override
    public void onChildChanged(User user) {

    }

    @Override
    public void onChildRemoved(User user) {

    }


}
