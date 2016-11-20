package com.applozic.mobicomkit.uiwidgets.conversation.fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.uiwidgets.ApplozicApplication;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.MultimediaOptionsGridView;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.MobicomMultimediaPopupAdapter;
import com.applozic.mobicommons.commons.core.utils.LocationUtils;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

public class ConversationFragment extends MobiComConversationFragment implements SearchListFragment {

    private static final String TAG = "ConversationFragment";
    private MultimediaOptionsGridView popupGrid;
    InputMethodManager inputMethodManager;

    public ConversationFragment() {
        this.messageIntentClass = MessageIntentService.class;
    }

    public ConversationFragment(Contact contact, Channel channel,Integer conversationId) {
        this.messageIntentClass = MessageIntentService.class;
        this.contact = contact;
        this.channel = channel;
        this.currentConversationId = conversationId;
    }

    public ConversationFragment(Contact contact, Channel channel,Integer conversationId,String searchString) {
        this.messageIntentClass = MessageIntentService.class;
        this.contact = contact;
        this.channel = channel;
        this.currentConversationId = conversationId;
        this.searchString=searchString;

        if (searchString != null) {
            SyncCallService.refreshView=true;
        }
    }

    public void attachLocation(Location mCurrentLocation) {
        String address = LocationUtils.getAddress(getActivity(), mCurrentLocation);
        if (!TextUtils.isEmpty(address)) {
            address = "Address: " + address + "\n";
        } else {
            address = "";
        }
        this.messageEditText.setText(address + "http://maps.google.com/?q=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.title = ApplozicApplication.TITLE;
        this.conversationService = new MobiComConversationService(getActivity());
        hideExtendedSendingOptionLayout = true;

        View view = super.onCreateView(inflater, container, savedInstanceState);

        sendType.setSelection(1);

        messageEditText.setHint(R.string.enter_mt_message_hint);

        multimediaPopupGrid.setVisibility(View.GONE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.secret_message_timer_array, R.layout.mobiframework_custom_spinner);

        adapter.setDropDownViewResource(R.layout.mobiframework_custom_spinner);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        messageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multimediaPopupGrid.setVisibility(View.GONE);
            }
        });


        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MobicomMultimediaPopupAdapter mobicomMultimediaPopupAdapter = new MobicomMultimediaPopupAdapter(getActivity(), getResources().getStringArray(R.array.multimediaOptionIcons_without_price), getResources().getStringArray(R.array.multimediaOptions_without_price_text));
                mobicomMultimediaPopupAdapter.setAlCustomizationSettings(alCustomizationSettings);
                multimediaPopupGrid.setAdapter(mobicomMultimediaPopupAdapter);
                multimediaPopupGrid.setVisibility(View.VISIBLE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                MultimediaOptionsGridView itemClickHandler = new MultimediaOptionsGridView(getActivity(), multimediaPopupGrid);
                itemClickHandler.setMultimediaClickListener();

                if (contact != null && !contact.isBlocked() || channel != null) {
                    if (attachmentLayout.getVisibility() == View.VISIBLE) {
                        Toast.makeText(getActivity(), R.string.select_file_count_limit, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (contact != null && contact.isBlocked()) {
                    userBlockDialog(false);
                }
            }
        });

        return view;
    }

    @Override
    protected void processMobiTexterUserCheck() {

    }

    public void updateTitle() {
        //((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(ApplozicApplication.TITLE);
        super.updateTitle();
    }

    public void hideMultimediaOptionGrid() {
        if (multimediaPopupGrid.getVisibility() == View.VISIBLE) {
            multimediaPopupGrid.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            conversationAdapter.getFilter().filter(null);
        } else {
            conversationAdapter.getFilter().filter(newText);
        }
        return true;
    }
}