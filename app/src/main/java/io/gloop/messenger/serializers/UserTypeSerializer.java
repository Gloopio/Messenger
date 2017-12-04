package io.gloop.messenger.serializers;

import java.util.Map;

import io.gloop.messenger.model.UserType;
import io.gloop.serializers.GloopSerializeToInt;

/**
 * Created by Alex Untertrifaller on 04.12.17.
 */

public class UserTypeSerializer extends GloopSerializeToInt<UserType> {
    @Override
    public int serialize(UserType status, Map<String, Object> map) {
        if (status == UserType.OTHER)
            return 0;
        if (status == UserType.SELF)
            return 1;
        return 0;
    }

    @Override
    public UserType deserialize(int i, Map<String, Object> map) {
        if (i == 0)
            return UserType.OTHER;
        if (i == 1)
            return UserType.SELF;
        return null;
    }
}
