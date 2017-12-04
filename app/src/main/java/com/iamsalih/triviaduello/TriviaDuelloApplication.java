package com.iamsalih.triviaduello;

import android.app.Application;

import com.iamsalih.triviaduello.di.component.DaggerNetworkComponent;
import com.iamsalih.triviaduello.di.component.NetworkComponent;
import com.iamsalih.triviaduello.di.module.NetworkModule;

import dagger.Component;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public class TriviaDuelloApplication extends Application {

    public static NetworkComponent component;

    @Override
    public void onCreate() {
        initDagger();
        super.onCreate();
    }

    private void initDagger() {
        component = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule())
                .build();
    }
}
