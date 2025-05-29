package com.example.emsi_app;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class Sign_up extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView registerLink;
    private final String validEmail = "user@example.com";
    private final String validPassword = "123456";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        registerLink = findViewById(R.id.registerLink);

        // Configurer le clic sur le lien d'inscription
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(Sign_up.this, Register.class);
            startActivity(intent);
            finish();
        });

        // Configurer le clic sur le bouton de connexion
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("L'email est requis");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Le mot de passe est requis");
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Sign_up.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Sign_up.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(Sign_up.this, "Erreur de connexion: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
    }
}


