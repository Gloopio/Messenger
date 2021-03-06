package io.gloop.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.gloop.Gloop;
import io.gloop.messenger.utils.SharedPreferencesStore;

public class SplashActivity extends Activity {

    public static final String SHARED_PREFERENCES_FIRST_START = "firstStart";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splashscreen);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        SharedPreferencesStore.setContext(getBaseContext());

        if (SharedPreferencesStore.isFirstStart())
            showIntroOnFirstRun();
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // setup Gloop
                    // set apiKey and host in the AndroidManifest.xml file.
                    // It is also possible to pass them as parameters to the initialize method.
                    Gloop.initialize(SplashActivity.this);

                    if (!logInWithRememberedUser()) {
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                    }
                }
            }).start();
        }
    }

    private boolean logInWithRememberedUser() {
        try {
            String email = SharedPreferencesStore.getEmail();
            String password = SharedPreferencesStore.getPassword();

            if (!email.isEmpty() && !password.isEmpty()) {
                if (Gloop.login(email, password)) {

                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    return true;
                }

            }
            return false;
        } catch (Exception e) {
            logInWithRememberedUser();
            return false;
        }
    }

    private void showIntroOnFirstRun() {
        //  Declare a new thread to do a preference check
        new Thread(new Runnable() {
            @Override
            public void run() {
                //  If the activity has never started before...
                if (SharedPreferencesStore.isFirstStart()) {

                    //  Launch app intro
                    Intent i = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(i);

                    SharedPreferencesStore.setFirstRun(false);
                }
            }
        }).start();
    }
}