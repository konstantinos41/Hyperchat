package com.example.konstantinos.hyperplane;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class LoginSignupActivity extends AppCompatActivity
{
    public static String Name, Email, UserID;
    private CallbackManager callbackManager;
    private TextView info;
    private LoginButton loginButton;
    public static AccessToken token;

    public static Context applozicContext;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login_signup);
        info = (TextView)findViewById(R.id.info);




        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions( Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("User ID:  " +
                        loginResult.getAccessToken().getUserId() + "\n" +
                        "Auth Token: " + loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
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
                                    email =  jsonObject.getString("email");
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

                                loginSignupToApplozic(nombre,email,id);
                                startActivity(new Intent(LoginSignupActivity.this, TabbedActivity.class));

                            }
                        });



                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

                token = loginResult.getAccessToken();

            }

            @Override
            public void onCancel() {
                info.setText("Login attempt cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText(e.toString());
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void loginSignupToApplozic(String name, String email, String id)
    {
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context)
            {
                //After successful registration with Applozic server the callback will come here
                PushNotificationTask pushNotificationTask = null;
                PushNotificationTask.TaskListener listener=  new PushNotificationTask.TaskListener() {
                    @Override
                    public void onSuccess(RegistrationResponse registrationResponse) {

                    }
                    @Override
                    public void onFailure(RegistrationResponse registrationResponse, Exception exception) {

                    }

                };
                pushNotificationTask = new PushNotificationTask(Applozic.getInstance(context).getDeviceRegistrationId(),listener,context);
                pushNotificationTask.execute((Void)null);

                applozicContext = context;

                ApplozicClient.getInstance(context).hideChatListOnNotification();
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                //If any failure in registration the callback  will come here
            }};


        User user = new User();
        user.setUserId(id); //userId it can be any unique user identifier
        user.setDisplayName(name); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(email); //optional
        user.setImageLink("https://graph.facebook.com/" + id + "/picture?type=large");//optional,pass your image link
        new UserLoginTask(user, listener, LoginSignupActivity.this).execute((Void) null);
    }
}
