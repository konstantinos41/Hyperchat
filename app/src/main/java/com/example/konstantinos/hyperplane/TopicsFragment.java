package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 10/29/2016.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.android.gms.location.places.Place;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A placeholder fragment containingχ    a simple view.
 */
public class TopicsFragment extends Fragment implements MessageCommunicator, MobiComKitActivityInterface
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private static int retry;
    public LinearLayout layout;
    public Snackbar snackbar;
    MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    ConversationUIService conversationUIService;
    MobiComQuickConversationFragment mobiComQuickConversationFragment;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
    public static TopicsFragment newInstance() {
        TopicsFragment fragment = new TopicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layout = (LinearLayout) layout.findViewById(R.id.footerAd);//this is snack bar layout needed

        conversationUIService = new ConversationUIService(this, mobiComQuickConversationFragment);
        mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this, mobiComQuickConversationFragment);
        new MobiComConversationService(this).processLastSeenAtStatus();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topics, container, false);

        return rootView;
    }

    @Override
    public void showErrorMessageView(String message) {
        layout.setVisibility(View.VISIBLE);
        snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        TextView textView = (TextView) group.findViewById(R.id.snackbar_action);
        textView.setTextColor(Color.YELLOW);
        group.setBackgroundColor(getResources().getColor(R.color.error_background_color));
        TextView txtView = (TextView) group.findViewById(R.id.snackbar_text);
        txtView.setMaxLines(5);
        snackbar.show();
    }


    @Override
    public void retry() {
        retry++;
    }


    @Override
    public int getRetryCount() {
        return retry;
    }


    public void dismissErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        final String deviceKeyString = MobiComUserPreference.getInstance(this).getDeviceKeyString();
        final String userKeyString = MobiComUserPreference.getInstance(this).getSuUserKeyString();
        Intent intent = new Intent(this, ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.USER_KEY_STRING, userKeyString);
        intent.putExtra(ApplozicMqttIntentService.DEVICE_KEY_STRING, deviceKeyString);
        startService(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
        Intent subscribeIntent = new Intent(this, ApplozicMqttIntentService.class);
        subscribeIntent.putExtra(ApplozicMqttIntentService.SUBSCRIBE, true);
        startService(subscribeIntent);


        if (!Utils.isInternetAvailable(getApplicationContext())) {
            String errorMessage = getResources().getString(R.string.internet_connection_not_available);
            showErrorMessageView(errorMessage);
        }
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel, Integer conversationId, String searchString) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(ConversationUIService.TAKE_ORDER, true);
        intent.putExtra(ConversationUIService.SEARCH_STRING, searchString);
        intent.putExtra(ConversationUIService.CONVERSATION_ID, conversationId);
        if (contact != null) {
            intent.putExtra(ConversationUIService.USER_ID, contact.getUserId());
            intent.putExtra(ConversationUIService.DISPLAY_NAME, contact.getDisplayName());
        } else if (channel != null) {
            intent.putExtra(ConversationUIService.GROUP_ID, channel.getKey());
            intent.putExtra(ConversationUIService.GROUP_NAME, channel.getName());
        }
        startActivity(intent);
    }


    @Override
    public void startContactActivityForResult() {
        conversationUIService.startContactActivityForResult();
    }


    @Override
    public void addFragment(ConversationFragment conversationFragment) {
    }


    @Override
    public void updateLatestMessage(final Message message, final String formattedContactNumber) {
        conversationUIService.updateLatestMessage(message, formattedContactNumber);
    }


    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        conversationUIService.removeConversation(message, formattedContactNumber);
    }

}