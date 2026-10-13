package com.example.coursemanagement.api;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String DEFAULT_BASE_URL = "https://6acdfc7af338359cf1c2ceaf.mockapi.io/api/v1/";
    private static final String PREF_NAME = "course_mgmt_prefs";
    private static final String KEY_BASE_URL = "mock_api_base_url";

    private static Retrofit retrofit = null;
    private static String currentBaseUrl = null;

    public static String getBaseUrl(Context context) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public static void setBaseUrl(Context context, String newUrl) {
        if (!newUrl.endsWith("/")) {
            newUrl += "/";
        }
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_BASE_URL, newUrl).apply();
        retrofit = null; // Reset retrofit instance
    }

    public static ApiService getApiService(Context context) {
        String baseUrl = getBaseUrl(context);
        if (retrofit == null || !baseUrl.equals(currentBaseUrl)) {
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
