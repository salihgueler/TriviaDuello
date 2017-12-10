package com.iamsalih.triviaduello.di.module;

import android.support.annotation.NonNull;

import com.iamsalih.triviaduello.api.TriviaCall;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

@Module
public class NetworkModule {

    private static String BASE_URL = "https://opentdb.com/";

    @Provides
    @NonNull
    Retrofit provideRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    @Provides
    @NonNull
    TriviaCall providesApiService(@NonNull Retrofit retrofit) {
        return retrofit.create(TriviaCall.class);
    }
}
