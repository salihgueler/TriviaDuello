package com.iamsalih.triviaduello.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.iamsalih.triviaduello.R;

/**
 * Created by muhammedsalihguler on 10.12.17.
 */

public class LeaderboardWidgetListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private String textForWidget;

    public LeaderboardWidgetListProvider(Context applicationContext, String stringExtra) {
        this.context = applicationContext;
        this.textForWidget = stringExtra;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_row);

        row.setTextViewText(R.id.leaderboard_text_for_widget, textForWidget);

        return row;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return (long)i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}