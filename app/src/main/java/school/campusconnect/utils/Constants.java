package school.campusconnect.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

import school.campusconnect.BuildConfig;

/**
 * Created by frenzin04 on 2/13/2017.
 */

public class Constants {
    public static final String VIVID_GROUP_ID =  "";//  First Group Id

    public static final String APP_KEY = "NezBAck80EPh2KCsJ5RiynKm20dznUI2lVIk" ; //zoom key
    public static final String APP_SECRET = "IXvTUJTYKplPT7KNZWhpOAQO328fR6OwEeAB"; // zoom secret key
    public static final String WEB_DOMAIN = ""; //zooom domain name


    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FILE_TYPE_PDF = "pdf";
    public static final String FILE_TYPE_YOUTUBE = "youtube";
    public static final String FILE_TYPE_IMAGE = "image";

    public static final String GROUP_DATA = "GROUP_DATA";
    public static final String TOTAL_MEMBER = "TOTAL_MEMBER";
    public static final int MAX_IMAGE_NUM = 4;

    public static final long INTERVAL = 15000;
    public static final long INTERVAL_FAST = 14000;
    public static final String CATEGORY_SCHOOL = "school";
    public static final String BACKGROUND_IMAGE = "BACKGROUND_IMAGE";
    public static final String FILE_TYPE_VIDEO = "video";


    public static int image_width = 1800;
    public static int image_height = 1200;

    public static int requestCode = -1;
    public static boolean finishActivity;

    public static final int notFinishCode = 1;
    public static final int finishCode = 2;

    public static int screen_width;
    public static int screen_height;

    public static final String TEAM_TYPE_DEFAULT="default";
    public static final String TEAM_TYPE_CREATED="created";


    public static String[] getAdminArray(String[] currentArray) {
        if (currentArray != null) {
            String[] result = new String[currentArray.length];

            for (int i = 0; i < result.length; i++) {
                result[i] = "a" + currentArray[i] + "a";
            }

            return result;
        } else
            return new String[0];
    }
    public static String encodeStringToBase64(String text) {
        byte[] data;
        try {
            data = text.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    public static String decodeUrlToBase64(String base64) {
        try {
            if(base64.contains("http"))
            {

                base64= base64.replace("gruppie-phoenix-backend.s3.amazonaws.com" , AmazoneHelper.PATH_REPLACE);
                base64 = base64.replace("gruppie-phoenix-backend.s3.ap-southeast-1.amazonaws.com" ,AmazoneHelper.PATH_REPLACE);
                AppLog.e("Constants","decode url :"+base64);
                return base64;
            }
            byte[] data = Base64.decode(base64, Base64.DEFAULT);
            String text;
            text = new String(data, "UTF-8");
            AppLog.e("Constants","decode url :"+text);

            text= text.replace("gruppie-phoenix-backend.s3.amazonaws.com" , AmazoneHelper.PATH_REPLACE);
            text= text.replace("gruppie-phoenix-backend.s3.ap-southeast-1.amazonaws.com" , AmazoneHelper.PATH_REPLACE);


            AppLog.e("Constants","decode n replaced url :"+text);


            return text;
        } catch (UnsupportedEncodingException | NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return base64;
    }

    /**
     * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
     *
     * @param n         the number to format
     * @param iteration in fact this is the class from the array c
     * @return a String representing the number n formatted in a cool looking way.
     */
    public static String coolFormat(double n, int iteration) {
        if (n < 1000)
            return String.valueOf((int) n);
        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : coolFormat(d, iteration + 1));

    }
    private static char[] c = new char[]{'k', 'm', 'b', 't'};
}
