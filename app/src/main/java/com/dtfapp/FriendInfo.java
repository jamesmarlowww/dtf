package com.dtfapp;

import android.graphics.Bitmap;

/**
 * Created by James on 6/30/2015.
 */
public class FriendInfo {
    private String friendName;
    private String id;
    private boolean youTick;
    private boolean youHeart;
    private boolean theyTick;
    private boolean theyHeart;

    public FriendInfo(String name, String id, boolean youLikeThem, boolean theyLikeYou, boolean theyTick, boolean theyHeart) {
        friendName = name;
        this.id = id;
        this.youTick = youLikeThem;
        this.youHeart = theyLikeYou;
        this.theyTick = theyTick;
        this.theyHeart = theyHeart;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getId() {
        return id;
    }

    public boolean isYouTick() {
        return youTick;
    }

    public boolean isYouHeart() {
        return youHeart;
    }

    public boolean isTheyTick() {
        return theyTick;
    }

    public boolean isTheyHeart() {
        return theyHeart;
    }
}
