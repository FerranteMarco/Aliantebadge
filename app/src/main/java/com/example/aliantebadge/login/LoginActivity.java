package com.example.aliantebadge.login;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aliantebadge.R;

public class LoginActivity extends AppCompatActivity {
    View progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

    }
}
