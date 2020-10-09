package com.jogger.beautifulapp.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jogger.beautifulapp.http.gson.GsonConverterFactory;
import com.jogger.beautifulapp.util.Ulog;
import com.jogger.beautifulapp.util.Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;

/**
 * Created by jogger on 2018/5/4.
 * 网络请求基类
 */

abstract class BaseHttpAction {
    IHttpRequest mHttpRequest;


    BaseHttpAction() {
        initRetrofit();
    }

    private void initRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        builder.cache(new Cache(Util.getApp().getCacheDir(), 10 * 1024 * 1024)).connectTimeout
                (10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
        builder.interceptors().add(new ReceivedCookiesInterceptor());
        builder.interceptors().add(new AddCookiesInterceptor());
        builder.interceptors().add(new CacheInterceptor());
        builder.addNetworkInterceptor(new HttpInterceptor());//打印log
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(getBaseUrl())
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        RequestService requestService = retrofit.create(RequestService.class);
        mHttpRequest = getHttpRequest(requestService);
    }

    abstract IHttpRequest getHttpRequest(RequestService requestService);

    abstract String getBaseUrl();

    public static class HttpInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {


            Request request = chain.request();
            HttpUrl httpUrl = request.url()
                    .newBuilder()
//                    .addQueryParameter("IMEI", "864686037902690")
//                    .addQueryParameter("user_id", "1")
                    .build();

            Request build = request.newBuilder()
                    // add common header
                    .url(httpUrl)
                    .build();
            Response response = chain.proceed(build);
//            Ulog.i("url", request.url() + "?" + bodyToString(request.body()));
            String strResponseLog = "请求方式:\n" + request.method() +
                    "\n\n请求url:\n" + request.url() +
                    "\n\n请求参数:\n" + bodyToString(request.body());
            Ulog.writeToFile(strResponseLog);
            return response;
        }
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
