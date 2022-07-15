package school.campusconnect.utils;

import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import school.campusconnect.database.LeafPreference;

public class AmazoneHelper {
    private static AmazonS3Client sS3Client;
//    public static final String BUCKET_NAME_URL = "https://gruppie.sgp1.digitaloceanspaces.com/";
    public static final String BUCKET_NAME_URL = "https://gruppiemedia.sgp1.digitaloceanspaces.com/";
//    public static final String BUCKET_NAME = "gruppie";
    public static final String BUCKET_NAME = "gruppiemedia";
    public static final String PATH_REPLACE = "gruppiemedia.sgp1.digitaloceanspaces.com";
    public static final String BUCKET_REGION = "ap-southeast-1";
    private static TransferUtility sTransferUtility;

    private static String part1 = "4LG3TFWU";
    private static String part3 = "AKIA6N";
    private static String part2 = "3553U7";

    private static String c1 = "hrGUSs";
    private static String c3 = "EMAzAfsPWYUSHg";
    private static String c5 = "/n/kGSvRIfcydua";
    private static String c2 = "rhT0T";

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = TransferUtility.builder().s3Client(getS3Client(context.getApplicationContext()))
                    .context(context.getApplicationContext()).build();
        }
        return sTransferUtility;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            ClientConfiguration cc = new ClientConfiguration();
            cc.setSocketTimeout(300000);
            cc.setConnectionTimeout(30000);
            sS3Client = new AmazonS3Client(new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    //return BuildConfig.AWSAccessKey1+BuildConfig.AWSAccessKey2;
                 //   return "FAWMZVH2FHNS7BNKXXUD";
                    return LeafPreference.getInstance(context).getString(LeafPreference.ACCESS_KEY);
                }

                @Override
                public String getAWSSecretKey() {
                    //return BuildConfig.AWSSecretKey1+BuildConfig.AWSSecretKey2;
                   // return "vI3HRV+G2xZXRXa8SWI26Od7XxdyWNbFXwNLbWy2C+Y";
                    return LeafPreference.getInstance(context).getString(LeafPreference.SECRET_KEY);
                }
            }, cc);
            sS3Client.setEndpoint("sgp1.digitaloceanspaces.com");
        }
        return sS3Client;
    }

    public static String getAmazonS3Key(String fileType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        String key;
        if (Constants.FILE_TYPE_VIDEO.equals(fileType)) {
            key = "videos/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) +".mp4";
        } else if (Constants.FILE_TYPE_PDF.equals(fileType)) {
            key = "pdf/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) +".pdf";
        } else if (Constants.FILE_TYPE_AUDIO.equals(fileType)) {
            key = "audio/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) +".mp3";
        }else {
            key = "images/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) + ".jpg";
        }
        return key;
        // "gruppie_" + simpleDateFormat.format(new Date())+new Random().nextInt(999);
    }
    public static String getAmazonS3KeyThumbnail(String fileType) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
        String key;
        if (Constants.FILE_TYPE_VIDEO.equals(fileType)) {
            key = "videos/thumbnail/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) +".jpg";
        } else if (Constants.FILE_TYPE_PDF.equals(fileType)) {
            key = "pdf/thumbnail/gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) + ".jpg";
        }else {
            key = "gruppie_" + simpleDateFormat.format(new Date()) + new Random().nextInt(999) + ".jpg";
        }
        return key;
        // "gruppie_" + simpleDateFormat.format(new Date())+new Random().nextInt(999);
    }

}
