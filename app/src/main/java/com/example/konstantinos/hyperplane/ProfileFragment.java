package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 10/29/2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import org.w3c.dom.Text;

import java.net.URL;


public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ProfileFragment()
    {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        sectionNumber = 12;
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);


        TextView textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewName.setText(LoginSignupActivity.Name);


        ProfilePictureView profilePictureView = (ProfilePictureView) rootView.findViewById(R.id.ProfilePicture);
        profilePictureView.setProfileId(LoginSignupActivity.UserID);

        EditText editTextNewTopic = (EditText) rootView.findViewById(R.id.editTextNewTopic);
        editTextNewTopic.requestFocus();


        return rootView;
    }


}
