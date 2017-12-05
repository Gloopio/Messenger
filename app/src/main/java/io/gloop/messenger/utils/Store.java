package io.gloop.messenger.utils;

import io.gloop.messenger.model.UserInfo;

/**
 * Created by Alex Untertrifaller on 05.12.17.
 */

public class Store {

    private static UserInfo ownerUserInfo;

    public static UserInfo getOwnerUserInfo() {
        return ownerUserInfo;
    }

    public static void setOwnerUserInfo(UserInfo owner) {
        ownerUserInfo = owner;
    }
}
