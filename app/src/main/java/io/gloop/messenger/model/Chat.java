package io.gloop.messenger.model;

import io.gloop.GloopObject;

/**
 * Created by Alex Untertrifaller on 29.11.17.
 */

public class Chat extends GloopObject {

    private UserInfo user1;
    private UserInfo user2;

    public UserInfo getUser1() {
        return user1;
    }

    public void setUser1(UserInfo user1) {
        this.user1 = user1;
    }

    public UserInfo getUser2() {
        return user2;
    }

    public void setUser2(UserInfo user2) {
        this.user2 = user2;
    }
}
