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

import io.gloop.messenger.R;
import io.gloop.messenger.model.UserInfo;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class Register1Fragment extends Fragment implements ISlideBackgroundColorHolder {

    private ConstraintLayout layoutContainer;

    public Register1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_register_phone_number, container, false);

//        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
//
//        phoneNumber = (EditText) layoutContainer.findViewById(R.id.intro_phone_number);
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            phoneNumber.setText(tMgr.getLine1Number());
//        }

        final UserInfo userinfo = new UserInfo();

        final IntlPhoneInput phoneInputView = (IntlPhoneInput) layoutContainer.findViewById(R.id.my_phone_input);


        Button next = (Button) layoutContainer.findViewById(R.id.intro_register_phone_number);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myInternationalNumber;
                if (phoneInputView.isValid()) {
                    myInternationalNumber = phoneInputView.getNumber();
                    userinfo.setPhone(myInternationalNumber);


                    Fragment newFragment = new Register2Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userinfo);
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
