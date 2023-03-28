package com.example.aliantebadge.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.aliantebadge.PositionHelper;
import com.example.aliantebadge.R;
import com.example.aliantebadge.Variable;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.Badge;
import com.example.aliantebadge.roomDB.Entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private AppDatabase db = null;

    long minuts_between_misurations = 1;
    NotificationCompat.Builder builder;
    String id;
    Timestamp timestamp;
    Date date = new Date();
    Date date2 = new Date();
    FirebaseFirestore mFirestore;
    Button timbra;
    LocationListener listener;
    LocationManager locationManager;
    Location lastKnownLocation;
    int buttonConta = 0;
    Badge badge1;
    Handler handler;
    final Runnable r = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        public void run() {
            checkLocationPermissionsEnabledThenAttachListener();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = AppDatabase.getDbInstance(requireActivity());
        mFirestore = FirebaseFirestore.getInstance();

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        if(Variable.isaBoolean(db.userDao().getCurrentUser().uid))
            Variable.setAdmin(true);
        else {
            SharedPreferences preferences = requireActivity().getSharedPreferences("Aliante", Context.MODE_PRIVATE);
            Variable.setAdmin(preferences.getBoolean("enableAdmin", true));
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
                lastKnownLocation = PositionHelper.getLastKnownLocation(locationManager);
                //positionChanged();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                System.out.println("GPS stato cambiato");
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                /*if(lastKnownLocation != null) {
                    textViewNearest.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_round_location_disabled_24, 0);
                    textViewNearestWarningPositionDisabled.setVisibility(View.VISIBLE);
                }
                if(lastKnownLocation == null) {
                    noLocationStateItineraryNear.setVisibility(View.VISIBLE);
                    textViewSeeAllNearest.setVisibility(View.GONE);
                }*/
            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creazione del canale di notifica
            String channelId = "my_channel_id";
            CharSequence channelName = "My Channel";
            String channelDescription = "My channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            // Impostazione delle opzioni del canale
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), attributes);
            channel.enableVibration(true);
            channel.setLightColor(Color.RED);

            // Registrazione del canale nel sistema di notifica
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(requireActivity(), "my_channel_id")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Attenzione al GPS")
                .setContentText("Sembra che il tuo GPS non sia attivo. attivalo per la rilevazione della posizione")
                .setPriority(NotificationCompat.PRIORITY_HIGH);


// Invio della notifica

        checkLocationPermissionsEnabled();

        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Rome"));

        Locale myLocale = new Locale("it","IT");
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.setLocale(myLocale);
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        date = new Date();
        date2 = new Date();

        //mNav = Navigation.findNavController(view);
        timbra = view.findViewById(R.id.buttonTimbra);


        TextView username;

        username = view.findViewById(R.id.fragmentHomeUsername);

        TextView versionToUp = view.findViewById(R.id.versionToUp);
        if(Variable.isVersionToUpdate()){
            ConstraintLayout bannerHome = view.findViewById(R.id.bannerHome);
            bannerHome.setVisibility(View.GONE);
            LinearLayout bodyHome = view.findViewById(R.id.bodyHome);
            bodyHome.setVisibility(View.GONE);
            //timbra.setVisibility(View.GONE);
            versionToUp.setVisibility(View.VISIBLE);
            versionToUp.setTextColor(requireActivity().getColor(R.color.red_wrong));
            //calendarView.setVisibility(View.GONE);
        }
        else
            versionToUp.setVisibility(View.GONE);


        handler = new Handler();

        timbra.setOnClickListener(view12 -> {

            //todo aggiungere abilitazione automatica gps

            if((buttonConta % 2) == 0) {

                checkLocationPermissionsEnabledThenSaveOnDB();
            }
            else {
                timbra.setText("Timbra inizio");
                buttonConta ++;

                handler.removeCallbacks(r);
                mFirestore.collection("Badges").document(badge1.id_badge).update("fine",lastKnownLocation.getTime());

                //todo fine registrazione
            }

        });

       if(db.userDao().getCurrentUser() != null)
            username.setText(db.userDao().getCurrentUser().firstName);
    }

    private void registerBadge() {

        timestamp = new Timestamp(System.currentTimeMillis());
        id = Long.toString(timestamp.getTime());
        User currentUser = db.userDao().getCurrentUser();

        badge1 = new Badge();
        badge1.id_badge = id;
        badge1.inizio = timestamp.getTime();
        badge1.fine = Long.valueOf(0);
        badge1.operator_id = currentUser.uid;
        badge1.operator_name = currentUser.firstName + " " + currentUser.secondName;
        badge1.phoneModel = getDeviceName();

        Map<String, Object> badge = new HashMap<>();

        if (lastKnownLocation != null) {
            //badge1.id_badge = String.valueOf(lastKnownLocation.getTime());
            badge1.inizio = lastKnownLocation.getTime();
            Map<String, Double> position = new HashMap<>();
            badge1.geo_location = new ArrayList<>();
            position.put("latidude", lastKnownLocation.getLatitude());
            position.put("longitude", lastKnownLocation.getLongitude());
            badge1.geo_location.add(position);
            badge.put("geo_location", badge1.geo_location);
        }

        System.out.println("badge " + timestamp.getTime());

        badge.put("inizio", badge1.inizio);
        badge.put("fine", badge1.fine);
        badge.put("operator_id",badge1.operator_id);
        badge.put("operator_name", badge1.operator_name);
        badge.put("phoneModel", badge1.phoneModel);


        mFirestore.collection("Badges").document(badge1.id_badge)
                .set(badge)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onSuccess(Void aVoid) {

                       db.badgeDao().insertBadge(badge1);
                       if (locationManager.isLocationEnabled()) {
                           locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

                           timbra.setText("Timbra fine");

                           buttonConta ++;

                           handler.postDelayed(r, minuts_between_misurations * 1000 * 60);
                       }else{
                           AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                           builder.setTitle(getText(R.string.pay_attention))
                                   .setMessage(getText(R.string.request_position_dialog2))
                                   .setCancelable(false)
                                   .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                       }
                                   })
                                   .show();
                       }

                        // mNav.navigate(R.id.action_registerFragment_to_registerFeedbackFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error writing document");
                    }
                });




    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationPermissionsEnabledThenAttachListener();
                }
                break;
            }
        }
    }

    private void positionChanged() {

        if(lastKnownLocation != null) {
            String locality = PositionHelper.getLocality(getContext(), lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            if(locality != null && !locality.equals("")) {
                //textLocation.setVisibility(View.VISIBLE);
                System.out.println("località " + locality);
            }
        }else{
            //todo error
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void checkLocationPermissionsEnabledThenAttachListener() {

        //controllo di non avere ancora i permessi
        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //controllo che l'utente non me li abbia già negati precedentemente
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(getText(R.string.pay_attention))
                        .setMessage(getText(R.string.request_position_dialog))
                        .setCancelable(false)
                        .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
        } else {
          //  textViewNearest.setVisibility(View.VISIBLE);
            Map<String, Double> position = new HashMap<>();
            if(locationManager.isLocationEnabled()){
                 lastKnownLocation = PositionHelper.getLastKnownLocation(locationManager);

                if(lastKnownLocation != null) {
                    position.put("latidude", lastKnownLocation.getLatitude());
                    position.put("longitude", lastKnownLocation.getLongitude());

                }else {
                    int notificationId = 1;
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                    notificationManager.notify(notificationId, builder.build());
                }

                badge1.geo_location.add(position);
                mFirestore.collection("Badges").document(badge1.id_badge).update("geo_location", badge1.geo_location);

                System.out.println("running runnable");
                handler.postDelayed(r, minuts_between_misurations * 1000 * 60);
            }else{
                position.put("latidude", null);
                position.put("longitude", null);

                badge1.geo_location.add(position);
                mFirestore.collection("Badges").document(badge1.id_badge).update("geo_location", badge1.geo_location);

                int notificationId = 1;
                
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                notificationManager.notify(notificationId, builder.build());

                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(getText(R.string.pay_attention))
                        .setMessage(getText(R.string.request_position_dialog2))
                        .setCancelable(false)
                        .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .show();
            }
        }
    }

    private void checkLocationPermissionsEnabled() {

        //controllo di non avere ancora i permessi
        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //controllo che l'utente non me li abbia già negati precedentemente
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(getText(R.string.pay_attention))
                        .setMessage(getText(R.string.request_position_dialog))
                        .setCancelable(false)
                        .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
        } else {
            //  textViewNearest.setVisibility(View.VISIBLE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minuts_between_misurations * 499 * 60, 1, listener);
            lastKnownLocation = PositionHelper.getLastKnownLocation(locationManager);
        }
    }

    private void checkLocationPermissionsEnabledThenSaveOnDB() {

        //controllo di non avere ancora i permessi
        if(ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //controllo che l'utente non me li abbia già negati precedentemente
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(getText(R.string.pay_attention))
                        .setMessage(getText(R.string.request_position_dialog))
                        .setCancelable(false)
                        .setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .show();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
        } else {
            //  textViewNearest.setVisibility(View.VISIBLE);
           // locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minuts_between_misurations * 499 * 60, 1, listener);
            lastKnownLocation = PositionHelper.getLastKnownLocation(locationManager);
            registerBadge();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }


    private void updateUser() {

        FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long timestamp = System.currentTimeMillis() + snapshot.getValue(Long.class);
                // Usa il timestamp qui
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Gestione dell'errore
            }
        });

/*
        //come posso ottenere il timestamp da firebase nella mia app android sviluppata in java
        mFirestore.collection("Badges").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Object> documentData = document.getData();

                        Badge badge = new Badge();
                        badge.id = uid;

                        assert documentData != null;

                        badge.email = (String) documentData.get("email");
                        badge.firstName = (String) documentData.get("first_name");
                        badge.secondName = (String) documentData.get("second_name");

                        db.userDao().deleteAll();

                        db.userDao().insertUser(badge);
                    }
                    checkLastVersionAvailable();

                }

            });*/
        }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}