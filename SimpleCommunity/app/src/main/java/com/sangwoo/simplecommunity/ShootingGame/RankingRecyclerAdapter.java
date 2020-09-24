package com.sangwoo.simplecommunity.ShootingGame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangwoo.simplecommunity.R;

import java.util.List;

public class RankingRecyclerAdapter extends RecyclerView.Adapter<RankingRecyclerAdapter.ViewHolder> {

    private final List<RankingVO> mDataList;

    public RankingRecyclerAdapter(List<RankingVO> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public RankingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingRecyclerAdapter.ViewHolder holder, int position) {
        RankingVO item = mDataList.get(position);
        int i = position;
        holder.ranking_text.setText(i + 1 + "위");
        holder.user.setText(item.getUser());
        holder.score.setText(item.getScore());
        holder.Time.setText(item.getData());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView score;
        TextView Time;
        TextView ranking_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.ranking_user);
            score = itemView.findViewById(R.id.ranking_score);
            Time = itemView.findViewById(R.id.ranking_Time);
            ranking_text = itemView.findViewById(R.id.ranking_text);
        }
    }
}
