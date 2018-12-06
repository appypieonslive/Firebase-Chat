package com.sumit.firebasechat;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class FirebaseChatApp {

    ArrayList<User> userList=new ArrayList<>();



    public static void registerUser(String name, String userId, String imagePath) {


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constant.NODE_USER);
        User u=new User();
        u.setUser_id(userId);
        u.setActive_status("1");
        u.setTimestamp(System.currentTimeMillis());
        u.setName(name);
        u.setProfile_pic(imagePath);
        mDatabase.child(userId).setValue(u);


    }

    public static void intializeFirebase(Context splash) {
        FirebaseApp.initializeApp(splash);
    }


    public static void retrieveUserList(final onRetrieveUserList mainActivity, final String device_id) {
        DatabaseReference    mDatabase = FirebaseDatabase.getInstance().getReference(Constant.NODE_USER);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final User user = dataSnapshot.getValue(User.class);
                if(!user.getUser_id().equals(device_id)){
                    user.setLast_message("-");

                    final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(Constant.NODE_MESSAGE).child(device_id+"_"+user.getUser_id());
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0) {


                                final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference(Constant.NODE_MESSAGE).child(device_id+"_"+user.getUser_id());

                                Query lastQuery = databaseReference2.orderByKey().limitToLast(1);
                                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot1) {

                                        if(dataSnapshot1.exists()){
                                            Message account = null;
                                            try{

                                                account = dataSnapshot1.getChildren().iterator().next()
                                                        .getValue(Message.class);

                                            } catch (Throwable e) {

                                            }

                                            user.setLast_message(account.getMessage());
                                            mainActivity.onRetriverUser(user,account.getMessage());

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                            }else{
                                user.setLast_message("-");
                                mainActivity.onRetriverUser(user, "-");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                } else {
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //User user = dataSnapshot.getValue(User.class);
                // mainActivity.onChildChanged(user);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mainActivity.onChildRemoved(user);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

















    public static void sendMessage(String senderId, String receiverId, String message) {

        Message messageObject=new Message();
        messageObject.setFrom_user(senderId);
        messageObject.setTo_user(receiverId);
        messageObject.setTimestamp(System.currentTimeMillis());
        messageObject.setMessage(message);
        messageObject.setType("1");

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constant.NODE_MESSAGE);
        mDatabase.child(senderId + "_" + receiverId).push().setValue(messageObject);
        mDatabase.child(receiverId + "_" + senderId).push().setValue(messageObject);


    }

    public static void retrieveMessage(final onRetrieveMessage sendMessageActivity, String senderId, String receiverId) {



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constant.NODE_MESSAGE).child(senderId+"_"+receiverId);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                sendMessageActivity.pnRetriverUserAdd(message);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void authoriseUser(Activity applicationContext, final String name, final String userId, final String imagePath, final FirebaseLoginRegister onRegisterLoginCallbacks) {

        final String email = userId + "@chatapp.com";
        final String password = email;

        FirebaseAuth  mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(applicationContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(imagePath.isEmpty()){
                                FirebaseChatApp.registerUser(name,userId,imagePath);
                                onRegisterLoginCallbacks.firebaseRegister(name,true);
                            }else{
                                FirebaseChatApp.uploadImage(imagePath,name,userId,onRegisterLoginCallbacks);
                            }

                        } else {
                            onRegisterLoginCallbacks.firebaseRegister(name,false);
                        }
                    }

                });

    }

    private static void uploadImage(final String imagePath, final String name, final String userId, final FirebaseLoginRegister onRegisterLoginCallbacks) {

        FirebaseStorage   storage = FirebaseStorage.getInstance();
        StorageReference  storageReference = storage.getReference();


        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(Uri.fromFile(new File(imagePath)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                          @Override
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              Uri downloadUri = taskSnapshot.getDownloadUrl();
                                              FirebaseChatApp.registerUser(name,userId,String.valueOf(downloadUri));
                                              onRegisterLoginCallbacks.firebaseRegister(name,true);
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

    }

    public static void signInUser(Activity splash, final String name, final String userId, final FirebaseLoginRegister onRegisterLoginCallbacks) {
        final String email = userId + "@chatapp.com";
        final String password = email;

        FirebaseAuth  mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(splash, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onRegisterLoginCallbacks.firebaseLogin(true);
                        } else {
                            onRegisterLoginCallbacks.firebaseLogin(false);
                        }
                    }
                });
    }
}
