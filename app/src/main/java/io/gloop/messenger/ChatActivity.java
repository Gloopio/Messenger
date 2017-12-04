package io.gloop.messenger;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.ListView;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import br.com.instachat.emojilibrary.controller.WhatsAppPanel;
import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.WhatsAppPanelEventListener;
import in.co.madhur.chatbubblesdemo.AndroidUtilities;
import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.ChatMessage;
import io.gloop.messenger.model.Status;
import io.gloop.messenger.model.UserType;


public class ChatActivity extends EmojiCompatActivity implements NotificationCenter.NotificationCenterDelegate, WhatsAppPanelEventListener {

    private ListView chatListView;
    private GloopList<ChatMessage> chatMessages;
    private ChatListAdapter listAdapter;

    private Chat chat;

    private WhatsAppPanel mBottomPanel;

    @Override
    public void onSendClicked() {
        sendMessage(mBottomPanel.getText(), UserType.OTHER);
        mBottomPanel.setText("");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidUtilities.statusBarHeight = getStatusBarHeight();

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.splashscreen_background));

        chat = (Chat) getIntent().getSerializableExtra("chat");

        chatMessages = Gloop.all(ChatMessage.class).where().equalsTo("chatId", chat.getObjectId()).all().sort("timestamp");

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        listAdapter = new ChatListAdapter(chatMessages, this);

        chatListView.setAdapter(listAdapter);

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

        mBottomPanel = new WhatsAppPanel(this, this, R.color.colorPrimary);
    }

    private void sendMessage(final String messageText, final UserType userType) {
        if (messageText.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage();
        message.setChatId(chat.getObjectId());
        message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setUserType(userType);
        message.setMessageTime(new Date().getTime());
        chatMessages.add(message);

        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();

        // Mark message as delivered after one second

        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

        exec.schedule(new Runnable() {
            @Override
            public void run() {
                message.setMessageStatus(Status.DELIVERED);

                final ChatMessage message = new ChatMessage();
                message.setChatId(chat.getObjectId());
                message.setMessageStatus(Status.SENT);
                message.setMessageText(messageText);
                message.setUserType(UserType.SELF);
                message.setMessageTime(new Date().getTime());
                chatMessages.add(message);

                ChatActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        listAdapter.notifyDataSetChanged();
                    }
                });


            }
        }, 1, TimeUnit.SECONDS);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
//            if (emojiView != null) {
//                emojiView.invalidateViews();
//            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

}
