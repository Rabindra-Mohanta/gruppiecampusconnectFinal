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


public class LeafHttpClientYoutube extends OkHttpClient {

    public LeafHttpClientYoutube(final Context context) {
        super();
        newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                AppLog.e("sfds", "header called");
                Request.Builder builder = original.newBuilder();

                builder.header("Authorization", "Bearer " + LeafPreference.getInstance(context).getString(LeafPreference.TOKEN)).header("clientId", "4c834078acd49913fb86cd9e86d6c20ae3857b4b")
                        .header("clientPassword", "9a36b09fc659db246cad6b0d18174ba844027cff");
//                builder.header("clientId", "4c834078acd49913fb86cd9e86d6c20ae3857b4b");
//                builder.header("clientPassword", "9a36b09fc659db246cad6b0d18174ba844027cff");

                Request request = builder.method(original.method(), original.body())
                        .build();
                AppLog.e("sdfgf", request.url().toString());
                AppLog.e("sfds", request.header("Authorization"));
                AppLog.e("sfds", request.header("clientId"));
                AppLog.e("sfds", request.header("clientPassword"));

                return chain.proceed(request);
            }
        }).build();

    }
}
