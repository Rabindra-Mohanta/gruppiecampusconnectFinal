package school.campusconnect.network;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import school.campusconnect.utils.AppLog;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;


public class ResponseWrapper<T> {

    private Call<T> mRequest;


    public ResponseWrapper(Call<T> service) {
        mRequest = service;
    }

    @SuppressWarnings("unchecked")
    public void execute(final int apiId, final ResponseHandler handler, final Type type) {

        mRequest.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                String errorBody;

                AppLog.e("ResponseWrapperCode", "code is " + response.code());



                switch (response.code()) {
                    case 200:
                    case 201:
                        AppLog.e("ResponseWrapper201", new Gson().toJson(response.body()));
                        handler.handle200(apiId, response.body());

                        break;
                    case 404:
                        AppLog.e("ResponseWrapper404", new Gson().toJson(response));
                        try {
                            errorBody = response.errorBody().string();
                            handler.handleError(apiId, response.code(), new Gson().fromJson(errorBody, type));
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppLog.e("app", e.toString());
                            handler.handleException(apiId, new Exception(getErrorMsg(e)));
                        }
                        break; // commit karo pehle commit hi he
                    default:
                        AppLog.e("ResponseWrapperDefault", new Gson().toJson(response));
                        try {
                            errorBody = response.errorBody().string();
                            AppLog.e("errorBody : ",errorBody);
                            handler.handleError(apiId, response.code(), new Gson().fromJson(errorBody, type));
                        } catch (Exception e) {
                            e.printStackTrace();
                            AppLog.e("app", e.toString());
                            handler.handleException(apiId, new Exception(getErrorMsg(e)));
                        }

//                    case 400:
//                        try {
//                            errorBody = response.errorBody().string();
//                            handler.handle400(new Gson().fromJson(errorBody, type));
//                        } catch (IOException e) {
//                            e.printStackTrace(); 9901721897 maha
//
//                        }

                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                t.printStackTrace();
                String str = "Unable to process response.Please try again.";
                if (t.getMessage() != null) {
                    str = str + " " + t.getMessage();
                }
                handler.handleException(apiId, new Exception(str));
            }
        });
    }

    public interface ResponseHandler<U, V> {

        void handle200(int apiId, U response);

        //        void handle400(V error);
//
//        void handle401(V error);
//
//        void handle500(V error);
        void handleError(int apiId, int code, V error);

        void handleException(int apiId, Exception e);
    }

    private String getErrorMsg(Exception e) {
        String error = "";
        if (e instanceof SocketTimeoutException) {
            error = "Unable to connect to server.Please check your internet connection.";
        } else if (e instanceof UnknownHostException) {
            error = "Unable to connect to server.Please check your internet connection.";
        } else if (e instanceof IOException) {
            error = "Unable to connect to server.Please check your internet connection.";
        } else if (e instanceof TimeoutException) {
            error = "Unable to connect to server.Please check your internet connection.";
        } else if (e instanceof SocketException) {
            error = "Unable to connect to server.Please check your internet connection.";
        } else {
            error = e.getLocalizedMessage();
        }
        return error;
    }
}
