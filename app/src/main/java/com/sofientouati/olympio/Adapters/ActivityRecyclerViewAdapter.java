package com.sofientouati.olympio.Adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by sofirntouati on 18/06/17.
 */

public class ActivityRecyclerViewAdapter extends /*RecyclerView.Adapter<ActivityRecyclerViewAdapter.ViewHolder> */
        RealmRecyclerViewAdapter<ActivityObject, ActivityRecyclerViewAdapter.ViewHolder> {
    private List<ActivityObject> list;
    private Context context;
    private int color;
    private ActivityObject activityObject;
    private ValueAnimator coloAnimator;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private Realm realm;


//    public ActivityRecyclerViewAdapter(List<ActivityObject> list, Context context, int color) {
//        this.list = list;
//        this.context = context;
//        this.color = color;
//    }

    public ActivityRecyclerViewAdapter(Context context, OrderedRealmCollection<ActivityObject> data) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        realm = Realm.getDefaultInstance();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ActivityObject obj = getItem(position);
        holder.object = obj;

        holder.date.setText(holder.object.getDate());


        if (Methods.checkSolde()) {
            holder.status.setColorFilter(Color.parseColor(red));
            holder.amount.setTextColor(Color.parseColor(red));
        } else {
            holder.status.setColorFilter(Color.parseColor(blue));
            holder.amount.setTextColor(Color.parseColor(blue));
        }
        switch (holder.object.getAction()) {
            case "depose": {
                holder.name.setText(Methods.getName() + " " + Methods.getLastname());
                holder.action.setText("rechargé");
                holder.source.setText(holder.object.getDestinationNumber());
                holder.amount.setText("+" + String.format("%.2f", holder.object.getAmount()));
                setStatusImage(holder.status, holder.object.getStatus(), position);
                break;
            }
            case "retire": {
                holder.name.setText(Methods.getName() + " " + Methods.getLastname());
                holder.action.setText("retiré");
                holder.source.setText(holder.object.getSourceNumber());
                holder.amount.setText("-" + String.format("%.2f", holder.object.getAmount()));
                setStatusImage(holder.status, holder.object.getStatus(), position);
                break;
            }
            case "envoi": {
                UserObject other;
                if (holder.object.getSourceNumber().equals(Methods.getPhone())) {
                    other = realm.where(UserObject.class).equalTo("phone", holder.object.getDestinationNumber()).findFirst();
                    holder.name.setText(other.getName() + " " + other.getLastname());
                    holder.action.setText("envoyé");
                    holder.source.setText(holder.object.getDestinationNumber());
                    holder.amount.setText("-" + String.format("%.2f", holder.object.getAmount()));
                    setStatusImage(holder.status, holder.object.getStatus(), position);


                } else {
                    other = realm.where(UserObject.class).equalTo("phone", holder.object.getDestinationNumber()).findFirst();
                    holder.name.setText(other.getName() + " " + other.getLastname());
                    holder.action.setText("réçu");
                    holder.source.setText(holder.object.getSourceNumber());
                    holder.amount.setText("+" + String.format("%.2f", holder.object.getAmount()));
                    setStatusImage(holder.status, holder.object.getStatus(), position);


                }

            }


        }
    }

    @Override
    public long getItemId(int index) {
        return super.getItemId(index);
    }

    private void setStatusImage(ImageView image, String status, int position) {


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

    //animations
    private void animateTextView(float initVal, float finalVal, final ImageView imageView) {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(initVal, finalVal);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("onAnimationUpdate: ", String.valueOf(animation.getAnimatedValue()));
                if (Float.valueOf(animation.getAnimatedValue().toString()) <= Methods.getSeuil())
                    animateAppAndStatusBar(Color.parseColor(blue), Color.parseColor(red), imageView);
            }

        });
        valueAnimator.start();
    }

    private void animateAppAndStatusBar(Integer fromColor, final Integer toColor, final ImageView image) {
        coloAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);

        coloAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                image.setColorFilter((Integer) animation.getAnimatedValue());

            }
        });
        coloAnimator.setDuration(200);

        coloAnimator.start();

    }

    private void updateColor() {

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView status;
        TextView source, action, date, amount, name;
        ActivityObject object;

        ViewHolder(View itemView) {
            super(itemView);
            source = (TextView) itemView.findViewById(R.id.number);
            action = (TextView) itemView.findViewById(R.id.action);
            date = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);
            name = (TextView) itemView.findViewById(R.id.name);
            cardView = (CardView) itemView.findViewById(R.id.activityCardView);
            status = (ImageView) itemView.findViewById(R.id.img);


        }
    }

    //97649673

}
