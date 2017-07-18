package com.sofientouati.olympio.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.CodealerObject;
import com.sofientouati.olympio.Objects.PhonesObject;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 10/07/17.
 */

public class CoDealersRecyclerAdapter extends RealmRecyclerViewAdapter<CodealerObject, CoDealersRecyclerAdapter.ViewHolder>
        implements Filterable {

    private Context context;
    private RecyclerView recyclerView;
    private TextView empty;
    private String type;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private Realm realm;

    public CoDealersRecyclerAdapter(Context context,
                                    @Nullable OrderedRealmCollection<CodealerObject> data,
                                    RecyclerView recyclerView,
                                    TextView empty,
                                    String type) {
        super(data, true);
        this.context = context;
        this.recyclerView = recyclerView;
        this.empty = empty;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_codealer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        realm = Realm.getDefaultInstance();
        holder.object = getItem(holder.getAdapterPosition());

        final UserObject sender;
        if (Methods.checkSolde()) {
            Methods.setButtonColor(holder.confirm, Color.parseColor(red));
        } else {
            Methods.setButtonColor(holder.confirm, Color.parseColor(blue));
        }
        switch (type) {

            case "matched": {
                holder.confirm.setVisibility(View.GONE);
                if (holder.object.getSender().equals(Methods.getPhone())) {
                    sender = realm.where(UserObject.class)
                            .equalTo("phone", holder.object.getReceiver())
                            .findFirst();
                    holder.name.setText(sender.getName() + " " + sender.getLastname());
                    holder.number.setText(holder.object.getReceiver());
                } else {
                    sender = realm.where(UserObject.class)
                            .equalTo("phone", holder.object.getSender())
                            .findFirst();
                    holder.name.setText(sender.getName() + " " + sender.getLastname());
                    holder.number.setText(holder.object.getSender());

                }
                break;
            }
            case "requests": {
                sender = realm.where(UserObject.class)
                        .equalTo("phone", holder.object.getSender())
                        .findFirst();
                holder.name.setText(sender.getName() + " " + sender.getLastname());
                holder.number.setText(holder.object.getSender());
                holder.confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        performAction(holder.object, holder.getAdapterPosition());
                    }
                });
                break;
            }
        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.beginTransaction();
                UserObject sender = realm.where(UserObject.class)
                        .equalTo("phone", holder.object.getSender())
                        .findFirst();
                UserObject receiver = realm.where(UserObject.class)
                        .equalTo("phone", holder.object.getReceiver())
                        .findFirst();
                PhonesObject
                        s = realm.where(PhonesObject.class)
                        .equalTo("phone", holder.object.getSender())
                        .findFirst(),
                        r = realm.where(PhonesObject.class)
                                .equalTo("phone", holder.object.getReceiver())
                                .findFirst();
                Log.i("onClick: sender sent ", sender.getSent().toString());
                Log.i("onClick: reciever Rec", receiver.getReceived().toString());
                Log.i("onClick: sender matched", sender.getMatched().toString());
                Log.i("onClick: receiver matched", receiver.getMatched().toString());
                switch (type) {
                    case "requests": {
                        sender.getSent().remove(r);
                        receiver.getReceived().remove(s);
                        Log.i("onClick: ", String.valueOf(sender.getReceived().indexOf(r)));
                        Log.i("onClick: ", String.valueOf(receiver.getSent().indexOf(s)));
                        break;


                    }
                    case "matched": {

                        Log.i("sender:", sender.getMatched().toString());
                        Log.i("receiver:", receiver.getMatched().toString());

                        sender.getMatched().remove(r);
                        receiver.getMatched().remove(s);

                        Log.i("onClick: ", String.valueOf(sender.getMatched().indexOf(r)));
                        Log.i("onClick: ", String.valueOf(receiver.getMatched().indexOf(s)));
                        break;
                    }
                }
                holder.object.deleteFromRealm();

                Log.i("onClick: sender sent  ", sender.getSent().toString());
                Log.i("onClick: reciever Rec", receiver.getReceived().toString());
                Log.i("onClick: sender matched", sender.getMatched().toString());
                Log.i("onClick: receiver matched", receiver.getMatched().toString());


                realm.commitTransaction();
                notifyItemRemoved(holder.getAdapterPosition());

                if (getData().isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return getData().size();
    }


    private void performAction(final CodealerObject object, int postion) {
        realm.beginTransaction();
        UserObject sender = realm.where(UserObject.class)
                .equalTo("phone", object.getSender())
                .findFirst();
        UserObject receiver = realm.where(UserObject.class)
                .equalTo("phone", object.getReceiver())
                .findFirst();
        PhonesObject
                s = realm.where(PhonesObject.class)
                .equalTo("phone", object.getSender())
                .findFirst(),
                r = realm.where(PhonesObject.class)
                        .equalTo("phone", object.getReceiver())
                        .findFirst();
        Log.i("performAction: before receiver", object.getReceiver());
        Log.i("performAction: befor sender", sender.toString());
        Log.i("performAction: befor", receiver.toString());
        Log.i("performAction: ", s.toString());
        Log.i("performAction: ", r.toString());
        sender.getSent().remove(r);
        sender.getMatched().add(r);
        receiver.getReceived().remove(s);
        receiver.getMatched().add(s);
        Log.i("performAction: ", sender.getSent().toString());
        Log.i("performAction: ", sender.getMatched().toString());
        Log.i("performAction: ", receiver.getReceived().toString());
        Log.i("performAction: ", receiver.getMatched().toString());
        object.setStatus("done");
        realm.commitTransaction();
        notifyItemRemoved(postion);
        if (getData().isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public Filter getFilter() {
        CodealerFilter filter = new CodealerFilter(this);
        return filter;
    }


    public void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();
        if (text == null || "".equals(text)) {
            updateData(realm.where(CodealerObject.class).findAll());
        } else {
            updateData(realm.where(CodealerObject.class)
                    .contains("phone", text, Case.INSENSITIVE)
                    .or()
                    .contains("name", text, Case.INSENSITIVE)
                    .or()
                    .contains("lastName", text, Case.INSENSITIVE)
                    .findAll());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout confirm2;
        TextView name, number;
        CodealerObject object;
        Button confirm, cancel;


        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            confirm = (Button) itemView.findViewById(R.id.confirm);
            cancel = (Button) itemView.findViewById(R.id.cancel);
            confirm2 = (LinearLayout) itemView.findViewById(R.id.confirm2);

        }
    }

    private class CodealerFilter extends Filter {
        private final CoDealersRecyclerAdapter adapter;

        private CodealerFilter(CoDealersRecyclerAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }

    }
}
