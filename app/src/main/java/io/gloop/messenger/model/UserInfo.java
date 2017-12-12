package io.gloop.messenger.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

import io.gloop.GloopObject;
import io.gloop.annotations.Serializer;
import io.gloop.serializers.custom.BitmapSerializer;

/**
 * Created by Alex Untertrifaller on 20.09.17.
 */

public class UserInfo extends GloopObject {

    private String phone;
    @Serializer(BitmapSerializer.class)
    private Bitmap picture;
    private String userName;
    private long lastTimeOnline;
    private List<String> favories = new ArrayList<>();

    public UserInfo() {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public long getLastTimeOnline() {
        return lastTimeOnline;
    }

    public void setLastTimeOnline(long lastTimeOnline) {
        this.lastTimeOnline = lastTimeOnline;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public List<String> getFavories() {
        return favories;
    }

    public void setFavories(List<String> favories) {
        this.favories = favories;
    }
}
