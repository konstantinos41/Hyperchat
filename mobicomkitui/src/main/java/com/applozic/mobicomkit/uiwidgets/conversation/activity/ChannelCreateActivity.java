package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.RegisteredUsersAsyncTask;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.feed.RegisteredUsersApiResponse;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.FilePathFinder;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.soundcloud.android.crop.Crop;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sunil on 3/2/16.
 */


public class ChannelCreateActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_CODE_ATTACH_PHOTO = 901;
    private static final String TAG = "ChannelCreateActivity";
    public static String GROUP_TYPE = "GroupType";
    private EditText channelName;
    private CircleImageView circleImageView;
    private View focus;
    private ActionBar mActionBar;
    private ImageView uploadImageButton;
    private Uri imageChangeUri;
    private String groupIconImageLink;
    private int groupType;
    private LinearLayout layout;
    private Snackbar snackbar;
    private ApplozicPermissions applozicPermissions;
    private FinishActivityReceiver finishActivityReceiver;
    public static final String ACTION_FINISH_CHANNEL_CREATE =
            "channelCreateActivity.ACTION_FINISH";
    MobiComUserPreference userPreference;
    AlCustomizationSettings alCustomizationSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_create_activty_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        String jsonString = FileUtils.loadSettingsJsonFile(getApplicationContext());
        if(!TextUtils.isEmpty(jsonString)){
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString,AlCustomizationSettings.class);
        }else {
            alCustomizationSettings =  new AlCustomizationSettings();
        }
        userPreference = MobiComUserPreference.getInstance(ChannelCreateActivity.this);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.channel_create_title);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        finishActivityReceiver = new FinishActivityReceiver();
        registerReceiver(finishActivityReceiver, new IntentFilter(ACTION_FINISH_CHANNEL_CREATE));
        layout = (LinearLayout) findViewById(R.id.footerAd);
        applozicPermissions = new ApplozicPermissions(this, layout);
        channelName = (EditText) findViewById(R.id.channelName);
        circleImageView = (CircleImageView) findViewById(R.id.channelIcon);
        uploadImageButton = (ImageView) findViewById(R.id.applozic_channel_profile_camera);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processImagePicker();
            }
        });

        if(getIntent() != null){
            groupType = getIntent().getIntExtra(GROUP_TYPE, Channel.GroupType.PUBLIC.getValue().intValue());
        }
       /* groupType = getIntent().getIntExtra(GROUP_TYPE, Channel.GroupType.PRIVATE.getValue().intValue());
        if(groupType.equals(Channel.GroupType.BROADCAST.getValue().intValue())){
            circleImageView.setImageResource(R.drawable.applozic_ic_applozic_broadcast);
            uploadImageButton.setVisibility(View.GONE);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_create_menu, menu);
        menu.removeItem(R.id.Done);
        menu.findItem(R.id.menu_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.Next) {
            boolean check = true;
            if (channelName.getText().toString().trim().length() == 0 || TextUtils.isEmpty(channelName.getText().toString())) {
                focus = channelName;
                focus.requestFocus();
                check = false;
            }
            if (check) {
                Utils.toggleSoftKeyBoard(ChannelCreateActivity.this, true);
                if (alCustomizationSettings.getTotalRegisteredUserToFetch() > 0 && alCustomizationSettings.isRegisteredUserContactListCall() && !userPreference.getWasContactListServerCallAlreadyDone()) {
                    processDownloadRegisteredUsers();
                }else {
                    Intent intent = new Intent(ChannelCreateActivity.this, ContactSelectionActivity.class);
                    intent.putExtra(ContactSelectionActivity.CHANNEL, channelName.getText().toString());
                    if (!TextUtils.isEmpty(groupIconImageLink)) {
                        intent.putExtra(ContactSelectionActivity.IMAGE_LINK, groupIconImageLink);
                    }
                    intent.putExtra(ContactSelectionActivity.GROUP_TYPE, groupType);
                    startActivity(intent);
                }

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void processDownloadRegisteredUsers(){
        final ProgressDialog progressDialog = ProgressDialog.show(ChannelCreateActivity.this, "",
                getString(R.string.applozic_contacts_loading_info), true);

        RegisteredUsersAsyncTask.TaskListener usersAsyncTaskTaskListener = new RegisteredUsersAsyncTask.TaskListener() {
            @Override
            public void onSuccess(RegisteredUsersApiResponse registeredUsersApiResponse, String[] userIdArray) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                userPreference.setWasContactListServerCallAlreadyDone(true);
                Intent intent = new Intent(ChannelCreateActivity.this, ContactSelectionActivity.class);
                intent.putExtra(ContactSelectionActivity.CHANNEL, channelName.getText().toString());
                if (!TextUtils.isEmpty(groupIconImageLink)) {
                    intent.putExtra(ContactSelectionActivity.IMAGE_LINK, groupIconImageLink);
                }
                intent.putExtra(ContactSelectionActivity.GROUP_TYPE, groupType);
                startActivity(intent);

            }

            @Override
            public void onFailure(RegisteredUsersApiResponse registeredUsersApiResponse, String[] userIdArray, Exception exception) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                String error = getString(Utils.isInternetAvailable(ChannelCreateActivity.this) ? R.string.applozic_server_error : R.string.you_need_network_access_for_block_or_unblock);
                Toast toast = Toast.makeText(ChannelCreateActivity.this, error, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            @Override
            public void onCompletion() {

            }
        };
        RegisteredUsersAsyncTask usersAsyncTask  = new RegisteredUsersAsyncTask(ChannelCreateActivity.this,usersAsyncTaskTaskListener, alCustomizationSettings.getTotalRegisteredUserToFetch(), userPreference.getRegisteredUsersLastFetchTime(),null,null,true);
        usersAsyncTask.execute((Void)null);

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "new_group_profile.jpeg"));
        Crop.of(source, destination).asSquare().start(this);
    }

    public void processImagePicker() {
        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)) {
            applozicPermissions.requestStoragePermissions();
        } else {
            Intent getContentIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getContentIntent, REQUEST_CODE_ATTACH_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Uri selectedFileUri = null;
        if (requestCode == REQUEST_CODE_ATTACH_PHOTO && resultCode == RESULT_OK) {
            selectedFileUri = (intent == null ? null : intent.getData());
            Log.i(TAG, "selectedFileUri :: " + selectedFileUri);
            beginCrop(selectedFileUri);
        }
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            try {
                imageChangeUri = Crop.getOutput(intent);
                circleImageView.setImageDrawable(null); // <--- added to force redraw of ImageView
                circleImageView.setImageURI(imageChangeUri);
                new ProfilePictureUpload(imageChangeUri, ChannelCreateActivity.this).execute((Void[]) null);
            } catch (Exception e) {
                Log.i(TAG, "exception in profile image");
            }
        }

    }

    class ProfilePictureUpload extends AsyncTask<Void, Void, Boolean> {

        Context context;
        Uri fileUri;
        String displayName;
        private ProgressDialog progressDialog;

        public ProfilePictureUpload(Uri fileUri, Context context) {
            this.context = context;
            this.fileUri = fileUri;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",
                    context.getString(R.string.applozic_contacts_loading_info), true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            FileClientService fileClientService = new FileClientService(context);
            try {
                if (fileUri != null) {
                    String filePath = FilePathFinder.getPath(context, fileUri);
                    groupIconImageLink = fileClientService.uploadProfileImage(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(ChannelCreateActivity.class.getName(), "Exception");

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            progressDialog.dismiss();
        }

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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(layout, resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(finishActivityReceiver);
    }

    private final class FinishActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ACTION_FINISH_CHANNEL_CREATE))
                finish();
        }
    }

}
