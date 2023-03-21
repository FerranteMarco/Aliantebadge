package com.example.aliantebadge.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.PositionHelper;
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
    Date date = new Date();
    Date date2 = new Date();
    FirebaseFirestore mFirestore;
    Button timbra;
    LocationListener listener;
    LocationManager locationManager;
    Location lastKnownLocation;

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        checkLocationPermissionsEnabledThenAttachListener();


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




        timbra.setOnClickListener(view12 -> {
            //todo check permission
            positionChanged();

            
        });

       if(db.userDao().getCurrentUser() != null)
            username.setText(db.userDao().getCurrentUser().firstName);
    }


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
        System.out.println("ci siamo eh ");
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 20, listener);
            lastKnownLocation = PositionHelper.getLastKnownLocation(locationManager);
            //positionChanged();*/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(listener);
    }



}