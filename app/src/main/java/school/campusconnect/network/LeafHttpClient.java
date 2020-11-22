package school.campusconnect.network;

import android.content.Context;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import school.campusconnect.utils.AppLog;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import school.campusconnect.database.LeafPreference;


public class LeafHttpClient extends OkHttpClient {

    public LeafHttpClient(final Context context) {
        super();
        newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder builder = original.newBuilder();

                builder.header("Authorization","Bearer "+ LeafPreference.getInstance(context).getString(LeafPreference.TOKEN));

                Request request = builder.method(original.method(), original.body())
                        .build();
                AppLog.e("sdfgf",request.url().toString());
                AppLog.e("sfds",request.header("Authorization"));
                AppLog.e("sfds", "header not called");
                return chain.proceed(request);
            }
        }).build();
    }
}
