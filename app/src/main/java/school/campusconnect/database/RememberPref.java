package school.campusconnect.database;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by frenzin05 on 8/31/2017.
 */

public class RememberPref {
    public static final String REMEMBER_USERNAME = "REMEMBER_USERNAME";
    public static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    private static RememberPref sInstance;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;

    private RememberPref(Context context) {
        sPref = context.getSharedPreferences("nnr.electionleaf.remember", Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public static RememberPref getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RememberPref(context);
        }
        return sInstance;
    }
    public void clearData() {
        sEditor.clear().apply();
    }

    public void setString(String key, String value) {
        sEditor.putString(key, value).apply();
    }


    public void remove(String key) {
        sEditor.remove(key).commit();
    }

    public String getString(String key) {
        return sPref.getString(key, "");
    }

    public  boolean contains(String key)
    {
        return sPref.contains(key);
    }


}
