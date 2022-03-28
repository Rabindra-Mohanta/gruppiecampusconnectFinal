package school.campusconnect.database;

import android.content.Context;
import android.content.SharedPreferences;

public class LeafPreference {

    public static final String ADD_FRIEND = "ADD_FRIEND";
    public static final String ISTEAMUPDATED = "ISTEAMUPDATED";
    public static final String Replay_List_Changes = "Replay_List_Changes";
    public static final String PERSONAL_POST_ADDED_1 = "PERSONAL_POST_ADDED_1";
    public static final String PERSONAL_POST_ADDED_2 = "PERSONAL_POST_ADDED_2";
    public static final String TRACK_GROUP_ID = "TRACK_GROUP_ID";
    public static final String TRACK_TEAM_ID = "TRACK_TEAM_ID";
    public static final String EDIT_ATTENDANCE = "EDIT_ATTENDANCE";
    public static final String SUBMITTED_DATA = "SUBMITTED_DATA";
    public static final String IS_STUDENT_ADDED = "IS_STUDENT_ADDED";
    public static final String ISGALLERY_POST_UPDATED = "ISGALLERY_POST_UPDATED";
    public static final String IS_VENDOR_POST_UPDATED = "IS_VENDOR_POST_UPDATED";
    public static final String IS_CODE_CONDUCT_UPDATED = "IS_CODE_CONDUCT_UPDATED";
    public static final String ISTIME_TABLE_UPDATED = "ISTIME_TABLE_UPDATED";
    public static final String GROUP_COUNT = "GROUP_COUNT";
    public static final String CONST_GROUP_COUNT = "CONST_GROUP_COUNT";
    public static final String VIDEO_CLASS_LIST_OFFLINE = "VIDEO_CLASS_LIST_OFFLINE";
    public static final String HOME_LIST_OFFLINE = "HOME_LIST_OFFLINE";
    public static final String SCHOOL_LIST = "SCHOOL_LIST";
    public static final String GROUP_ID_LIST = "GROUP_ID_LIST";
    public static final String ROLE = "ROLE";

    public static final String SECRET_KEY = "SECRET_KEY";
    public static final String ACCESS_KEY = "ACCESS_KEY";

    private static LeafPreference sInstance;
    private static SharedPreferences sPref;
    private static SharedPreferences.Editor sEditor;
    public static final String LOGIN_ID = "login_id";
    public static final String TOKEN = "centerr_status";
    public static final String PIN = "pin";
    public static final String NAME = "name";
    public static final String NUM = "num";
    public static final String EMAIL = "email";
    public static final String PROFILE_COMPLETE = "profilecompletion";
    public static final String PROFILE_IMAGE = "profileimage";

    public static final String PROFILE_NAME = "PROFILE_NAME";
    public static final String PROFILE_VOTERID = "PROFILE_VOTERID";
    public static final String PROFILE_IMAGE_NEW = "PROFILE_IMAGE_NEW";

    public static final String COUNTRY_CODE = "country_code";
    public static final String CALLING_CODE = "calling_code";
    public static final String GCM_TOKEN = "gcmtoken";
    public static final String ISPROFILEUPDATED = "isprofileupdated";
    public static final String ISUSERDELETED = "isuserdeleted";
    public static final String ISUSERDELETED1 = "isuserdeleted1";
    public static final String ISGROUPUPDATED = "isgroupupdated";
    public static final String ISIMPCONTANTUPDATED = "isimpcontactupdated";
    public static final String ISFRIENDUPDATED = "isfriendsupdated";
    public static final String ISGENERALPOSTUPDATED = "isgeneralpostupdated";
    public static final String ISTEAMPOSTUPDATED = "isteampostupdated";
    public static final String ISPERSONALPOSTUPDATED = "ispersonalpostupdated";
    public static final String ISALLCONTACTSAVED = "isallcontactsaved";
    public static final String LOGOUT_FOR_212 = "logout_for_212";
    public static final String countryCode = "countryCode";
    public static final String phoneNumber = "phone";
    public static final String ISWALKTHROUGHDONE = "walkthrough";
    public static String Subscribed_Teams="Subscribed_Teams";
    public static final String OFFLINE_VIDEONAMES = "offlinevideonames";
    public static final String TOTAL_PAGE_NOTIFICATION = "totalpagenotification";
    public static final String VIDEO_CALL_START_TIME = "videocallstarttime";

    private LeafPreference(Context context) {
        sPref = context.getSharedPreferences("nnr.electionleaf", Context.MODE_PRIVATE);
        sEditor = sPref.edit();
    }

    public static LeafPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LeafPreference(context);
        }
        return sInstance;
    }
    public  boolean contains(String key)
    {
        return sPref.contains(key);
    }

    public void clearData() {
        String s = getString(GCM_TOKEN);
        sEditor.clear().commit();
        setString(GCM_TOKEN, s);

    }

    public boolean getBoolean(String key) {
        return sPref.getBoolean(key, false);
    }

    public void setBoolean(String key, boolean value) {
        sEditor.putBoolean(key, value).commit();
    }

    public int getInt(String key) {
        return sPref.getInt(key, 0);
    }

    public long getLong(String key) {
        return sPref.getLong(key, 0);
    }

    public void setInt(String key, int value) {
        sEditor.putInt(key, value).commit();
    }

    public void setLong(String key, long value) {
        sEditor.putLong(key, value).commit();

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
    public String getString(String key,String defVal) {
        return sPref.getString(key, defVal);
    }
    public String getUserId(){
        return getString(LeafPreference.LOGIN_ID);
    }
    public String getUserName(){
        return getString(LeafPreference.NAME);
    }
}
