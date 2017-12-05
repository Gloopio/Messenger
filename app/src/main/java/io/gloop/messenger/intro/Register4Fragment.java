package io.gloop.messenger.intro;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import io.gloop.messenger.R;
import io.gloop.messenger.model.UserInfo;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Alex Untertrifaller on 30.11.17.
 */

public class Register4Fragment extends Fragment  {

    private ImageView imageView;

    private UserInfo userInfo;

    public Register4Fragment() {
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
        ConstraintLayout layoutContainer = (ConstraintLayout) inflater.inflate(R.layout.intro_choose_picture, container, false);

        imageView = (ImageView) layoutContainer.findViewById(R.id.intro_image);

        Button capture = (Button) layoutContainer.findViewById(R.id.intro_pic_capture_image);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);//zero can be replaced with any action code
            }
        });
        Button pic = (Button) layoutContainer.findViewById(R.id.intro_pic_image);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });
        Button next = (Button) layoutContainer.findViewById(R.id.intro_pic_image_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // tODOd save image to userInfo

                Fragment newFragment = new Register3Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userInfo);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return layoutContainer;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                }
                break;
        }
    }
}