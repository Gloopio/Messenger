package io.gloop.messenger.intro;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import io.gloop.Gloop;
import io.gloop.messenger.MainActivity;
import io.gloop.messenger.R;
import io.gloop.messenger.utils.SharedPreferencesStore;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class LoginFragment extends Fragment implements ISlideBackgroundColorHolder {

    private ConstraintLayout layoutContainer;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_login, container, false);

        final IntlPhoneInput phoneInputView = (IntlPhoneInput) layoutContainer.findViewById(R.id.intro_login_phone);

        final EditText password = (EditText) layoutContainer.findViewById(R.id.intro_login_password);


        Button next = (Button) layoutContainer.findViewById(R.id.intro_login_continue);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myInternationalNumber;
                if (phoneInputView.isValid()) {
                    myInternationalNumber = phoneInputView.getNumber();

                    if (Gloop.login(myInternationalNumber, password.getText().toString()) ) {

                        SharedPreferencesStore.setUser(myInternationalNumber, password.getText().toString());

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);

                        getActivity().finish();

                    } else {
                        Snackbar.make(view, "Login credentials are not correct", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(view, "Not a valid phone number", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        return layoutContainer;
    }

//    private String getPhoneNumber() {
//
//
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        } else {
//            TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            if (tMgr != null) {
//                return tMgr.getLine1Number();
//            } else {
//                return null;
//            }
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSION_READ_STATE) {
//            TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            phoneNumber.setText(tMgr.getLine1Number());
//        }
//    }

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
