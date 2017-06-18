package com.sofientouati.mtnapp.Adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofientouati.mtnapp.Objects.ActivityObject;
import com.sofientouati.mtnapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sofirntouati on 18/06/17.
 */

public class ActivityRecyclerViewAdapter extends RecyclerView.Adapter<ActivityRecyclerViewAdapter.ViewHolder> {
    private List<ActivityObject> list;
    private Context context;
    private int color;
    private ActivityObject activityObject;


    public ActivityRecyclerViewAdapter(List<ActivityObject> list, Context context, int color) {
        this.list = list;
        this.context = context;
        this.color = color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_row, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        activityObject = list.get(position);
        holder.action.setText(activityObject.getAction());
        holder.date.setText(activityObject.getDate());
        switch (activityObject.getAction()) {
            case "déposé": {

                holder.source.setText(activityObject.getDestinationNumber());
                holder.amount.setText(String.valueOf(activityObject.getAmount()));
                setStatusImage(holder.imageView, activityObject.getStatus());
                break;
            }
            case "retiré": {
                holder.source.setText(activityObject.getSourceNumber());
                holder.amount.setText(String.valueOf(activityObject.getAmount()));
                setStatusImage(holder.imageView, activityObject.getStatus());
                break;
            }


        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setStatusImage(ImageView image, String status) {
        image.setColorFilter(color);
        switch (status) {
            case "done":
                image.setImageResource(R.drawable.ic_done_white_48dp);
                break;
            case "pending":
                image.setImageResource(R.drawable.ic_access_time_black_48dp);
                break;
            case "cancel":
                image.setImageResource(R.drawable.ic_close_black_48dp);
                break;

        }

    }

    /**
     * Filter Logic
     **/
    public void animateTo(List<ActivityObject> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);

    }

    private void applyAndAnimateRemovals(List<ActivityObject> newModels) {

        for (int i = list.size() - 1; i >= 0; i--) {
            final ActivityObject model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ActivityObject> newModels) {

        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ActivityObject model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ActivityObject> newModels) {

        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ActivityObject model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ActivityObject removeItem(int position) {
        final ActivityObject model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ActivityObject model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ActivityObject model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView source, action, date, amount;

        public ViewHolder(View itemView) {
            super(itemView);
            source = (TextView) itemView.findViewById(R.id.number);
            action = (TextView) itemView.findViewById(R.id.action);
            date = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
            cardView = (CardView) itemView.findViewById(R.id.activityCardView);
            imageView = (ImageView) itemView.findViewById(R.id.img);


        }
    }
}
