package io.gloop.messenger;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;

public class App extends Application {

    private static App Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Instance=this;

        applicationHandler = new Handler(getInstance().getMainLooper());

        Intent startServiceIntent = new Intent(this, BackgroundService.class);
        startService(startServiceIntent);

    }

    public static App getInstance()
    {
        return Instance;
    }
}
