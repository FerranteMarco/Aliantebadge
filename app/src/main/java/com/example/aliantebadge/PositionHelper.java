package com.example.aliantebadge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.util.List;
import java.util.Locale;

public class PositionHelper {

    /**
     * restituisce la posizione con l'accuratezza più altra cercandola con tutti i provider abilitati
     * da chiamare dopo aver verificato di avere i permessi di posizione
     */
    public static Location getLastKnownLocation(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            //la funzione getLastKnownLocation() è chiamata solo se i permessi ci sono già
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * fornisce in output la località, fornendo latitudine e longitudine della posizione desiderata
     */
    public static String getLocality(Context ctx, double lat, double lng) {
        try {
            Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = null;
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLocality();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * fornisce in output la nazione, fornendo latitudine e longitudine della posizione desiderata
     */
    public static String getCountryName(Context ctx, double lat, double lng) {
        try {
            Geocoder gcd = new Geocoder(ctx, Locale.getDefault());
            List<Address> addresses = null;
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getCountryName();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
}
