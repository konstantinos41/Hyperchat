package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 10/29/2016.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelCreateActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ContactSelectionActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelMetadata;
import com.facebook.login.widget.ProfilePictureView;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
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
        //editTextNewTopic.requestFocus();

        final Button button = (Button) rootView.findViewById(R.id.buttonNewTopic);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
//                startActivity(new Intent(getActivity(), ConversationActivity.class));

//                Intent intent = new Intent(getActivity(), ConversationActivity.class);
//                intent.putExtra(ConversationUIService.GROUP_ID, 959373);      //Pass group id here.
//                startActivity(intent);




            }
        });


        return rootView;
    }


}
