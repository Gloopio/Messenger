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
import android.widget.Toast;

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
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private long timestamp;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

//            Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
//            }
//        }, 1000);


//            AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            Intent i = new Intent(getApplicationContext(), BackgroundService.class);
//            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
//            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); // Millisec * Second * Minute


//            Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_LONG).show();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("Messenger", "Check");
//                    Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
//                }
//            }, 1000);


            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Restore interrupt status.
//                Thread.currentThread().interrupt();
//            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
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

        if (Gloop.login(SharedPreferencesStore.getEmail(), SharedPreferencesStore.getPassword())) {

            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);
            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        checkForNewMassages();
                        try {
//                        Thread.sleep(300000);   // 5 min
//                            Thread.sleep(60000);   // 1 min
                            Thread.sleep(30000);   // 1 min
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            Log.e("Messenger", "Login failed");
            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkForNewMassages() {
        long timestamp = SharedPreferencesStore.getLastTimeStamp();
        GloopList<ChatMessage> messages = Gloop.all(ChatMessage.class)
                .where()
                .greaterThan("messageTime", timestamp)
                .and()
                .notEqualsTo("author", Gloop.getOwner().getUserId())
                .all();

        for (ChatMessage message : messages) {
            showNotification(message);
        }


        Log.d("Messenger", messages.toString());

        SharedPreferencesStore.setCurrentTimestamp();
//        }
    }

    private void showNotification(ChatMessage message) {

        Chat chat = Gloop.all(Chat.class).where().equalsTo("objectId", message.getChatId()).first();

        if (chat != null ) {

            UserInfo ownerUserInfo = Gloop.all(UserInfo.class)
                    .where()
                    .equalsTo("phone", Gloop.getOwner().getName())
                    .first();

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.CHAT, chat);
            intent.putExtra(ChatActivity.USER_INFO, ownerUserInfo);
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

//        UserInfo user = Gloop.all(UserInfo.class).where().equalsTo("phone", message.getAuthor()).first();

            UserInfo user;

            if (chat.getUser1().getPhone().equals(ownerUserInfo.getPhone())) {
                user = chat.getUser2();
            } else {
                user = chat.getUser1();
            }

// build notification
// the addAction re-use the same intent to keep the example short
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
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}