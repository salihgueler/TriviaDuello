package com.iamsalih.triviaduello;

import android.app.Application;

import com.iamsalih.triviaduello.di.component.DaggerFirebaseComponent;
import com.iamsalih.triviaduello.di.component.DaggerNetworkComponent;
import com.iamsalih.triviaduello.di.component.FirebaseComponent;
import com.iamsalih.triviaduello.di.component.NetworkComponent;
import com.iamsalih.triviaduello.di.module.FirebaseModule;
import com.iamsalih.triviaduello.di.module.NetworkModule;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

public class TriviaDuelloApplication extends Application {

    public static NetworkComponent networkComponent;
    public static FirebaseComponent firebaseComponent;

    @Override
    public void onCreate() {
        initDagger();
        super.onCreate();
    }

    private void initDagger() {
        networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule())
                .build();

        firebaseComponent = DaggerFirebaseComponent.builder()
                .firebaseModule(new FirebaseModule())
                .build();
    }
}
