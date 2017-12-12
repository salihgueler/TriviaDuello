package com.iamsalih.triviaduello.onboarding.login;

import com.facebook.CallbackManager;
import com.iamsalih.triviaduello.BaseView;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public interface LoginView extends BaseView {

    CallbackManager getCallbackManager();
    void showLoginView();
    void hideLoginView();
}
