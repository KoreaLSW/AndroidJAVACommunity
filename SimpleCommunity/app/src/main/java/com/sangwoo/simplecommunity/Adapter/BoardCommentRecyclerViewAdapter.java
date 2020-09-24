package com.sangwoo.simplecommunity.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangwoo.simplecommunity.VO.BoardCommentVO;
import com.sangwoo.simplecommunity.R;

import java.util.List;

public class BoardCommentRecyclerViewAdapter extends RecyclerView.Adapter<BoardCommentRecyclerViewAdapter.ViewHolder> {

    private final List<BoardCommentVO> mDataList;

    public BoardCommentRecyclerViewAdapter(List<BoardCommentVO> DataList) {
        mDataList = DataList;
    }

    public interface BoardCommentRecyclerLongClickListener {
        void onItemLongClicked(int position);
    }

    private BoardCommentRecyclerLongClickListener mCommentLongClickListener;

    public void setOnLongClickListener(BoardCommentRecyclerLongClickListener listener) {
        mCommentLongClickListener = listener;
    }

    @NonNull
    @Override
    public BoardCommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_comment_recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardCommentRecyclerViewAdapter.ViewHolder holder, int position) {
        BoardCommentVO item = mDataList.get(position);
        holder.comment_user.setText(item.getUser());
        holder.comment_time.setText(item.getData());
        holder.comment_contents.setText(item.getContent());
        Log.d("CommentSelectCheck",item.getUser() + " " + item.getData() + " " + item.getContent());
        Log.d("CommentSelectCheck","123123123123123");

        // 리사이클러뷰 롱클릭이벤트
        if (mDataList != null) {
            final int pos = position;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    mCommentLongClickListener.onItemLongClicked(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("CommentSelectCheck",mDataList.size()+"");
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView comment_user;
        TextView comment_time;
        TextView comment_contents;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment_user = itemView.findViewById(R.id.comment_user);
            comment_time = itemView.findViewById(R.id.comment_time);
            comment_contents = itemView.findViewById(R.id.comment_contents);
        }
    }

}
