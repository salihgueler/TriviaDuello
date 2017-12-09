package com.iamsalih.triviaduello.mainscreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.mainscreen.data.model.QuestionList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by muhammedsalihguler on 25.11.17.
 */

public class MainScreenActivity extends AppCompatActivity implements MainScreenView {


    @BindView(R.id.practice_mode_button)
    Button practiceModeButton;

    @BindView(R.id.duel_mode_button)
    Button duelModeButton;

    @BindView(R.id.loading_indicator)
    ProgressBar loadingIndicator;

    private MainScreenPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_layout);
        ButterKnife.bind(this);
        presenter = new MainScreenPresenter(this);
        presenter.resetJobDispatcher();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.leaderboard_button:
                presenter.startLeaderboardScreen();
                break;
            case R.id.settings_button:
                presenter.startSettingsScreen();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.practice_mode_button)
    public void startPracticeMode() {
        presenter.getQuestions(null, null);
    }

    @OnClick(R.id.duel_mode_button)
    public void startDuelMode() {
        presenter.startDuelProcess();
    }

    @Override
    public void startDuelGame(QuestionList questionList) {
        startGameView(questionList);
    }

    @Override
    public void showProgressBar() {
        practiceModeButton.setVisibility(View.GONE);
        duelModeButton.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        practiceModeButton.setVisibility(View.VISIBLE);
        duelModeButton.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void startGameView(QuestionList questionList) {
        presenter.startGameView(questionList);
    }
}
