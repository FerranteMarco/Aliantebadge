package com.example.aliantebadge;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.aliantebadge.roomDB.AppDatabase;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Main extends AppCompatActivity {

    static final float END_SCALE = 1f;

    Toolbar toolbar;
    NavigationView navigationView;
    private AppDatabase db = null;
    BroadcastReceiver broadcastReceiver;
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawerLayout;
    LinearLayout contentView;
    FirebaseUser currentUser;
    NavController navController;
    View progressBar;
    Menu hamburgerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        db = AppDatabase.getDbInstance(this);

        TextView creator = findViewById(R.id.creator);
        creator.setText("App creata da Marco Ferrante\nVersione " + BuildConfig.VERSION_NAME);

        currentUser =  FirebaseAuth.getInstance().getCurrentUser();
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

        createAppBar();
        hamburgerMenu = navigationView.getMenu();

        navigationView.bringToFront();
        animateNavigationDrawer();
        contentView = findViewById(R.id.contentHome);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        hamburgerMenu = navigationView.getMenu();



        if(currentUser!=null) {
            if (Variable.isAdmin(currentUser.getUid())) {
                hamburgerMenu.setGroupVisible(R.id.adminOption, true);
            }
            if(Variable.isOwner(currentUser.getUid()))
                hamburgerMenu.setGroupVisible(R.id.adminEnableOption, true);
        }



        //imposto la navigazione per i click sulle voci del menu a sinistra
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){

                case R.id.itemEnableAdmin:
                    closeDrawerIfOpen();
                   // navController.navigate(R.id.enableAdminFragment);
                    return true;

                case R.id.itemSettings:
                    closeDrawerIfOpen();
                    navController.navigate(R.id.settingFragment);
                    return true;

                case R.id.itemAdmin:
                    closeDrawerIfOpen();
                    //navController.navigate(R.id.adminFragment);
                    return true;

                case R.id.itemSignOut:
                    //faccio logout dell'utente se è loggato
                    //in entrambi i casi cancello i dati dal locale
                    if(currentUser != null) {
                        String uid = currentUser.getUid();
                        db.userDao().deleteByUserId(uid);
                        FirebaseAuth.getInstance().signOut();
                    }

                    //avvio l'activity di login
                    Intent LoginActivity = new Intent(Main.this, com.example.aliantebadge.login.LoginActivity.class);        //activity chiamante, activity chiamata
                    startActivity(LoginActivity);
                    finish();
                    return true;
            }
            return false;
        });

        //registra il receiver che si mette in ascolto della presenza di connessione di rete
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver((BroadcastReceiver) broadcastReceiver, intentFilter);

    }

    public void setNotification() {

            getSupportActionBar().setHomeAsUpIndicator(setBadgeCount(this, R.drawable.ic_burger));
            hamburgerMenu.getItem(0).setIcon(setBadgeCount(this,R.drawable.ic_round_settings_24));

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        //stacco il reciever per la presenza di connessione di rete
        //unregisterReceiver((BroadcastReceiver) broadcastReceiver);
    }

    /**
     * collega il menu a sinistra e la barra in alto al navController
     * in modo da mostrare il tasto per aprire il menu a sinistra
     * e mostrare il titolo del fragment nella top bar quando ci si muove
     */
    private void createAppBar() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.homeFragment)//, R.id.profileFragment)
                .setOpenableLayout(drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private Drawable setBadgeCount(Context context, int res){

        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable);
        Drawable mainIcon = ContextCompat.getDrawable(context, res);
        BadgeDrawable badge = new BadgeDrawable(context);
        badge.setCount("");
        assert icon != null;
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);

        return icon;
    }

    @Override
    protected void onResume() {
        super.onResume();


        if(Variable.isLastVersionAvailable()) {
            setNotification();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);

        //quando clicco indietro nascondo la tastiera
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        //se il navigate app è gestito dal navControlle, non eseguo super.onSupportNavigateUp();
        if(!NavigationUI.navigateUp(navController, mAppBarConfiguration))
            super.onSupportNavigateUp();

        return true;
    }

    @Override
    public void onBackPressed() {
        //quando va indietro, se è aperto l'hamburger menu, lo chiude
        if(drawerLayout.isDrawerVisible(GravityCompat.START))drawerLayout.closeDrawer(GravityCompat.START);
        else {
            //naviga all'indietro
            super.onBackPressed();

        }
    }

    /**
     * permette la chiusura del menù a sinistra
     */
    private void closeDrawerIfOpen() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START))drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void animateNavigationDrawer() {

        //drawerLayout.setScrimColor(getResources().getColor(R.color.light_pink, getTheme()));
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

}