package com.iamsalih.triviaduello.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.data.model.LeaderBoardItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LeaderboardActivity extends AppCompatActivity implements LeaderboardView {

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.leaderboard_list)
    RecyclerView leaderboardList;

    @BindView(R.id.adView)
    AdView adView;

    private LeaderboardPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);
        presenter = new LeaderboardPresenter(this);
        presenter.getLeaderboardPoints();
        MobileAds.initialize(this, getString(R.string.admob_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leaderboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                presenter.shareItWithAnApplication();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showProgressBar() {
        loadingIndicator.setVisibility(View.VISIBLE);
        leaderboardList.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        loadingIndicator.setVisibility(View.GONE);
        leaderboardList.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public void initRecyclerView(List<LeaderBoardItem> leaderBoardItemList) {
        LeaderBoardAdapter adapter = new LeaderBoardAdapter();
        adapter.setLeaderBoardItems(leaderBoardItemList);
        leaderboardList.setLayoutManager(new LinearLayoutManager(this));
        leaderboardList.setAdapter(adapter);
        Bundle bundle = new Bundle();
        bundle.putString("leaderboard_screen", "results_view");
        FirebaseAnalytics.getInstance(this).logEvent("trivia_duello", bundle);
    }
}
