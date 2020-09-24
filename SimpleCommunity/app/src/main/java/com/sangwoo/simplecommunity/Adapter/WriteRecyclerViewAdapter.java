package com.sangwoo.simplecommunity.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.VO.WriteItemVO;

import java.util.ArrayList;

public class WriteRecyclerViewAdapter extends RecyclerView.Adapter<WriteRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<WriteItemVO> mDataList;

    public WriteRecyclerViewAdapter(ArrayList<WriteItemVO> mDataList) {
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public WriteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.write_image_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        WriteItemVO item = mDataList.get(position);
        holder.write_image.setImageBitmap(item.getImageView());
        Log.d("이미지", holder.write_image + "");


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView write_image;
        EditText write_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            write_image = itemView.findViewById(R.id.write_image);
            write_text = itemView.findViewById(R.id.write_text);
        }
    }

    }
