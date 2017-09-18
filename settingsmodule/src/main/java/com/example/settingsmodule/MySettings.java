package com.example.settingsmodule;

import android.content.SharedPreferences;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by George on 9/17/2017.
 *
 * This class makes all needed communication with internal storage to save and retrieve users settings
 * Any other class needing info about the user's settings should get them through the public methods of this class
 */

public class MySettings {

    private static SharedPreferences sharedPreferences;
    private static final String NotificationPREFERENCES           = "NotificationPreferences";
    private static Context context;

    //region key values to retrieve notification preferences

    private static final String NotificationSoundNewMessage       = "NotificationSoundNewMessage";
    private static final String NotificationVibrationNewMessage   = "NotificationVibrationNewMessage";
    private static final String NotificationLedNewMessage         = "NotificationLedNewMessage";

    private static final String NotificationSoundNewGroup       = "NotificationSoundNewGroup";
    private static final String NotificationVibrationNewGroup   = "NotificationVibrationNewGroup";
    private static final String NotificationLedNewGroup         = "NotificationLedNewGroup";

    //endregion

    //region Variables to cache the settings when application is open


    private static boolean hasNotificationSoundNewMessage;
    private static boolean hasNotificationVibrationNewMessage;
    private static boolean hasNotificationLedNewMessage;
    private static boolean hasNotificationSoundNewGroup;
    private static boolean hasNotificationVibrationNewGroup;
    private static boolean hasNotificationLedNewGroup;

    //endregion

    //region getters and setters for the above
    public static boolean isHasNotificationSoundNewMessage() {
        return hasNotificationSoundNewMessage;
    }
    public static void setHasNotificationSoundNewMessage(boolean hasNotificationSoundNewMessage) {
        MySettings.hasNotificationSoundNewMessage = hasNotificationSoundNewMessage;
    }
    public static boolean isHasNotificationVibrationNewMessage() {
        return hasNotificationVibrationNewMessage;
    }
    public static void setHasNotificationVibrationNewMessage(boolean hasNotificationVibrationNewMessage) {
        MySettings.hasNotificationVibrationNewMessage = hasNotificationVibrationNewMessage;
    }
    public static boolean isHasNotificationLedNewMessage() {
        return hasNotificationLedNewMessage;
    }
    public static void setHasNotificationLedNewMessage(boolean hasNotificationLedNewMessage) {
        MySettings.hasNotificationLedNewMessage = hasNotificationLedNewMessage;
    }
    public static boolean isHasNotificationSoundNewGroup() {
        return hasNotificationSoundNewGroup;
    }
    public static void setHasNotificationSoundNewGroup(boolean hasNotificationSoundNewGroup) {
        MySettings.hasNotificationSoundNewGroup = hasNotificationSoundNewGroup;
    }
    public static boolean isHasNotificationVibrationNewGroup() {
        return hasNotificationVibrationNewGroup;
    }
    public static void setHasNotificationVibrationNewGroup(boolean hasNotificationVibrationNewGroup) {
        MySettings.hasNotificationVibrationNewGroup = hasNotificationVibrationNewGroup;
    }
    public static boolean isHasNotificationLedNewGroup() {
        return hasNotificationLedNewGroup;
    }
    public static void setHasNotificationLedNewGroup(boolean hasNotificationLedNewGroup) {
        MySettings.hasNotificationLedNewGroup = hasNotificationLedNewGroup;
    }

    //endregion

    //region static initializer
    static{

    }
    //endregion

    public static void  loadAllNotificationPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(NotificationPREFERENCES,0);
        hasNotificationSoundNewMessage = sharedPreferences.getBoolean(NotificationSoundNewMessage, false);
        hasNotificationVibrationNewMessage = sharedPreferences.getBoolean(NotificationVibrationNewMessage, false);
        hasNotificationLedNewMessage = sharedPreferences.getBoolean(NotificationLedNewMessage,true);
        hasNotificationSoundNewGroup = sharedPreferences.getBoolean(NotificationSoundNewGroup,false);
        hasNotificationVibrationNewGroup = sharedPreferences.getBoolean(NotificationVibrationNewGroup,true);
        hasNotificationLedNewGroup = sharedPreferences.getBoolean(NotificationLedNewGroup,true);
    }

    public static boolean saveNotificationPreferences(Context context){
        try{
            sharedPreferences = context.getSharedPreferences(NotificationPREFERENCES,0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NotificationSoundNewMessage, hasNotificationSoundNewMessage);
            editor.putBoolean(NotificationVibrationNewMessage, hasNotificationVibrationNewMessage);
            editor.putBoolean(NotificationLedNewMessage, hasNotificationLedNewMessage);
            editor.putBoolean(NotificationSoundNewGroup, hasNotificationSoundNewGroup);
            editor.putBoolean(NotificationVibrationNewGroup, hasNotificationVibrationNewGroup);
            editor.putBoolean(NotificationLedNewGroup, hasNotificationLedNewGroup);
            return true;
        }catch(Exception e)
        {
            return false;
        }
    }


}