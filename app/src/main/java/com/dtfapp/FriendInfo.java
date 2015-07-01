package com.dtfapp;

/**
 * Created by James on 6/30/2015.
 */
public class FriendInfo {
    private String friendName;
    private int id;
    private boolean youLikeThem;
    private boolean theyLikeYou;

    public FriendInfo(String name, int pic, boolean youLikeThem, boolean theyLikeYou) {
        friendName = name;
        id = pic;
        this.youLikeThem = youLikeThem;
        this.theyLikeYou = theyLikeYou;

    }

    public String getFriendName() {
        return friendName;
    }

    public int getId() {
        return id;
    }


}
