package school.campusconnect.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.utils.AppLog;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LeafApiClient {
    final ConcurrentHashMap<Class, Object> services;
    Retrofit retrofit;
    private static LeafApiClient sInstance;

    public static LeafApiClient getInstance(Context context) {
//        if (sInstance == null) {
            sInstance = new LeafApiClient(context);
//        }
        return sInstance;
    }

    public static LeafApiClient getInstanceYoutube(Context context) {
       AppLog.e("YOTU", "getInstanceYoutube");
//        if (sInstance == null) {
            sInstance = new LeafApiClient(context, false);
//        }
        return sInstance;
    }

    private LeafApiClient(Context context) {
        services = new ConcurrentHashMap<>();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder().client(getHttpClient(context)).
                baseUrl(new LeafApi().getBaseHostUrl()).addConverterFactory(GsonConverterFactory.create(gson)).build();
    }

    private LeafApiClient(Context context, boolean flag) {
        services = new ConcurrentHashMap<>();
       AppLog.e("YOTU", "LeafApiClient");
        retrofit = new Retrofit.Builder().client(getHttpClientYoutube(context)).
                baseUrl(new LeafApi().getBaseHostUrl()).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public <T> T getService(Class<T> cls) {
        return this.getRetrofitService(this.retrofit, cls);
    }

    public <T> T getRetrofitService(Retrofit retrofit, Class<T> cls) {
        if (!this.services.contains(cls)) {
            this.services.putIfAbsent(cls, retrofit.create(cls));
        }
        return (T) this.services.get(cls);
    }

    private OkHttpClient getHttpClient(final Context context)
    {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder builder = original.newBuilder();

                builder.header("Authorization","Bearer "+ LeafPreference.getInstance(context.getApplicationContext()).getString(LeafPreference.TOKEN));

                Request request = builder.method(original.method(), original.body())
                        .build();
                AppLog.e("sdfgf",request.url().toString());
                AppLog.e("sfds",request.header("Authorization"));
                AppLog.e("sfds", "header not called");
                return chain.proceed(request);
            }
        }).build();
    }
    private OkHttpClient getHttpClientYoutube(final Context context)
    {
        return new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).addInterceptor(new Interceptor() {
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
