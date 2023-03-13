package com.example.aliantebadge.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.grbarber.BuildConfig;
import com.example.grbarber.R;
import com.example.grbarber.Variable;
import com.example.grbarber.roomDB.AppDatabase;
import com.example.grbarber.roomDB.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;
import java.util.Objects;

public class Splash extends AppCompatActivity {

    SharedPreferences prefs = null;
    BroadcastReceiver broadcastReceiver;
    private AppDatabase db = null;
    View progressBar;
    FirebaseFirestore mFirestore;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        progressBar = findViewById(R.id.include);

        prefs = getSharedPreferences("gr_theme", MODE_PRIVATE);
        int theme = prefs.getInt("theme", -1);

        switch (theme){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        Variable.setVersionIsToUpdate(false);

        // per bypassare l'esposizione dell'uri
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        TextView creator = findViewById(R.id.creator);
        creator.setText("App creata da Marco Ferrante\nVersione " + BuildConfig.VERSION_NAME);

        db = AppDatabase.getDbInstance(this);
        mFirestore = FirebaseFirestore.getInstance();

        MotionLayout motionLayout = findViewById(R.id.motionLayout);
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Bundle extras = intent.getExtras();
                        NetworkInfo info = (NetworkInfo) extras.getParcelable("networkInfo");
                        NetworkInfo.State state = info.getState();

                        if (state == NetworkInfo.State.CONNECTED) {
                            //quando il dispositivo sarà connesso alla rete,
                            //scarica tutti i dati che servono
                            updateLocalDB();
                        }
                    }
                };

                //receiver che si mette in ascolto della presenza di connessione di rete
                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                registerReceiver((BroadcastReceiver) broadcastReceiver, intentFilter);

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                 progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

    }



    /**
     * cancello dati locali per caricare i dati aggiornati
     */
    private void updateLocalDB() {

        db.userDao().deleteAll();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
            updateUser();
        else{
            unregisterReceiver(broadcastReceiver);

            //altrimenti procedo con la normale esecuzione e mostro la pagina di login
            startActivity(new Intent(Splash.this, com.example.grbarber.login.LoginActivity.class));

            //chiudo l'activity in modo da non mostrare lo splash quando l'utente clicca il tasto indietro
            finish();
        }
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


                        User user = new User();
                        user.uid = uid;

                        assert documentData != null;

                        user.email = (String) documentData.get("email");
                        user.firstName = (String) documentData.get("first_name");
                        user.secondName = (String) documentData.get("second_name");

                        assert documentData.get("accepted") != null;
                        user.accepted = (boolean) documentData.get("accepted");
                        db.userDao().deleteAll();

                        db.userDao().insertUser(user);
                    }
                    checkLastVersionAvailable();

                }

            });
        }
    }



    private void checkLastVersionAvailable() {

        mFirestore.collection("Version").whereNotEqualTo("newVerionApp",BuildConfig.VERSION_NAME)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task1.getResult())) {

                            Map<String, Object> documentData = document.getData();
                            String actualVersion = (String) documentData.get("newVerionApp");
                            boolean availableUpdate = actualVersion.compareTo(BuildConfig.VERSION_NAME) > 0;
                            System.out.println("App da aggiornare ?? " + availableUpdate);

                            if (document.exists()) {
                                Variable.setLastVersionAvailable(availableUpdate);
                            }

                        }

                    } else {
                        Log.d(null, "Error getting documents: ", task1.getException());
                    }


                });

        mFirestore.collection("Version").whereGreaterThan("minVersionApp",BuildConfig.VERSION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                            Map<String, Object> documentData = document.getData();
                            String minV =(String)documentData.get("minVersionApp");
                            boolean toUpdate = minV.compareTo(BuildConfig.VERSION_NAME) > 0;

                            if (document.exists()) {
                                Variable.setVersionIsToUpdate(toUpdate);

                            }

                        }

                    } else {
                        Log.d(null, "Error getting documents: ", task.getException());
                    }


                    System.out.println("App da aggiornare ? " + Variable.isLastVersionAvailable());

                    unregisterReceiver(broadcastReceiver);

                    //altrimenti procedo con la normale esecuzione e mostro la pagina di login
                    startActivity(new Intent(Splash.this, com.example.grbarber.login.LoginActivity.class));

                    //chiudo l'activity in modo da non mostrare lo splash quando l'utente clicca il tasto indietro
                    finish();

                });



    }




}

