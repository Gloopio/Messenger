package io.gloop.messenger.model;

import io.gloop.GloopObject;
import io.gloop.annotations.Serializer;
import io.gloop.messenger.serializers.StatusSerializer;

public class ChatMessage extends GloopObject {

    private String chatId;
    private String messageText;
    private String author;
    @Serializer(StatusSerializer.class)
    private Status messageStatus;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    private long messageTime;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "chatId='" + chatId + '\'' +
                ", messageText='" + messageText + '\'' +
                ", author='" + author + '\'' +
                ", messageStatus=" + messageStatus +
                ", messageTime=" + messageTime +
                '}';
    }
}
