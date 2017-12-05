package io.gloop.messenger;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import io.gloop.messenger.intro.RequestPermissionsFragment;
import io.gloop.messenger.intro.WelcomeFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = (new StrictMode.ThreadPolicy.Builder()).permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // check for runtime permissions
        RequestPermissionsFragment newFragment = new RequestPermissionsFragment();
        if (!newFragment.hasPermissions(this)) {
            transaction.replace(R.id.fragment_container, newFragment);
        } else {
            Fragment frag = new WelcomeFragment();
            transaction.replace(R.id.fragment_container, frag);
        }

        transaction.addToBackStack(null);
        transaction.commit();
    }

}

