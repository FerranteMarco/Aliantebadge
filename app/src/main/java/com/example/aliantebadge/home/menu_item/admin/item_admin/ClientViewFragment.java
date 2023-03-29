package com.example.aliantebadge.home.menu_item.admin.item_admin;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aliantebadge.R;
import com.example.aliantebadge.home.card.CardClientView;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.OtherUser;
import com.example.aliantebadge.upload.UploadData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClientViewFragment extends Fragment {
    private AppDatabase db = null;
    List<OtherUser> users;
    private NavController mNav;
    RecyclerView recyclerViewReservation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDbInstance(requireActivity());
        saveUsers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_client, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNav = Navigation.findNavController(view);
        recyclerViewReservation = view.findViewById(R.id.reservationCardRecyclerView);


    }

    private void saveUsers() {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                UploadData.uploadUsersOnDB(task, requireActivity());

            } else {
                UploadData.dialogError(requireActivity(), getText(R.string.an_error_occurred));
            }
            users = db.otherUserDao().getAllUsers();
            OtherUser current = new OtherUser(db.userDao().getCurrentUser());

            users.remove(current);

            sortUsers();
        });
    }

    private void sortUsers() {
        if (!users.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                users.sort((t1, t2) -> {
                    String cognome1 = t1.secondName;
                    String cognome2 = t2.secondName;
                    return cognome1.compareToIgnoreCase(cognome2);
                });
            }

            CardClientView cardClient = new CardClientView(getContext(), users, getActivity());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerViewReservation.setLayoutManager(linearLayoutManager);
            recyclerViewReservation.setAdapter(cardClient);

        }
    }
}
