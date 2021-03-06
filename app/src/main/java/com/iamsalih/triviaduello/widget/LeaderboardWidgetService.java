package com.iamsalih.triviaduello.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.iamsalih.triviaduello.AppConstants;

/**
 * Created by muhammedsalihguler on 10.12.17.
 */

public class LeaderboardWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LeaderboardWidgetListProvider(getApplicationContext(), intent.getStringExtra(AppConstants.WIDGET_KEY));
    }
}
