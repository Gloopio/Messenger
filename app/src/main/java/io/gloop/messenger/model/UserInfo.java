package io.gloop.messenger.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import io.gloop.GloopObject;

/**
 * Created by Alex Untertrifaller on 20.09.17.
 */

public class UserInfo extends GloopObject {

    private String phone;
    private String imageURL;
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

    public Uri getImageURL() {
        if (imageURL != null)
            return Uri.parse(imageURL);
        else
            return null;
    }

    public void setImageURL(Uri imageURL) {
        if (imageURL != null)
            this.imageURL = imageURL.toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getFavories() {
        return favories;
    }

    public void setFavories(List<String> favories) {
        this.favories = favories;
    }
}
