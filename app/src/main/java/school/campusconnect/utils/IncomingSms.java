package school.campusconnect.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import school.campusconnect.activities.UserExistActivity;


public class IncomingSms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AppLog.e("BROADCAST", "onReceive");

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            assert status != null;
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    AppLog.e("BROADCAST", "message : " + message);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    decode(message);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    AppLog.e("BROADCAST", "TIMEOUT : ");
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    break;
            }
        }
    }

    private void decode(String message) {
        try {
            String code = parseCode(message);
            if (UserExistActivity.getInstance() != null)
                UserExistActivity.getInstance().recivedSms(code);
            else {
                AppLog.e("BROADCAST", "not Signup");
            }
        } catch (Exception e) {
            AppLog.e("BROADCAST", "Catch1\n" + e.getMessage() + "\n" + e.toString());
        }
    }
    public String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }
}

