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
import com.example.aliantebadge.home.card.CardAllEmployee;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.OtherUser;
import com.example.aliantebadge.upload.UploadData;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class EmployeeViewFragment extends Fragment {
    private AppDatabase db = null;
    List<OtherUser> users;
    private NavController mNav;
    RecyclerView recyclerViewEmployee;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDbInstance(requireActivity());
        saveUsers();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_employee, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNav = Navigation.findNavController(view);
        recyclerViewEmployee = view.findViewById(R.id.allEmployeeCardRecyclerView);


    }

    private void saveUsers() {

        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                UploadData.uploadUsersOnDB(task, requireActivity());

            } else {
                UploadData.dialogError(requireActivity(), getText(R.string.an_error_occurred));
            }
            db.otherUserDao().deleteByUserId(db.userDao().getCurrentUser().uid);
            users = db.otherUserDao().getAllUsers();

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

            CardAllEmployee cardClient = new CardAllEmployee(getContext(), users, getActivity());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerViewEmployee.setLayoutManager(linearLayoutManager);
            recyclerViewEmployee.setAdapter(cardClient);


        }
    }
}
