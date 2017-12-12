package io.gloop.messenger;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import br.com.instachat.emojilibrary.controller.WhatsAppPanel;
import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.WhatsAppPanelEventListener;
import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.ChatMessage;
import io.gloop.messenger.model.Status;
import io.gloop.messenger.model.UserInfo;
import io.gloop.messenger.utils.Store;
import io.gloop.utils.TimeUtil;


public class ChatActivity extends EmojiCompatActivity implements WhatsAppPanelEventListener {

    public static final String CHAT = "chat";

    private ListView chatListView;
    private GloopList<ChatMessage> chatMessages;
    private ChatListAdapter listAdapter;

    private ProgressDialog progress;

    private UserInfo userInfo;
    private Chat chat;

    private WhatsAppPanel mBottomPanel;


    @Override
    public void onSendClicked() {
        sendMessage(mBottomPanel.getText(), userInfo.getPhone());
        mBottomPanel.setText("");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.splashscreen_background));

        chat = (Chat) getIntent().getSerializableExtra(ChatActivity.CHAT);
        userInfo = Store.getOwnerUserInfo();

        chatListView = (ListView) findViewById(R.id.chat_list_view);

        mBottomPanel = new WhatsAppPanel(this, this, R.color.colorPrimary);

        new LoadTask().execute();

    }

    private class LoadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ChatActivity.this);
            progress.setTitle(getString(R.string.loading));
            progress.setMessage(getString(R.string.wait_while_loading_lines));
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... urls) {
            chatMessages = Gloop.all(ChatMessage.class).where().equalsTo("chatId", chat.getObjectId()).all().sort("timestamp");
            chatMessages.load();

            listAdapter = new ChatListAdapter(chatMessages, ChatActivity.this, userInfo);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatListView.setAdapter(listAdapter);
                }
            });
            return null;
        }


        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            progress.dismiss();
        }
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendMessage(final String messageText, final String author) {
        if (messageText.trim().length() == 0)
            return;

        final ChatMessage message = new ChatMessage();
        message.setUser(chat.getGloopUser());
        message.setChatId(chat.getObjectId());
        message.setMessageStatus(Status.SENT);
        message.setMessageText(messageText);
        message.setAuthor(author);
        message.setMessageTime(TimeUtil.currentTimestamp());
        chatMessages.add(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        chatMessages.removeOnChangeListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatMessages.removeOnChangeListeners();
    }


}
