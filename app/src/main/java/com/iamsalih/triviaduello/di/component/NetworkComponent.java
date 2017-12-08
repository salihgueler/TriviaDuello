package com.iamsalih.triviaduello.di.component;

import com.iamsalih.triviaduello.di.module.FirebaseModule;
import com.iamsalih.triviaduello.di.module.NetworkModule;
import com.iamsalih.triviaduello.mainscreen.MainScreenPresenter;

import dagger.Component;

/**
 * Created by muhammedsalihguler on 04.12.17.
 */

@Component(modules = {NetworkModule.class, FirebaseModule.class})
public interface NetworkComponent {
    void inject(MainScreenPresenter mainScreenPresenter);
}
