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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.grbarber.ControlInput;
import com.example.grbarber.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private NavController mNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNav = Navigation.findNavController(view);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final ImageView goBackButton = view.findViewById(R.id.imageViewBackArrowForgotPassword);
        final Button resetPasswordButton = view.findViewById(R.id.buttonResetPassword);
        final TextView emailText = view.findViewById(R.id.inputTextEmailForgotPassword);
        final TextInputLayout emailTextLayout = view.findViewById(R.id.inputLayoutEmailForgotPassword);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();

                emailTextLayout.setError(null);
                emailText.clearFocus();

                //controllo che l'email immessa sia valida
                if(email.isEmpty()) emailTextLayout.setError(getString(R.string.insert_email_error));
                else if(!ControlInput.isValidEmailFormat(email)) emailTextLayout.setError(getString(R.string.email_format_error));
                else {

                    //se è valida, attraverso FirebaseAuth invio una mail di reset password
                    View progressBar = getActivity().findViewById(R.id.progress_bar);
                    Activity activity = getActivity();
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressBar.setVisibility(View.GONE);
                                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if (task.isSuccessful()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                                        builder.setTitle(getText(R.string.perfect));
                                        builder.setMessage(getText(R.string.successfully_sent_reset_password));
                                        builder.show();
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                                        builder.setTitle(getText(R.string.pay_attention));
                                        builder.setMessage(getText(R.string.error_sending_reset_password));
                                        builder.show();
                                    }
                                }
                            });
                }
            }
        });

        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //quando la TextView prende il focus, setto l'errore a null
                    emailTextLayout.setError(null);
                } else {

                    //quando la TextView perde il focus, nascondo la tastiera
                    InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNav.navigateUp();
            }
        });
    }
}