package io.gloop.messenger.model;

import java.util.HashMap;
import java.util.Map;

import io.gloop.GloopObject;

/**
 * Created by Alex Untertrifaller on 06.10.17.
 */

public class Task extends GloopObject {

    private boolean done;
    private String title;
    private String content;
    private boolean privateTask = false;
    private boolean freezeTask = false;
    private int color;
    private Map<String, String> members = new HashMap<>();

    public Task() {
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isPrivateTask() {
        return privateTask;
    }

    public void setPrivateTask(boolean privateTask) {
        this.privateTask = privateTask;
    }

    public boolean isFreezeTask() {
        return freezeTask;
    }

    public void setFreezeTask(boolean freezeTask) {
        this.freezeTask = freezeTask;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }

    public void addMember(String userId, String userImageUri) {
        members.put(userId, userImageUri);
    }

    public Map<String, String> getMembers() {
        return members;
    }
}
