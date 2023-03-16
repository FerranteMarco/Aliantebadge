package com.example.aliantebadge.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.Main;
import com.example.aliantebadge.R;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFeedbackFragment extends Fragment{

    private NavController mNav = null;
    private AppDatabase db = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
 
        db = AppDatabase.getDbInstance(requireActivity());

        //se l'utente clicca il tasto indietro, si aspetta di tornare al login, quindi effettuo un logout
        //per evitare l'insorgenza di problemi
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(mNav != null) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser != null) {
                        String uid = currentUser.getUid();
                        db.userDao().deleteByUserId(uid);
                        FirebaseAuth.getInstance().signOut();
                    }
                    mNav.navigateUp();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNav = Navigation.findNavController(view);
        final Button buttonTutorial = view.findViewById(R.id.buttonGotoTutorial);

        //quando l'utente clicca sul tasto, avvio il tutorial
        buttonTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), Main.class));
                requireActivity().finish();
            }
        });
    }
}
