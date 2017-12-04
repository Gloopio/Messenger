package io.gloop.messenger.serializers;

import java.util.Map;

import io.gloop.messenger.model.Status;
import io.gloop.serializers.GloopSerializeToInt;

/**
 * Created by Alex Untertrifaller on 04.12.17.
 */

public class StatusSerializer extends GloopSerializeToInt<Status> {
    @Override
    public int serialize(Status status, Map<String, Object> map) {
        if (status == Status.SENT)
            return 0;
        if (status == Status.DELIVERED)
            return 1;
        return 0;
    }

    @Override
    public Status deserialize(int i, Map<String, Object> map) {
        if (i == 0)
            return Status.SENT;
        if (i == 1)
            return Status.DELIVERED;
        return null;
    }
}
