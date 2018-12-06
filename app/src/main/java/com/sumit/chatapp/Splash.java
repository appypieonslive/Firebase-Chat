package com.sumit.chatapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.sumit.firebasechat.FirebaseChatApp;
import com.sumit.firebasechat.FirebaseLoginRegister;
import com.sumit.firebasechat.PrefsHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Splash extends AppCompatActivity implements FirebaseLoginRegister{

   @BindView(R.id.progress)
    ProgressBar progress;


    private static final int PERMISSION_DEVICE_ID = 100;
    private String deviceId;
    PrefsHelper helper;

    @BindView(R.id.edtName)
    EditText edtName;
    private String imagePath="";

    @OnClick(R.id.submit)
    public void onClick(){
        if(!edtName.getText().toString().isEmpty()){
            progress.setVisibility(View.VISIBLE);
            String name=edtName.getText().toString();
            String userId=deviceId;
            FirebaseChatApp.authoriseUser(Splash.this,name,userId,imagePath,this);
        }else{
            Toast.makeText(Splash.this,"Please enter the name",Toast.LENGTH_LONG).show();
        }
    }

    @BindView(R.id.image)
    ImageView image;

    @OnClick(R.id.image)
    public void onImageClick(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 2);

        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 123);
        }


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ButterKnife.bind(this);

        progress.setVisibility(View.GONE);
        FirebaseChatApp.intializeFirebase(getApplicationContext());

        helper=new PrefsHelper(Splash.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, PERMISSION_DEVICE_ID);
        } else {
            getDeviceId();
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceId() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = manager.getDeviceId();
        helper.savePref(Constant.DEVICE_ID,deviceId);

        String name=helper.getPref(Constant.NAME,"");

        if(name.isEmpty()){
        }else{
            FirebaseChatApp.signInUser(Splash.this,name,deviceId,this);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_DEVICE_ID) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                getDeviceId();
            }else if (requestCode == 2) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 123);
            } else {
                finish();
            }
        }
    }
    @Override
    public void firebaseRegister(String name, boolean value) {
        progress.setVisibility(View.GONE);
        if(value){
            helper.savePref(Constant.NAME,edtName.getText().toString());
            Intent in=new Intent(Splash.this,MainActivity.class);
            startActivity(in);
            finish();
        }else{
            FirebaseChatApp.signInUser(Splash.this,name,deviceId,this);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == 123) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        imagePath = Method.getPath(Splash.this, selectedImageUri);
                        System.out.println("Image Path : " + imagePath);
                        Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
                        image.setImageBitmap(myBitmap);

                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(Splash.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }
            }


        }catch (Exception e){

        }
    }

    @Override
    public void firebaseLogin(boolean value) {
        progress.setVisibility(View.GONE);
        if(value){
            Intent in=new Intent(Splash.this,MainActivity.class);
            startActivity(in);
            finish();
        }else{
            Toast.makeText(Splash.this,"unable to proceed",Toast.LENGTH_LONG).show();
        }
    }
}
