package com.applozic.mobicomkit.uiwidgets.people.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.attachment.FileClientService;

import com.applozic.mobicomkit.contact.AppContactService;

import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.PictureUploadPopUpFragment;
import com.applozic.mobicommons.commons.image.ImageLoader;
import com.applozic.mobicommons.commons.image.ImageUtils;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicomkit.uiwidgets.R;

import java.io.File;


public class ProfileFragment extends Fragment {

    public static final int REQUEST_CODE_ATTACH_PHOTO = 101;
    public static final int REQUEST_REMOVE_PHOTO = 102;
    private static final String TAG = "ProfileFragment";
    public static final String ProfileFragmentTag = "ProfileFragment";
    public static final int PROFILE_UPDATED = 1001;
    public static final int LINE_WIDTH = 2;
    public static final float LEFT_MARGIN = 7.0f;
    private ImageView img_profile;
    private ImageView selectImageProfileIcon,statusEdit;
    private Button logoutbtn;
    private TextView displayNameText;
    private TextView statusText;
    private String DEFAULT_CONATCT_IMAGE= "applozic_default_contactImg.jpeg";

    private ImageLoader mImageLoader; // Handles loading the contact image in a background thread
    AppContactService contactService;
    Contact userContact;
    private String changedStatusString;
    AlCustomizationSettings alCustomizationSettings;

    public void setAlCustomizationSettings(AlCustomizationSettings alCustomizationSettings) {
        this.alCustomizationSettings = alCustomizationSettings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_profile, container, false);


        img_profile = (ImageView) view.findViewById(R.id.applozic_user_profile);
        statusEdit = (ImageView) view.findViewById(R.id.status_edit_btn);
        selectImageProfileIcon = (ImageView) view.findViewById(R.id.applozic_user_profile_camera);
        logoutbtn =  (Button) view.findViewById(R.id.applozic_profile_logout);
        displayNameText = (TextView) view.findViewById(R.id.applozic_profile_displayname);
        statusText = (TextView) view.findViewById(R.id.applozic_profile_status);

        setupDeviderView(view,R.id.applozic_profile_section_rl,R.id.applozic_profile_verticalline_rl);
        setupDeviderView(view,R.id.applozic_datausage_section_rl,R.id.applozic_datausage_verticalline_rl);
        setupDeviderView(view,R.id.applozic_notification_section_rl,R.id.applozic_notification_verticalline_rl);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbar.setClickable(false);
        toolbar.setTitle(getString(R.string.applozic_user_profile_heading));
        toolbar.setSubtitle("");
        setHasOptionsMenu(true);

        contactService = new AppContactService(getActivity());
        userContact = contactService.getContactById(MobiComUserPreference.getInstance(getActivity()).getUserId());
        displayNameText.setText(userContact.getDisplayName());

        if(!TextUtils.isEmpty(userContact.getStatus())){
            statusText.setText(userContact.getStatus());
        }
        final  Context context = getActivity().getApplicationContext();
        mImageLoader = new ImageLoader(context, img_profile.getHeight()) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return contactService.downloadContactImage(context, (Contact) data);
            }
        };
        //For profile image
        selectImageProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPhotoOption();
            }
        });


        if(alCustomizationSettings.isProfileOption()){
            logoutbtn.setVisibility(View.VISIBLE);
        }else {
            logoutbtn.setVisibility(View.GONE);
        }
        logoutbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    final String logoutActivity = ApplozicSetting.getInstance(getActivity()).getActivityCallback(ApplozicSetting.RequestCode.USER_LOOUT);
                    if (!TextUtils.isEmpty(logoutActivity)) {
                        new UserClientService(getActivity()).logout();
                        Intent intent = new Intent(getActivity(), Class.forName(logoutActivity));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        getActivity().finish();
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        statusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Status");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changedStatusString = input.getText().toString();
                        new ProfilePictureUpload(changedStatusString,getActivity()).execute((Void[]) null);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        mImageLoader.setImageFadeIn(false);
        mImageLoader.setLoadingImage(R.drawable.applozic_ic_contact_picture_180_holo_light);
        mImageLoader.loadImage(userContact, img_profile);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.refresh).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
    }

    private void setupDeviderView(View view,int parentLayout, int childVerticalLineLayout) {
        final RelativeLayout layout = (RelativeLayout) view.findViewById(parentLayout);
        final RelativeLayout childLayout = (RelativeLayout)view.findViewById(childVerticalLineLayout);
        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();



        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int height = layout.getMeasuredHeight();
                float marginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LEFT_MARGIN, getActivity().getResources().getDisplayMetrics());
                float liineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, LINE_WIDTH, getActivity().getResources().getDisplayMetrics());
                RelativeLayout.LayoutParams layoutPrams = new RelativeLayout.LayoutParams((int)liineWidth, height );
                layoutPrams.setMargins((int)marginPx,0,0,0);
                childLayout.setLayoutParams(layoutPrams);
            }
        });
    }


    public void processPhotoOption() {
        try {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            DialogFragment fragment = new PictureUploadPopUpFragment();
            fragment.setTargetFragment(this, REQUEST_CODE_ATTACH_PHOTO);
            FragmentTransaction fragmentTransaction = supportFragmentManager
                    .beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragment.show(fragmentTransaction, "PhotosAttachmentFragment");


        } catch (Exception e) {

        }

    }

    public void handleProfileimageUpload(Uri imageUri) {
        img_profile.setImageDrawable(null);
        img_profile.setImageURI(imageUri);
        new ProfilePictureUpload(imageUri,getActivity()).execute((Void[]) null);
    }



    class ProfilePictureUpload extends AsyncTask<Void, Void, Boolean> {

        Context context;
        Uri fileUri;
        String displayName;
        String status;
        private ProgressDialog progressDialog;

        public ProfilePictureUpload( Uri fileUri ,Context context) {
            this.context = context;
            this.fileUri=fileUri;
        }
        public ProfilePictureUpload( String status ,Context context) {
            this.context = context;
            this.status=status;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "",
                    context.getString(R.string.applozic_contacts_loading_info), true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            FileClientService fileClientService =new FileClientService(getActivity());
            UserService userService =  UserService.getInstance(context);
            try {
                String response =null;
                String filePath=null;
                if(fileUri!=null){
                    File myFile = new File(fileUri.getPath());
                    response= fileClientService.uploadProfileImage(myFile.getAbsolutePath());
                    filePath =  myFile.getAbsolutePath();
                }
                if(TextUtils.isEmpty(displayName)){
                    this.displayName = userContact.getDisplayName();
                }
                userService.updateDisplayNameORImageLink(displayName,response,filePath,status);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(ProfileFragment.class.getName(),  "Exception");

            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            if(!TextUtils.isEmpty(changedStatusString)){
                statusText.setText(changedStatusString);
            }
            progressDialog.dismiss();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, intent);
            File file = FileClientService.getFilePath(DEFAULT_CONATCT_IMAGE,getContext(), "image",true);
            if(file==null || !file.exists()) {
                Log.i(TAG,"file not found,exporting it from drawable");
                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.applozic_ic_contact_picture_180_holo_light);
                String filePath = ImageUtils.saveImageToInternalStorage(FileClientService.getFilePath(DEFAULT_CONATCT_IMAGE, getActivity().getApplicationContext(), "image", true), bm);
                file= new File(filePath);
            }
            handleProfileimageUpload(Uri.fromFile(file));
        } else {
            Log.i(TAG, "Activity result failed with code: " + resultCode);
        }

    }

}
