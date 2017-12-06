package io.gloop.messenger.intro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.gloop.messenger.R;
import io.gloop.messenger.model.UserInfo;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class RegisterUsernameFragment extends Fragment {

    private ConstraintLayout layoutContainer;
    private UserInfo userInfo;

    public RegisterUsernameFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        userInfo = (UserInfo) args.getSerializable("userInfo");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_register_name, container, false);

        final EditText username = (EditText) layoutContainer.findViewById(R.id.intro_register_username);

        Button next = (Button) layoutContainer.findViewById(R.id.intro_register_username_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = username.getText().toString();
                if (!input.equals("")) {
                    userInfo.setUserName(input);

                    Fragment newFragment = new RegisterPictureFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userInfo", userInfo);
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Snackbar.make(view, "You have to enter a name!", Snackbar.LENGTH_SHORT).show();

                }
            }
        });


        return layoutContainer;
    }

}