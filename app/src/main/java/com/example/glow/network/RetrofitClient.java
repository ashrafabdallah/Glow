package com.example.glow.network;

import com.app.torch.utils.interceptors.CurlLoggingInterceptor;
import com.example.torch.intersectors.ResponseLoggingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit getRetrofitInstance() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//
//        okHttpClient.newBuilder().addInterceptor(new CurlLoggingInterceptor())
//                .addInterceptor(new ResponseLoggingInterceptor());
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(new CurlLoggingInterceptor()).addInterceptor(new ResponseLoggingInterceptor());
        //String u = "127.0.0.1:8000";
        //String emulator_ = "10.0.2.2:80";
        String url = "http://192.168.1.9/blog/public/api/v1/customer/";
        //String url = "http://powerful-temple-03256.herokuapp.com/api/v1/customer/";
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(url).addConverterFactory(GsonConverterFactory.create());
        builder.client(okHttpClient.build());
        return builder.build();
    }
}
