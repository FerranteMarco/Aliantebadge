package com.example.aliantebadge.home.menu_item.setting.item_setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.R;

public class ThemeFragment extends Fragment {
    int themeSelected = -1;
    int currentTheme;

    /**
     * theme = -1   -> no selected
     * theme = 0    -> light
     * theme = 1    -> dark
     */
    Button buttonSave;
    SharedPreferences prefs = null;
    private NavController mNav;
    private RadioGroup groupDark;
    private RadioGroup groupLight;
    private RadioButton radioLight;
    private RadioButton radioDark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        prefs = requireActivity().getSharedPreferences("gr_theme", Context.MODE_PRIVATE);
        currentTheme = prefs.getInt("theme", -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_theme, container, false);

        groupDark = view.findViewById(R.id.groupDark);
        radioDark = view.findViewById(R.id.rbDark);

        radioLight = view.findViewById(R.id.rbLight);
        groupLight = view.findViewById(R.id.groupLight);

        buttonSave = view.findViewById(R.id.buttonSave);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mNav = Navigation.findNavController(view);
        if ( currentTheme == 0 )
            groupLight.check(R.id.rbLight);

        if ( currentTheme == 1 )
            groupDark.check(R.id.rbDark);

        radioLight.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                themeSelected = 0;
                groupDark.clearCheck();
            }
        });

        radioDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                themeSelected = 1;
                groupLight.clearCheck();
            }
        });

        buttonSave.setOnClickListener(v -> {

            switch(themeSelected){
                case 0:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                    //todo https://github.com/imandolatkia/Android-Animated-Theme-Manager
            }
            SharedPreferences.Editor editor = prefs.edit();

            editor.putInt("theme", themeSelected);
            editor.apply();
            mNav.navigateUp();
            requireActivity().recreate();

        });
    }
}
