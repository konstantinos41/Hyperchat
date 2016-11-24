package com.example.konstantinos.hyperplane;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class LoginSignupActivity extends AppCompatActivity {
    public static String Name, Email, UserID;
    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;
    public static AccessToken token;

    public static Context applozicContext;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init
        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login_signup);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        //check login status (access token)
        if (AccessToken.getCurrentAccessToken() != null) {
            loginButton.setVisibility(View.INVISIBLE);
            loginButton.setClickable(FALSE);
            requestData();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    requestData();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void requestData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            final JSONObject object,
                            GraphResponse response) {
                        // Application code
                        final JSONObject jsonObject = response.getJSONObject();
                        String nombre = "";
                        String email = "";
                        String id = "";
                        try {
                            nombre = jsonObject.getString("name");
                            email = jsonObject.getString("email");
                            id = jsonObject.getString("id");

                            System.out.println(nombre);
                            System.out.println(email);
                            System.out.println(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Name = nombre;
                        Email = email;
                        UserID = id;

                        loginSignupToApplozic(nombre, email, id);
                        startActivity(new Intent(LoginSignupActivity.this, TabbedActivity.class));
                    }
                });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();

        token = AccessToken.getCurrentAccessToken();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void loginSignupToApplozic(String name, String email, String id) {
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                //After successful registration with Applozic server the callback will come here
                PushNotificationTask pushNotificationTask = null;
                PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {

                    }

                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                    }

                };
                pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(), listener, context);
                pushNotificationTask.execute((Void) null);

                applozicContext = context;

                ApplozicClient.getInstance(context).hideChatListOnNotification();
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                //If any failure in registration the callback  will come here
            }
        };


        User user = new User();
        user.setUserId(id); //userId it can be any unique user identifier
        user.setDisplayName(name); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(email); //optional
        user.setImageLink("https://graph.facebook.com/" + id + "/picture?type=large");//optional,pass your image link
        new UserLoginTask(user, listener, LoginSignupActivity.this).execute((Void) null);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LoginSignup Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
