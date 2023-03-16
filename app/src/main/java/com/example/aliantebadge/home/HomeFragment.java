package com.example.aliantebadge.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.R;
import com.example.aliantebadge.Variable;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private AppDatabase db = null;
    private NavController mNav;
    Button prenota;
    Date date = new Date();
    Date date2 = new Date();
    FirebaseFirestore mFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDbInstance(requireActivity());
        mFirestore = FirebaseFirestore.getInstance();


        if(Variable.isaBoolean(db.userDao().getCurrentUser().uid))
            Variable.setAdmin(true);
        else {
            SharedPreferences preferences = requireActivity().getSharedPreferences("GR_Barber", Context.MODE_PRIVATE);
            Variable.setAdmin(preferences.getBoolean("enableAdmin", true));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));

        Locale myLocale = new Locale("it","IT");
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.setLocale(myLocale);
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        date = new Date();
        date2 = new Date();

        mNav = Navigation.findNavController(view);


        TextView username;


        prenota = view.findViewById(R.id.buttonPrenota);
        username = view.findViewById(R.id.fragmentHomeUsername);

        TextView versionToUp = view.findViewById(R.id.versionToUp);
        if(Variable.isVersionToUpdate()){
            ConstraintLayout bannerHome = view.findViewById(R.id.bannerHome);
            bannerHome.setVisibility(View.GONE);
            LinearLayout bodyHome = view.findViewById(R.id.bodyHome);
            bodyHome.setVisibility(View.GONE);
            //prenota.setVisibility(View.GONE);
            versionToUp.setVisibility(View.VISIBLE);
            versionToUp.setTextColor(requireActivity().getColor(R.color.red_wrong));
            //calendarView.setVisibility(View.GONE);
        }
        else
            versionToUp.setVisibility(View.GONE);




        prenota.setOnClickListener(view12 -> {

        });

       if(db.userDao().getCurrentUser() != null)
            username.setText(db.userDao().getCurrentUser().firstName);
    }


    private void updateUser() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        if(mAuth.getCurrentUser() != null) {
            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


            mFirestore.collection("Users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> documentData = document.getData();

                        db.userDao().deleteAll();

                        User user = new User();
                        user.uid = uid;

                        assert documentData != null;

                        user.email = (String) documentData.get("email");
                        user.firstName = (String) documentData.get("first_name");
                        user.secondName = (String) documentData.get("second_name");


                        db.userDao().insertUser(user);
                    }
                }

            });
        }
    }





}