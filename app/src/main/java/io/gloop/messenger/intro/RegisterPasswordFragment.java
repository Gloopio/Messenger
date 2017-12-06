package io.gloop.messenger.intro;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.gloop.Gloop;
import io.gloop.messenger.MainActivity;
import io.gloop.messenger.R;
import io.gloop.messenger.model.UserInfo;
import io.gloop.messenger.utils.SharedPreferencesStore;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class RegisterPasswordFragment extends Fragment  {

    private UserInfo userInfo;

    public RegisterPasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        userInfo = (UserInfo) args.getSerializable("userInfo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_register_password, container, false);

        final EditText password = (EditText) layoutContainer.findViewById(R.id.intro_register_password);

        Button next = (Button) layoutContainer.findViewById(R.id.intro_register_finish);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Gloop.register(userInfo.getPhone(), password.getText().toString())) {

                    SharedPreferencesStore.setUser(userInfo.getPhone(), password.getText().toString());

                    userInfo.save();


                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    getActivity().finish();
                }
            }
        });


        return layoutContainer;
    }
}