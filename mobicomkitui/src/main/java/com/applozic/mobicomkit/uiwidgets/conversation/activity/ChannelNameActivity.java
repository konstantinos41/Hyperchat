package com.applozic.mobicomkit.uiwidgets.conversation.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.applozic.mobicomkit.feed.GroupInfoUpdate;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FilePathFinder;
import com.applozic.mobicommons.json.GsonUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;

/**
 * Created by sunil on 10/3/16.
 */
public class ChannelNameActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "ChannelNameActivity";
    private EditText channelName;
    private Button ok, cancel;
    public static final String CHANNEL_NAME = "CHANNEL_NAME";
    public static final String CHANNEL_IMAGE_URL = "IMAGE_URL";
    private static final int REQUEST_CODE_ATTACH_PHOTO = 701;
    String oldChannelName;
    ActionBar mActionBar;
    private ImageView selectImageProfileIcon;
    private ImageView applozicGroupProfileIcon;
    GroupInfoUpdate groupInfoUpdate;
    private LinearLayout layout;
    private Uri imageChangeUri;
    private Snackbar snackbar;
    private ApplozicPermissions applozicPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_channel_name_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        layout = (LinearLayout) findViewById(R.id.footerAd);
        applozicPermissions = new ApplozicPermissions(this, layout);
        mActionBar.setTitle(getString(R.string.update_channel_title_name));
        selectImageProfileIcon = (ImageView) findViewById(R.id.applozic_group_profile_camera);
        applozicGroupProfileIcon = (ImageView) findViewById(R.id.applozic_group_profile);

        if (getIntent().getExtras() != null) {
            String groupInfoJson  = getIntent().getExtras().getString(ChannelInfoActivity.GROUP_UPDTAE_INFO);
            groupInfoUpdate = (GroupInfoUpdate) GsonUtils.getObjectFromJson(groupInfoJson,GroupInfoUpdate.class);
        }

        if( groupInfoUpdate!=null && !TextUtils.isEmpty(groupInfoUpdate.getLocalImagePath())){
            Uri uri =  Uri.fromFile(new File(groupInfoUpdate.getLocalImagePath()));
            if(uri!=null){
                Log.i("ChannelNameActivity::",   uri.toString());
                applozicGroupProfileIcon.setImageURI(uri);
            }
        }else{
            applozicGroupProfileIcon.setImageResource(R.drawable.applozic_group_icon);

        }
        channelName = (EditText) findViewById(R.id.newChannelName);
        channelName.setText( groupInfoUpdate.getNewName());
        ok = (Button) findViewById(R.id.channelNameOk);
        cancel = (Button) findViewById(R.id.channelNameCancel);
        selectImageProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImagePicker();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (groupInfoUpdate.getNewName().equals(channelName.getText().toString())&& imageChangeUri==null) {
                    ChannelNameActivity.this.finish();
                }
                if (TextUtils.isEmpty(channelName.getText().toString()) || channelName.getText().toString().trim().length() == 0) {

                    Toast.makeText(ChannelNameActivity.this, getString(R.string.channel_name_empty), Toast.LENGTH_SHORT).show();
                    ChannelNameActivity.this.finish();

                } else {
                    Intent intent = new Intent();
                    groupInfoUpdate.setNewName(channelName.getText().toString());
                    if(imageChangeUri!=null){
                        String filePath = FilePathFinder.getPath(ChannelNameActivity.this, imageChangeUri);
                        groupInfoUpdate.setNewlocalPath(filePath);
                    }
                    intent.putExtra(ChannelInfoActivity.GROUP_UPDTAE_INFO,GsonUtils.getJsonFromObject(groupInfoUpdate,GroupInfoUpdate.class));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelNameActivity.this.finish();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionsUtils.REQUEST_STORAGE) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                processImagePicker();
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Uri selectedFileUri=null;
        if(requestCode == REQUEST_CODE_ATTACH_PHOTO  && resultCode == RESULT_OK){
            selectedFileUri = (intent == null ? null : intent.getData());
            beginCrop(selectedFileUri);
        }
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            try{
                imageChangeUri = Crop.getOutput(intent);
                applozicGroupProfileIcon.setImageDrawable(null); // <--- added to force redraw of ImageView
                applozicGroupProfileIcon.setImageURI(imageChangeUri);
            }catch (Exception e){
                Log.i(TAG, "exception in profile image");
            }
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(),groupInfoUpdate.getGroupId()+ "_profile_temp.jpeg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    public void processImagePicker(){
        if(Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)){
            applozicPermissions.requestStoragePermissions();
        }else {
            Intent getContentIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getContentIntent, REQUEST_CODE_ATTACH_PHOTO);
        }
    }

    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(layout, resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

}
