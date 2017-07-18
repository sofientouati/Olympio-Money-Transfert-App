package com.sofientouati.olympio.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.ActivityObject;
import com.sofientouati.olympio.Objects.SharedStrings;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by SOFIENTOUATI on 08/07/17.
 * OLYMPIO APP
 */

public class ConfirmActivityRecyclerViewAdapter extends RealmRecyclerViewAdapter<ActivityObject, ConfirmActivityRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private Realm realm = Realm.getDefaultInstance();
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AlertDialog progress;
    private AlertDialog d;
    private RecyclerView recyclerView;
    private TextView empty;

    public ConfirmActivityRecyclerViewAdapter(Context context,
                                              OrderedRealmCollection<ActivityObject> data,
                                              RecyclerView recyclerView,
                                              TextView empty) {
        super(data, true);
        setHasStableIds(true);
        this.context = context;
        this.recyclerView = recyclerView;
        this.empty = empty;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_confirm_trans, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.object = getItem(i);
        viewHolder.date.setText(viewHolder.object.getDate());
        if (Methods.checkSolde()) {
            viewHolder.amount.setTextColor(Color.parseColor(red));
            Methods.setButtonColor(viewHolder.confirm, Color.parseColor(red));


        } else {
            viewHolder.amount.setTextColor(Color.parseColor(blue));
            Methods.setButtonColor(viewHolder.confirm, Color.parseColor(blue));
        }

        viewHolder.source.setText(viewHolder.object.getSourceNumber());
        viewHolder.amount.setText(String.valueOf(viewHolder.object.getAmount()));

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = Methods.showProgressBar(context, "loading", true);
                performAction(viewHolder.object.getId(), "cancel");
                Log.i("onClick: ", String.valueOf(getData().size()));


            }
        });

        viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = Methods.showProgressBar(context, "loading", true);
                performAction(viewHolder.object.getId(), "done");


            }
        });


    }


    private void performAction(final String id, final String action) {


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.i("execute: ", Methods.getPhone());
                ActivityObject activityObject = realm.where(ActivityObject.class)
                        .equalTo("id", id)
                        .findFirst();
                UserObject user = realm.where(UserObject.class)
                        .equalTo("string", Methods.getPhone())
                        .findFirst();
                UserObject source = realm.where(UserObject.class)
                        .equalTo("string", activityObject.getSourceNumber())
                        .findFirst();


                activityObject.setStatus(action);


                if (action.equals("done")) {
                    Log.i("execute: ", ",");
                    if (user != null) {
                        user.setSolde(Methods.getSolde() + activityObject.getAmount());
                        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedStrings.SHARED_APP_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putFloat(SharedStrings.SHARED_SOLDE, Methods.getSolde() + activityObject.getAmount());
                        editor.commit();
                        Methods.setSolde(Methods.getSolde() + activityObject.getAmount());

                    }
                    if (source != null) {

                        source.setSolde(source.getSolde() - activityObject.getAmount());

                    }

                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (getData().size() < 1) {
                    recyclerView.setVisibility(View.GONE);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }

                Methods.dismissProgressBar(progress);
                d = new AlertDialog.Builder(context)
                        .setTitle("succés")
                        .setMessage("depose d'argent a été envoyé")
                        .setPositiveButton("ok", null)
                        .show();


                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(red));
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(blue));

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                Methods.dismissProgressBar(progress);
                d = new AlertDialog.Builder(context)
                        .setTitle("erreur")
                        .setMessage("erreur serveur" + error.getMessage())
                        .setPositiveButton("ok", null)
                        .show();
                if (Methods.checkSolde())
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(red));
                else
                    d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(blue));
            }
        });
    }


    @Override
    public int getItemCount() {
        return getData().size();

    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        Button cancel, confirm;
        TextView source, action, date, amount;
        ActivityObject object;

        public ViewHolder(View itemView) {
            super(itemView);
            cancel = (Button) itemView.findViewById(R.id.cancel);
            confirm = (Button) itemView.findViewById(R.id.confirm);
            source = (TextView) itemView.findViewById(R.id.number);
            date = (TextView) itemView.findViewById(R.id.date);
            amount = (TextView) itemView.findViewById(R.id.amount);


        }
    }

}
