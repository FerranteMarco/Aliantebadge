package com.example.aliantebadge.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aliantebadge.BuildConfig;
import com.example.aliantebadge.ControlInput;
import com.example.aliantebadge.Main;
import com.example.aliantebadge.R;
import com.example.aliantebadge.roomDB.AppDatabase;
import com.example.aliantebadge.roomDB.Entity.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private NavController mNav;
    private AppDatabase db = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        db = AppDatabase.getDbInstance(requireActivity());
        mFirestore = FirebaseFirestore.getInstance();
        //per prima cosa verifico che l'utente sia gia loggato, in tal caso non noterà quest'activity
        goMainIfLogged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNav = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();

        final Button loginButton = view.findViewById(R.id.buttonSend);
        final TextView registerButton = view.findViewById(R.id.textViewRegisterFromLogin);
        final TextView passForgotButton = view.findViewById(R.id.textViewPasswordForgot);

        final TextInputEditText email  = view.findViewById(R.id.inputTextEmailLogin);
        final TextInputEditText password  = view.findViewById(R.id.inputTextPasswordLogin);

        final TextInputLayout emailLayout = view.findViewById(R.id.inputLayoutEmailLogin);
        final TextInputLayout passwordLayout = view.findViewById(R.id.inputLayoutPasswordLogin);

        loginButton.setOnClickListener(view1 -> {

            //faccio perdere il focus dalla tastiera in modo che scompaia dallo schermo
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if(requireActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);

            String emailText = Objects.requireNonNull(email.getText()).toString();
            String passwordText = Objects.requireNonNull(password.getText()).toString();

            emailLayout.setError(null);
            passwordLayout.setError(null);

            email.clearFocus();
            password.clearFocus();

            //controllo che non ci siano errori nei dati inseriti e effettuo il login
            //se ci sono errori, li mostro all'utente
            if (emailText.isEmpty()) emailLayout.setError(getString(R.string.insert_email_error));
            else if(!ControlInput.isValidEmailFormat(emailText)) emailLayout.setError(getString(R.string.email_format_error));
            else if (passwordText.isEmpty()) passwordLayout.setError(getString(R.string.insert_password_error));
            else signIn(email.getText().toString(), password.getText().toString(), passwordLayout);

        });

        //rimuove la visulizzazione dell'errore dall'email quando l'utente clicca sul TextInput dell'email
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) emailLayout.setError(null);
        });

        //rimuove la visulizzazione dell'errore dalla password quando l'utente clicca sul TextInput della password
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) passwordLayout.setError(null);
        });

        //al click sul tasto di registrazione carico il fragment
        registerButton.setOnClickListener(v -> mNav.navigate(R.id.action_loginFragment_to_registerFragment));

        //al click sul tasto di password dimenticata carico il fragment
        passForgotButton.setOnClickListener(v -> mNav.navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

    }

    /**
     * eseguo il login dell'utente attraverso FirebaseAuth
     * visualizzo a schermo una progress bar per dare un feedback all'utente sul fatto che sto eseguendo l'azione
     * e anche per evitare che l'untente possa interagire con lo schermo mentre eseguo il login
     * se il login va a buon fine salvo i dati dell'utente in locale e vado in main
     * altrimenti mostro all'utente il tipo di errore avvenuto
     */
    private void signIn(String email, String password, TextInputLayout passLayout) {

        Activity activity = requireActivity();

        View progressBar = activity.findViewById(R.id.progress_bar);

        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        saveUserDBThenGoMain(password);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        passLayout.setError(getString(R.string.wrong_password_error));
                    }
        });

    }

    /**
     * salva i dati dell'utente nel db locale e poi va in main
     */
    private void saveUserDBThenGoMain(String username) {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.userDao().deleteAll();

        mFirestore.collection("Users").document(uid).update("surname",username).addOnCompleteListener(task ->
                    mFirestore.collection("Users").document(uid).get()
                    .addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DocumentSnapshot document = task2.getResult();
                        if (document.exists()) {
                            Map<String, Object> documentData = document.getData();

                            User user = new User();
                            user.uid = uid;
                            assert documentData != null;
                            user.email = (String) documentData.get("email");
                            user.firstName = (String) documentData.get("first_name");
                            user.secondName = (String) documentData.get("second_name");

                            db.userDao().insertUser(user);
                        }
                    }
                    intentMain();
                }));

    }


    /**
     * controllo che l'utente sia gia loggato
     * se è loggato come ospite vado direttamente nel main
     * altrimenti faccio un refresh dei dati dell'utente che ho in locale
     */
    private void goMainIfLogged() {
        User currentLocalUser = db.userDao().getCurrentUser();
        if(currentLocalUser != null){
             intentMain();
        }
    }

    /**
     * avvio l'activity main concludendo l'activity di login in modo da non far tornare l'utente
     * in questa sezione quando preme il tasto indietro
     */
    private void intentMain() {

        mFirestore.collection("Users").document(db.userDao().getCurrentUser().uid).update("versionApp", BuildConfig.VERSION_NAME);
            Intent HomeActivity = new Intent(requireActivity(), Main.class);
            startActivity(HomeActivity);
            requireActivity().finish();


    }
}