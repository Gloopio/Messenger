package io.gloop.messenger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by Alex Untertrifaller on 07.12.17.
 */

public class BackgroundService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
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
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();


        AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), BackgroundService.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); // Millisec * Second * Minute
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("Messenger", "Check");
//                Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
//            }
//        }, 1000);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("Messenger", "Check");
//                Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
//            }
//        }, 1000);


//        AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(getApplicationContext(), BackgroundService.class);
//        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, pi); // Millisec * Second * Minute

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                Toast.makeText(getApplicationContext(), "check", Toast.LENGTH_SHORT).show();
//            }
//        }, 1000);

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

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