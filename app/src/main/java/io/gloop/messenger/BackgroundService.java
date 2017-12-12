package io.gloop.messenger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.messenger.model.Chat;
import io.gloop.messenger.model.ChatMessage;
import io.gloop.messenger.model.UserInfo;
import io.gloop.messenger.utils.SharedPreferencesStore;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Alex Untertrifaller on 07.12.17.
 */

public class BackgroundService extends Service {

    long INITIAL_BACKOFF = 30;
    long backoff = INITIAL_BACKOFF;
    long MAX_BACKOFF = INITIAL_BACKOFF * 6;

    private long backoff() {
        backoff = backoff + INITIAL_BACKOFF;
        if (backoff > MAX_BACKOFF)
            backoff = MAX_BACKOFF;
        return backoff;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();


        StrictMode.ThreadPolicy policy = (new StrictMode.ThreadPolicy.Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Gloop.initialize(getApplication());
        SharedPreferencesStore.setContext(this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int tries = 5;
        while (tries >= 0) {
            try {
                if (Gloop.login(SharedPreferencesStore.getEmail(), SharedPreferencesStore.getPassword())) {

                    Looper mServiceLooper = thread.getLooper();
                    ServiceHandler mServiceHandler = new ServiceHandler(mServiceLooper);
                    mServiceHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    long sleepTime = checkForNewMassages();
                                    Thread.sleep(sleepTime * 1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    tries = -1;
                } else {
                    tries--;
                    Log.e("Messenger", "Login failed");
                }
            } catch (Exception e) {
                Log.i("MessengerBackground", "Login failed, try number: " + tries);
                tries--;
            }
        }
    }

    private long checkForNewMassages() {
        long timestamp = SharedPreferencesStore.getLastTimeStamp();
        GloopList<ChatMessage> messages = Gloop.all(ChatMessage.class)
                .where()
                .greaterThan("messageTime", timestamp)
                .and()
                .notEqualsTo("author", Gloop.getOwner().getUserId())
                .all();

        long sleepTime = backoff();

        for (ChatMessage message : messages) {
            showNotification(message);
            sleepTime = INITIAL_BACKOFF;
        }


        Log.d("Messenger", messages.toString());
        Log.d("Messenger", "Sleep time: " + sleepTime * 1000 + " ms");

        SharedPreferencesStore.setCurrentTimestamp();

        return sleepTime;
    }

    private void showNotification(ChatMessage message) {

        Chat chat = Gloop.all(Chat.class).where().equalsTo("objectId", message.getChatId()).first();

        if (chat != null) {

            UserInfo ownerUserInfo = Gloop.all(UserInfo.class)
                    .where()
                    .equalsTo("phone", Gloop.getOwner().getName())
                    .first();

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.CHAT, chat);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

            UserInfo user;

            if (chat.getUser1().getPhone().equals(ownerUserInfo.getPhone())) {
                user = chat.getUser2();
            } else {
                user = chat.getUser1();
            }

            Notification n = new Notification.Builder(this)
                    .setContentTitle("New message from " + user.getUserName())
                    .setContentText(message.getMessageText())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();
//                .addAction(R.mipmap, "Call", pIntent)
//                .addAction(R.drawable.icon, "More", pIntent)
//                .addAction(R.drawable.icon, "And more", pIntent).build();


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
    }
}