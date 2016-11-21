package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 10/29/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
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

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelCreateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ChannelCreateActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ContactSelectionActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelMetadata;
import com.applozic.mobicommons.people.contact.Contact;
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

        final EditText editTextNewTopic = (EditText) rootView.findViewById(R.id.editTextNewTopic);
        //editTextNewTopic.requestFocus();

        final Button button = (Button) rootView.findViewById(R.id.buttonNewTopic);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

                String channelName =  editTextNewTopic.getText().toString();
                int groupType = 1;
                AppContactService appContactService = new AppContactService(getActivity());
                // For larger image resolution replace width with height
                String imageUrl = "https://graph.facebook.com/" + LoginSignupActivity.UserID + "/picture?type=large&width=720";

                final ProgressDialog progressDialog  = ProgressDialog.show(getActivity(), "",
                        getActivity().getString(TextUtils.isEmpty(channelName)? com.applozic.mobicomkit.uiwidgets.R.string.broadcast_creating_info: com.applozic.mobicomkit.uiwidgets.R.string.group_creating_info), true);
                ApplozicChannelCreateTask.ChannelCreateListener channelCreateListener = new ApplozicChannelCreateTask.ChannelCreateListener() {
                    @Override
                    public void onSuccess(Channel channel, Context context) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (channel != null) {
                            Intent intent = new Intent(getActivity(), ConversationActivity.class);
                            if (ApplozicClient.getInstance(getActivity().getApplicationContext()).isContextBasedChat()) {
                                intent.putExtra(ConversationUIService.CONTEXT_BASED_CHAT, true);
                            }
                            intent.putExtra(ConversationUIService.GROUP_ID, channel.getKey());
                            intent.putExtra(ConversationUIService.GROUP_NAME, channel.getName());
                            getActivity().startActivity(intent);
                        }

//                        if (bundle != null && bundle.getString(CHANNEL) != null) {
//                            getActivity().sendBroadcast(new Intent(ChannelCreateActivity.ACTION_FINISH_CHANNEL_CREATE));
//                        }
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onFailure(Exception e, Context context) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                    }
                };

                if (FriendsFragment.ids != null && FriendsFragment.ids.size() > 0) {
                    if(TextUtils.isEmpty(channelName)){
                        StringBuffer stringBuffer = new StringBuffer();
                        int i = 0;
                        for (String userId : FriendsFragment.ids) {
                            i++;
                            if (i > 10)
                                break;
                            Contact contactDisplayName = appContactService.getContactById(userId);
                            stringBuffer.append(contactDisplayName.getDisplayName()).append(",");
                        }
                        int lastIndex = stringBuffer.lastIndexOf(",");
                        channelName = stringBuffer.replace(lastIndex, lastIndex + 1, "").toString();
                    }
                    ApplozicChannelCreateTask applozicChannelCreateTask = new ApplozicChannelCreateTask(getActivity(), channelCreateListener, channelName, FriendsFragment.ids, imageUrl);
                    applozicChannelCreateTask.setType(groupType);
                    applozicChannelCreateTask.execute((Void) null);
                }



            }
        });


        return rootView;
    }


}
