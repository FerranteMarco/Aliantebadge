package com.example.aliantebadge.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.grbarber.BuildConfig;
import com.example.grbarber.ControlInput;
import com.example.grbarber.R;
import com.example.grbarber.roomDB.AppDatabase;
import com.example.grbarber.roomDB.Entity.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterFragment extends Fragment{

    private FirebaseAuth mAuth;
    private AppDatabase db = null;
    private NavController mNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mNav = Navigation.findNavController(view);
        final ImageView back = view.findViewById(R.id.imageViewBackArrow);
        final Button register = view.findViewById(R.id.buttonRegister);

        final TextInputEditText email  = view.findViewById(R.id.inputTextEmail);
        final TextInputEditText password  = view.findViewById(R.id.inputTextPassword);
        final TextInputEditText passwordRepeat  = view.findViewById(R.id.inputTextRepeatPassword);
        final TextInputEditText firstName  = view.findViewById(R.id.inputTextName);
        final TextInputEditText secondName  = view.findViewById(R.id.inputTextSurname);


        final TextInputLayout emailLayout = view.findViewById(R.id.inputLayoutEmail);
        final TextInputLayout passwordLayout = view.findViewById(R.id.inputLayoutPassword);
        final TextInputLayout repeatPasswordLayout = view.findViewById(R.id.inputRepeatPassword);
        final TextInputLayout firstNameLayout  = view.findViewById(R.id.inputLayoutName);
        final TextInputLayout secondNameLayout  = view.findViewById(R.id.inputLayoutSurname);


        back.setOnClickListener(v -> mNav.navigateUp());

        //quando l'utente clicca su registrati, controllo che tutti i dati inseriti siano nel formato corretto
        //se non lo sono mostro all'utente l'errore
        //altrimenti eseguo la registrazione e vado in main
        register.setOnClickListener(view1 -> {

            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if(requireActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), 0);


            String emailText = Objects.requireNonNull(email.getText()).toString();
            String passwordText = Objects.requireNonNull(password.getText()).toString();
            String repeatPasswordText = Objects.requireNonNull(passwordRepeat.getText()).toString();
            String firstNameText = firstName.getText().toString();
            String secondNameText = secondName.getText().toString();

            emailLayout.setError(null);
            passwordLayout.setError(null);
            repeatPasswordLayout.setError(null);
            firstNameLayout.setError(null);
            secondNameLayout.setError(null);

            email.clearFocus();
            password.clearFocus();
            passwordRepeat.clearFocus();
            firstName.clearFocus();
            secondName.clearFocus();

            firstNameText = ControlInput.deleteBlank(firstNameText);
            secondNameText = ControlInput.deleteBlank(secondNameText);

            if(emailText.isEmpty()) emailLayout.setError(getString(R.string.insert_email_error));
            else if(!ControlInput.isValidEmailFormat(emailText)) emailLayout.setError(getString(R.string.email_format_error));
            else if(firstNameText.isEmpty()) firstNameLayout.setError("Inserisci il tuo nome");
            else if(secondNameText.isEmpty()) secondNameLayout.setError("Inserisci il tuo cognome");
            else if(passwordText.isEmpty()) passwordLayout.setError(getString(R.string.insert_password_error));
            else if(!ControlInput.isPasswordSafe(passwordText)) passwordLayout.setError(getString(R.string.password_not_safe_error));
            else if(repeatPasswordText.isEmpty()) repeatPasswordLayout.setError(getString(R.string.insert_repeat_password_error));
            else if(!passwordText.equals(repeatPasswordText)) repeatPasswordLayout.setError(getString(R.string.non_equivalent_passwords));
            else registerNewUser(emailText, passwordText, firstNameText, secondNameText,passwordText);
        });

        //rimuove la visulizzazione dell'errore dall'email quando l'utente clicca sul TextInput
        email.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) emailLayout.setError(null);
        });

        //rimuove la visulizzazione dell'errore dalla password quando l'utente clicca sul TextInput
        password.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) passwordLayout.setError(null);
        });

        //rimuove la visulizzazione dell'errore dalla ripetizione password quando l'utente clicca sul TextInput
        passwordRepeat.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) repeatPasswordLayout.setError(null);
        });
    }

    /**
     * eseguo la registrazione dell'utente attraverso FirebaseAuth
     * visualizzo a schermo una progress bar per dare un feedback all'utente sul fatto che sto eseguendo l'azione
     * e anche per evitare che l'untente possa interagire con lo schermo mentre eseguo la registrazione
     * se la registrazione va a buon fine salvo i dati dell'utente in locale e vado in main
     * altrimenti mostro all'utente il tipo di errore avvenuto
     */
    private void registerNewUser(String email, String password, String firstName, String secondName, String surname) {

        View progressBar = requireActivity().findViewById(R.id.progress_bar);
        Activity activity = requireActivity();
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {

                    if (task.isSuccessful()) {

                        saveUserDB(email, firstName, secondName,surname);
                        progressBar.setVisibility(View.GONE);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                        builder.setTitle(getString(R.string.pay_attention));
                        builder.setMessage(getString(R.string.an_error_occurred));
                        builder.show();
                    }
                });

    }

    /**
     * salvo l'user appena registrato tramite email
     * setto l'username prendendo la prima parte della email utilizzata
     * che sarà modificabile dalla sezione "modifica profilo"
     */
    private void saveUserDB (String email,String firstName, String secondName,String surname) {

        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        db = AppDatabase.getDbInstance(requireActivity());

        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();

        user.put("first_name", firstName);
        user.put("second_name", secondName);
        user.put("email", email);
        user.put("surname",surname);
        user.put("accepted", false);
        user.put("versionApp", BuildConfig.VERSION_NAME);



        firestoreDB.collection("Users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        User user = new User();
                        user.uid = uid;
                        user.email = email;
                        user.firstName = firstName;
                        user.secondName = secondName;
                        user.accepted = false;


                        db.userDao().insertUser(user);
                        mNav.navigate(R.id.action_registerFragment_to_registerFeedbackFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error writing document");
                    }
                });
    }
}