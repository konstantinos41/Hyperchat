package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 10/29/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsFragment extends Fragment {

    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    private List<String> name = new ArrayList<String>();
    private List<String> imageId = new ArrayList<String>();

    private View rootView;
    private CustomList adapter;


    public FriendsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendsFragment newInstance()
    {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mainListView = (ListView) rootView.findViewById( R.id.friends_list );
        mainListView.setClickable(true);

        // Check if friends list is empty and if it is fill it with friends
        // There seems to be a problem when a lot of friends are on the list, it takes some time to load
        // it might be because in simplerow.xml ProfilePicture control is used and not the simple image one.
        if(name.isEmpty()) {

            new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/friends", null, HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {

                                JSONArray friendslist = response.getJSONObject().getJSONArray("data");

                                for (int l = 0; l < friendslist.length(); l++) {
                                    name.add(0, friendslist.getJSONObject(l).getString("name"));
                                    imageId.add(0, friendslist.getJSONObject(l).getString("id"));
                                }

                                name.add("Invite more Friends!");
                                imageId.add("");
                                adapter = new CustomList(getActivity(), name, imageId);
                                mainListView.setAdapter(adapter);
                            } catch (Exception ex) {

                            }
                        }
                    }

            ).executeAsync();
        }


        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Object obj = mainListView.getItemAtPosition(position);

                //check if the invite button is clicked
                if (obj.equals("Invite more Friends!")){
                    inviteFriends();
                }

                /* do something more */
            }
        });

        return rootView;
    }


    // implements the "invite friends" function
    public void inviteFriends(){
        String appLinkUrl, previewImageUrl;

        appLinkUrl = "https://www.google.com";
        previewImageUrl = "https://lh5.ggpht.com/DZG1niZb55ylVexAwfs19GHsY7kjt_onLmhtuAdQaZmPDpxHkYrpXI8iV1eGAlgm9N0=w300-rw";

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        }
    }


}
