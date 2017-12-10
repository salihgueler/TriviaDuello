package com.iamsalih.triviaduello.leaderboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.data.model.LeaderBoardItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammedsalihguler on 08.12.17.
 */

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder> {

    private List<LeaderBoardItem> leaderBoardItems = new ArrayList<>();

    @Override
    public LeaderBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        return new LeaderBoardViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(LeaderBoardViewHolder holder, int position) {
        holder.bind(leaderBoardItems.get(position));
    }

    @Override
    public int getItemCount() {
        return leaderBoardItems.size();
    }

    public void setLeaderBoardItems(List<LeaderBoardItem> leaderBoardItems) {
        this.leaderBoardItems.clear();
        this.leaderBoardItems.addAll(leaderBoardItems);
        notifyDataSetChanged();
    }

    public class LeaderBoardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.leaderboard_name_text)
        TextView leaderboardName;

        @BindView(R.id.leaderboard_point_text)
        TextView leaderboardPoint;

        public LeaderBoardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(LeaderBoardItem leaderBoardItem) {
            leaderboardName.setText(leaderBoardItem.getUserName());
            leaderboardPoint.setText("" + leaderBoardItem.getPoint());
        }
    }
}
