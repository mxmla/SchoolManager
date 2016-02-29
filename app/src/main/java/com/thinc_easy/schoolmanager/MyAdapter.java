package com.thinc_easy.schoolmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * Created by M on 09.06.2015.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private LayoutInflater inflater;
    List<Information> data = Collections.emptyList();
    private Context context;
    private String listType;

    public MyAdapter(Context context, List<Information> data, String listType){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.listType = listType;
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(listType.equals("drawerList")) {
            view = inflater.inflate(R.layout.drawer_row, parent, false);
        } else if (listType.equals("1LineAvatarAndText")){
            view = inflater.inflate(R.layout.subjects_list_row, parent, false);
        } else {
            view = inflater.inflate(R.layout.drawer_row, parent, false);
        }
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current = data.get(position);
        holder.title.setText(current.title);
        // TODO Typeface-Wahl auslagern in callende Activity
        if(listType.equals("drawerList")) {
            holder.title.setTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Medium.ttf"));
        } else if(listType.equals("1LineAvatarAndText")){
            holder.title.setTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Regular.ttf"));
        } else {
            holder.title.setTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Regular.ttf"));
        }

        //holder.icon.setImageResource(current.iconId);
        holder.icon.setBackgroundDrawable(current.icon);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }
    }
}
