package com.example.aliantebadge.home.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aliantebadge.R;
import com.example.aliantebadge.roomDB.Entity.OtherUser;

import java.util.List;

public class CardClientView extends RecyclerView.Adapter{

    Context ctx;
    List<OtherUser> users;
    private final FragmentActivity activity;

    public CardClientView(Context ctx, List<OtherUser> users, FragmentActivity activity){
        this.ctx = ctx;
        this.activity = activity;
        this.users = users;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_view_client, parent, false );

        return new ViewHolderClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass= (ViewHolderClass) holder;
        OtherUser user = users.get(position);

        viewHolderClass.name.setText(user.secondName +" " + user.firstName);
        viewHolderClass.email.setText(user.email);
        viewHolderClass.number.setText(String.valueOf(position+1));

        if(user.versionApp != null)
            viewHolderClass.versionClient.setText(user.versionApp);


    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolderClass extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        TextView email;
        TextView number;
        TextView versionClient;

        public ViewHolderClass(@NonNull View view) {
            super(view);
            this.view = view;

            name = view.findViewById(R.id.nameClient);
            number = view.findViewById(R.id.numberClient);
            email = view.findViewById(R.id.emailClient);
            versionClient = view.findViewById(R.id.versionClient);
        }
    }

}


