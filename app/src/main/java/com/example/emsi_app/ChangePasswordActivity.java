package com.example.emsi_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPassword, newPassword, confirmPassword;
    private MaterialButton btnChangePassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialiser les vues
        initializeViews();

        // Configurer le bouton de changement de mot de passe
        setupChangePasswordButton();
    }

    private void initializeViews() {
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void setupChangePasswordButton() {
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPass = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        // Validation des champs
        if (currentPass.isEmpty()) {
            currentPassword.setError("Veuillez entrer votre mot de passe actuel");
            return;
        }

        if (newPass.isEmpty()) {
            newPassword.setError("Veuillez entrer un nouveau mot de passe");
            return;
        }

        if (newPass.length() < 6) {
            newPassword.setError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            confirmPassword.setError("Les mots de passe ne correspondent pas");
            return;
        }

        // Récupérer l'utilisateur actuel
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Créer les credentials pour la réauthentification
            AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), currentPass);

            // Réauthentifier l'utilisateur
            user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Mettre à jour le mot de passe
                        user.updatePassword(newPass)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this,
                                        "Mot de passe mis à jour avec succès",
                                        Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this,
                                        "Erreur lors de la mise à jour du mot de passe: " +
                                            updateTask.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                    } else {
                        Toast.makeText(ChangePasswordActivity.this,
                            "Mot de passe actuel incorrect",
                            Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 