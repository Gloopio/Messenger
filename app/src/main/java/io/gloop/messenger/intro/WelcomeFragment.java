package io.gloop.messenger.intro;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import io.gloop.messenger.R;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class WelcomeFragment extends Fragment implements ISlideBackgroundColorHolder {

    private ConstraintLayout layoutContainer;

    public WelcomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_welcome, container, false);

        Button login = (Button) layoutContainer.findViewById(R.id.intro_welcome_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new LoginFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button register = (Button) layoutContainer.findViewById(R.id.intro_welcome_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new RegisterPhoneFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return layoutContainer;
    }

    @Override
    public int getDefaultBackgroundColor() {
        // Return the default background color of the slide.
        return Color.parseColor("#000000");
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        // Set the background color of the view within your slide to which the transition should be applied.
        if (layoutContainer != null) {
            layoutContainer.setBackgroundColor(backgroundColor);
        }
    }
}
