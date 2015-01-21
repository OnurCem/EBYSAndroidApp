package ocs.com.ebys;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Onur Cem on 1/21/2015.
 */
public class PreferenceController {
    private static final String PREFERENCES_FILE = "prefs";
    public static final String PREF_USERNAME = "username";
    public static final String PREF_PASSWORD = "password";

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
}
