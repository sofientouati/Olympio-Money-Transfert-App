package com.sofientouati.olympio.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sofientouati.olympio.Methods;
import com.sofientouati.olympio.Objects.CodealerObject;
import com.sofientouati.olympio.Objects.PhonesObject;
import com.sofientouati.olympio.Objects.UserObject;
import com.sofientouati.olympio.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;

/**
 * OLYMPIO
 * Created by SOFIEN TOUATI on 11/07/17.
 */

public class AddCodealersRecyclerAdapter extends RealmRecyclerViewAdapter<UserObject, AddCodealersRecyclerAdapter.ViewHolder> implements Filterable {
    private Context context;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private TextView empty;
    private ArrayList<String> oldList;
    private String
            red = "#C62828",
            yellow = "#F9A825",
            blue = "#0072ff";
    private Realm realm;

    public AddCodealersRecyclerAdapter(Context context, @Nullable OrderedRealmCollection<UserObject> data, RecyclerView recyclerView, TextView empty, SearchView searchView) {
        super(data, true);
        this.context = context;
        realm = Realm.getDefaultInstance();
        this.recyclerView = recyclerView;
        this.empty = empty;
        this.searchView = searchView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_codealer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        realm = Realm.getDefaultInstance();
        if (Methods.checkSolde()) {
            Methods.setButtonColor(holder.confirm, Color.parseColor(red));
        } else {
            Methods.setButtonColor(holder.confirm, Color.parseColor(blue));
        }


        holder.object = getItem(holder.getAdapterPosition());
        holder.name.setText(holder.object.getName() + " " + holder.object.getLastname());
        holder.number.setText(holder.object.getPhone());
        holder.cancel.setVisibility(View.GONE);
        holder.confirm.setText("ajouter");
        holder.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                performAction(holder.object.getPhone());

            }
        });
    }

    private void performAction(final String phone) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                PhonesObject sphone = realm.where(PhonesObject.class)
                        .equalTo("phone", Methods.getPhone())
                        .findFirst();

                PhonesObject rphone = realm.where(PhonesObject.class)
                        .equalTo("phone", phone)
                        .findFirst();


                UserObject sent = realm.where(UserObject.class)
                        .equalTo("phone", Methods.getPhone())
                        .findFirst();
                UserObject received = realm.where(UserObject.class)
                        .equalTo("phone", phone)
                        .findFirst();

                sent.getSent().add(rphone);
                received.getReceived().add(sphone);

                CodealerObject codealerObject = realm.createObject(CodealerObject.class, UUID.randomUUID().toString());
                codealerObject.setSender(Methods.getPhone());
                codealerObject.setReceiver(phone);
                codealerObject.setStatus("pending");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                filterResults("");
                searchView.setQuery("", false);
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Methods.showSnackBar(recyclerView, error.getMessage());
                error.printStackTrace();
            }
        });
    }

    @Override
    public Filter getFilter() {
        AddCodealersRecyclerAdapter.CodealerFilter filter = new AddCodealersRecyclerAdapter.CodealerFilter(this);
        return filter;
    }


    public void filterResults(String text) {

        UserObject user = realm.where(UserObject.class)
                .equalTo("phone", Methods.getPhone())
                .findFirst();


        ArrayList<PhonesObject> phonesList = new ArrayList<>();
        phonesList.addAll(user.getSent());
        phonesList.addAll(user.getReceived());
        phonesList.addAll(user.getMatched());
        String[] phone = new String[phonesList.size()];
        int index = 0;
        for (PhonesObject s : phonesList
                ) {
            phone[index] = s.getPhone();
            index++;
        }
        Log.i("filterResults: ", user.getSent().toString());
        Log.i("filterResults: ", user.getReceived().toString());
        Log.i("filterResults: ", user.getMatched().toString());


        RealmQuery<UserObject> realmQuery = realm.where(UserObject.class);
        realmQuery.notEqualTo("phone", Methods.getPhone());
        realmQuery.contains("phone", text);
        Log.i("filterResults: ", Arrays.toString(phone));
        if (phone.length != 0) {
            realmQuery.not();
            realmQuery.in("phone", phone);
        }


        text = text == null ? null : text.toLowerCase().trim();
        recyclerView.setVisibility(View.VISIBLE);


        if (text == null || text.isEmpty()) {
            updateData(null);
            empty.setVisibility(View.VISIBLE);
            empty.setText("entrer un numéro de télephone");
            recyclerView.setVisibility(View.GONE);

        } else {
            updateData(realmQuery.findAll());

            if (getData().isEmpty()) {

                empty.setVisibility(View.VISIBLE);
                empty.setText("entrer un numéro de télephone");
                recyclerView.setVisibility(View.GONE);

            }
            if (getData() == null || getData().size() == 0) {
                empty.setVisibility(View.VISIBLE);
                empty.setText("numero n'existe pas");
                recyclerView.setVisibility(View.INVISIBLE);
                return;
            }

            if (text.length() != 8) {
                empty.setVisibility(View.VISIBLE);
                empty.setText(getData().size() + " codealers trouvés");
                if (getData().size() == 1) {
                    empty.setText(getData().size() + " codealer trouvé");
                }
                recyclerView.setVisibility(View.INVISIBLE);
                return;
            }


            empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkExists(String sender, String receiver) {
        CodealerObject sent = realm.where(CodealerObject.class)
                .equalTo("sender", sender)
                .equalTo("receiver", receiver)
                .findFirst();
        return sent == null;
    }

    private void parseArray(RealmList<UserObject> list) {

    }

    private String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C = new String[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        UserObject object;
        Button confirm, cancel;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            confirm = (Button) itemView.findViewById(R.id.confirm);
            cancel = (Button) itemView.findViewById(R.id.cancel);
//            confirm2 = (LinearLayout) itemView.findViewById(R.id.confirm2);

        }

    }

    private class CodealerFilter extends Filter {
        private final AddCodealersRecyclerAdapter adapter;

        private CodealerFilter(AddCodealersRecyclerAdapter adapter) {
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
