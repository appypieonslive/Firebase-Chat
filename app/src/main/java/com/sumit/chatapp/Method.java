package com.sumit.chatapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class Method {


    public static  String getPath(Context fantasyFilter, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = fantasyFilter.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =  cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

}
