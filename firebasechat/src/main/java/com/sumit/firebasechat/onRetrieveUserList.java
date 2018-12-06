package com.sumit.firebasechat;

public interface onRetrieveUserList {
    public void onRetriverUser(User user, String message);
    public void onChildChanged(User user);
    public void onChildRemoved(User user);
}
