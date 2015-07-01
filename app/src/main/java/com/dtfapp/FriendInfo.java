package com.dtfapp;

import android.graphics.Bitmap;

/**
 * Created by James on 6/30/2015.
 */
public class FriendInfo {
    private String friendName;
    private int id;
    private Bitmap image;
    private boolean youLikeThem;
    private boolean theyLikeYou;

    public FriendInfo(String name, int id, boolean youLikeThem, boolean theyLikeYou) {
        friendName = name;
        this.id = id;
        this.image = image;
        this.youLikeThem = youLikeThem;
        this.theyLikeYou = theyLikeYou;

    }

    public String getFriendName() {
        return friendName;
    }

    public int getId() {
        return id;
    }

    public String getIdStr() {
        return Integer.toString(id);
    }


    public Bitmap getBitmap() {
        return image;
    }
}
