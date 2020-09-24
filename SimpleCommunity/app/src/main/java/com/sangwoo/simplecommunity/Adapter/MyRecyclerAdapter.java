package com.sangwoo.simplecommunity.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangwoo.simplecommunity.VO.AllBoard;
import com.sangwoo.simplecommunity.R;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    String imgurl;


    private final List<AllBoard> mDataList;

    public interface MyRecyclerViewClickListener {
        void onItemClicked(int position);
    }

    public interface MyRecyclerViewLongClickListener {
        void onItemLongClicked(int position);
    }

    private MyRecyclerViewClickListener mListener;
    private MyRecyclerViewLongClickListener mLongClickListener;

    public void setOnClickListener(MyRecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void setOnLongClickListener(MyRecyclerViewLongClickListener listener) {
        mLongClickListener = listener;
    }

    public MyRecyclerAdapter(List<AllBoard> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        AllBoard item = mDataList.get(position);
        holder.title.setText(item.getTitle());
        holder.user.setText(item.getUser());
        holder.Time.setText(item.getData());
        if(item.getPicture() != null) {
            holder.img.setImageBitmap(item.getPicture());
        }
        Log.d("item.getPicture()", position + "/ " + item.getPicture());


        // 리사이클러뷰 클릭이벤트
        if (mListener != null) {
            final int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(holder.getAdapterPosition());
                }
            });
        }

        // 리사이클러뷰 롱클릭이벤트
        if (mListener != null) {
            final int pos = position;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClicked(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView user;
        ImageView img;
        TextView Time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Com_title);
            user = itemView.findViewById(R.id.Com_user);
            img = itemView.findViewById(R.id.Com_img);
            Time = itemView.findViewById(R.id.Com_Time);
        }
    }



}
