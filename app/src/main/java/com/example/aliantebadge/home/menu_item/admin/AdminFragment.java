package com.example.aliantebadge.home.menu_item.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.R;

public class AdminFragment extends Fragment {

    private NavController mNav;
    LinearLayout clientViewButton;
   // LinearLayout showReviewButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNav = Navigation.findNavController(view);
        clientViewButton = view.findViewById(R.id.clientViewOptions);
        //showReviewButton = view.findViewById(R.id.showReviewOptions);


        clientViewButton.setOnClickListener(v1 -> mNav.navigate(R.id.action_adminFragment_to_clientViewFragment));
       // showReviewButton.setOnClickListener(v3 -> mNav.navigate(R.id.action_adminFragment_to_showReviewFragment));
    }

    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
    }


}
