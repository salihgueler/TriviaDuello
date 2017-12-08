package com.iamsalih.triviaduello.leaderboard;

import com.iamsalih.triviaduello.BaseView;
import com.iamsalih.triviaduello.leaderboard.data.model.LeaderBoardItem;

import java.util.List;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public interface LeaderboardView extends BaseView {

    void initRecyclerView(List<LeaderBoardItem> leaderBoardItemList);
}
