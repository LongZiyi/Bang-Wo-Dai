package com.example.administrator.myone;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder>{
    private List<Share> mShareList;

    private Context mContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView shareImage;
        TextView shareArticle;

        View shareView;

        public ViewHolder (View view){
            super(view);

            shareView=view;

            shareImage = (ImageView) view.findViewById(R.id.share_image);
            shareArticle = (TextView) view.findViewById(R.id.share_article);

        }
    }

    public ShareAdapter(List<Share> shareList){
        mShareList = shareList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item, parent ,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.shareView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Share share = mShareList.get(position);
                Intent intent = new Intent(mContext,ShareDetailedActivity.class);
                intent.putExtra(ShareDetailedActivity.SHARE_Article,share.getShareArticle());
                intent.putExtra(ShareDetailedActivity.SHARE_IMAGE_ID,share.getShareImageId());
                mContext.startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Share share = mShareList.get(position);
        Glide.with(mContext).load(share.getShareImageId()).into(holder.shareImage);
        holder.shareArticle.setText(share.getShareArticle());
    }


    @Override
    public int getItemCount() {
        return mShareList.size();
    }
}
