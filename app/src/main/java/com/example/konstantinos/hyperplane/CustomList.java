package com.example.konstantinos.hyperplane;

/**
 * Created by Konstantinos on 11/15/2016.
 */


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final List<String> name;
    private final List<String> imageId;

    public CustomList(Activity context,
                      List<String> name, List<String> imageId) {
        super(context, R.layout.simplerow, name);
        this.context = context;
        this.name = name;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.simplerow, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        txtTitle.setText(name.get(position));



        ProfilePictureView imageView = (ProfilePictureView) rowView.findViewById(R.id.img);
        imageView.setProfileId(imageId.get(position));

        return rowView;
    }
}