package com.iamsalih.triviaduello.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.leaderboard.data.model.LeaderBoardItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LeaderboardActivity extends AppCompatActivity implements LeaderboardView {

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.share_button_holder)
    LinearLayout shareButtonHolder;

    @BindView(R.id.leaderboard_list)
    RecyclerView leaderboardList;

    private LeaderboardPresenter presenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);
        presenter = new LeaderboardPresenter(this);
        presenter.getLeaderboardPoints();
    }

    @Override
    public void showProgressBar() {
        loadingIndicator.setVisibility(View.VISIBLE);
        shareButtonHolder.setVisibility(View.GONE);
        leaderboardList.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        loadingIndicator.setVisibility(View.GONE);
        shareButtonHolder.setVisibility(View.VISIBLE);
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
    }
}
