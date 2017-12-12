package io.gloop.messenger.intro;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import io.gloop.Gloop;
import io.gloop.messenger.R;
import io.gloop.messenger.model.UserInfo;

import static io.gloop.permissions.GloopPermission.PUBLIC;
import static io.gloop.permissions.GloopPermission.READ;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class RegisterPhoneFragment extends Fragment implements ISlideBackgroundColorHolder {

    private ConstraintLayout layoutContainer;

    public RegisterPhoneFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_register_phone_number, container, false);

        final UserInfo userinfo = new UserInfo();
        userinfo.setUser(Gloop.getOwner().getUserId(), PUBLIC | READ);

        final IntlPhoneInput phoneInputView = (IntlPhoneInput) layoutContainer.findViewById(R.id.my_phone_input);


        Button next = (Button) layoutContainer.findViewById(R.id.intro_register_phone_number);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myInternationalNumber;
                if (phoneInputView.isValid()) {
                    myInternationalNumber = phoneInputView.getNumber();
                    userinfo.setPhone(myInternationalNumber);


                    Fragment newFragment = new RegisterUsernameFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userinfo);
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Snackbar.make(view, "Not a valid phone number", Snackbar.LENGTH_SHORT).show();
                }
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
