package com.example.aliantebadge.home.menu_item.setting;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.R;
import com.example.aliantebadge.Variable;
import com.example.aliantebadge.home.menu_item.setting.item_setting.ArrowBadgeDrawable;

public class SettingFragment extends Fragment {
    private NavController mNav;
    LinearLayout versionAppButton;
    LinearLayout languageButton;
    LinearLayout themeButton;
    //LinearLayout reviewButton;
    ImageView versionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNav = Navigation.findNavController(view);
        versionAppButton = view.findViewById(R.id.versionAppOptions);
        themeButton = view.findViewById(R.id.themeOptions);
        languageButton = view.findViewById(R.id.languageOptions);
        versionButton = view.findViewById(R.id.versionButton);
        //reviewButton = view.findViewById(R.id.reviewOption);

        if(Variable.isLastVersionAvailable())
            versionButton.setImageDrawable(setBadgeCount(requireContext(),R.drawable.ic_baseline_navigate_next_24_black));

        versionAppButton.setOnClickListener(v -> mNav.navigate(R.id.action_settingFragment_to_versionAppFragment));
        themeButton.setOnClickListener(v1 -> mNav.navigate(R.id.action_settingFragment_to_themeFragment));
        //reviewButton.setOnClickListener(v3 -> mNav.navigate(R.id.action_settingFragment_to_reviewFragment));
    }

    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }

    private Drawable setBadgeCount(Context context, int res){

        LayerDrawable icon = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.ic_badge_drawable);
        Drawable mainIcon = ContextCompat.getDrawable(context, res);
        ArrowBadgeDrawable badge = new ArrowBadgeDrawable(context);
        badge.setCount("");
        assert icon != null;
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
        icon.setDrawableByLayerId(R.id.ic_main_icon, mainIcon);

        return icon;
    }

}
