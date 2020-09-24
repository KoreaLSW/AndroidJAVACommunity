package com.sangwoo.simplecommunity.Adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangwoo.simplecommunity.VO.BoardRotatedExample;
import com.sangwoo.simplecommunity.R;

import java.util.List;

public class BoardExampleRecyclerViewAdapter extends RecyclerView.Adapter<BoardExampleRecyclerViewAdapter.ViewHolder> {

    private final List<BoardRotatedExample> mDataList;
    Bitmap bm;

    public interface BoardExampleRecyclerViewClickListener {
        void onItemClicked(int position);
    }

    private BoardExampleRecyclerViewClickListener clickListener;

    public BoardExampleRecyclerViewAdapter(List<BoardRotatedExample> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.write_image_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("mDataList.size()", mDataList.size()+"");
        BoardRotatedExample item = mDataList.get(position);
            if (item.getPicture() != null) {
                holder.img.setImageBitmap(item.getPicture());
            }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.write_image);
        }
    }
}
