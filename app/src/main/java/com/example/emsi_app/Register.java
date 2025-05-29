package com.example.emsi_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private static final String TAG = "Register";
    private TextInputEditText etName, etEmail, etPassword, confPassword;
    private MaterialButton btnRegister;
    private TextView loginLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser les vues
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        confPassword = findViewById(R.id.conf_password);
        btnRegister = findViewById(R.id.btn_register);
        loginLink = findViewById(R.id.loginLink);

        // Configurer le clic sur le lien de connexion
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Sign_up.class);
            startActivity(intent);
            finish();
        });

        // Configurer le clic sur le bouton d'inscription
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = confPassword.getText().toString().trim();

            if (validateInputs(name, email, password, confirmPassword)) {
                registerUser(name, email, password);
            }
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (name.isEmpty()) {
            etName.setError("Le nom est requis");
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("L'email est requis");
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Le mot de passe est requis");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confPassword.setError("La confirmation du mot de passe est requise");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confPassword.setError("Les mots de passe ne correspondent pas");
            return false;
        }
        return true;
    }

    private void registerUser(String name, String email, String password) {
        Log.d(TAG, "Début de l'inscription pour: " + email);
        
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Création de l'utilisateur réussie");
                    String userId = mAuth.getCurrentUser().getUid();
                    Log.d(TAG, "ID utilisateur: " + userId);

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("role", "professeur");
                    user.put("statut", "actif");
                    user.put("date_inscription", new Timestamp(new Date()));
                    user.put("derniere_connexion", new Timestamp(new Date()));
                    user.put("chaine", "EMSI");

                    Log.d(TAG, "Tentative d'écriture dans Firestore");
                    db.collection("users").document(userId)
                        .set(user)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Données utilisateur enregistrées avec succès");
                            Toast.makeText(Register.this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, HomeActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Erreur lors de l'enregistrement des données", e);
                            Toast.makeText(Register.this, "Erreur lors de l'enregistrement des données: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                        });
                } else {
                    Log.e(TAG, "Erreur lors de la création de l'utilisateur", task.getException());
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        etEmail.setError("Cette adresse email est déjà utilisée");
                        Toast.makeText(Register.this, 
                            "Cette adresse email est déjà utilisée. Veuillez utiliser une autre adresse ou vous connecter.", 
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Register.this, 
                            "Erreur lors de l'inscription: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}