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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

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

    private static final String ARG_SECTION_NUMBER = "section_number";

    public FriendsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendsFragment newInstance(int sectionNumber)
    {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mainListView = (ListView) rootView.findViewById( R.id.friends_list );


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
                                    name.add(friendslist.getJSONObject(l).getString("name"));
                                    imageId.add(friendslist.getJSONObject(l).getString("id"));
                                }
                            } catch (Exception ex) {

                            }
                        }
                    }
            ).executeAsync();
        }


         CustomList adapter = new CustomList(getActivity(), name, imageId);
         // Set the ArrayAdapter as the ListView's adapter.
         mainListView.setAdapter(adapter);



        return rootView;
    }
}
