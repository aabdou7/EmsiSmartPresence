package com.example.emsi_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    public static final int RESULT_PROFILE_UPDATED = 100;
    private TextInputEditText editName, editPhone, editDepartment, editStatus, editChain;
    private MaterialButton btnSave;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialiser les vues
        initializeViews();
        
        // Charger les données actuelles
        loadCurrentData();

        // Configurer le bouton de sauvegarde
        setupSaveButton();
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editDepartment = findViewById(R.id.edit_department);
        editStatus = findViewById(R.id.edit_status);
        editChain = findViewById(R.id.edit_chain);
        btnSave = findViewById(R.id.btn_save);
    }

    private void loadCurrentData() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    editName.setText(documentSnapshot.getString("name"));
                    editPhone.setText(documentSnapshot.getString("phone"));
                    editDepartment.setText(documentSnapshot.getString("department"));
                    editStatus.setText(documentSnapshot.getString("statut"));
                    editChain.setText(documentSnapshot.getString("chaine"));
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Erreur lors du chargement des données: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", editName.getText().toString().trim());
        updates.put("phone", editPhone.getText().toString().trim());
        updates.put("department", editDepartment.getText().toString().trim());
        updates.put("statut", editStatus.getText().toString().trim());
        updates.put("chaine", editChain.getText().toString().trim());

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                setResult(RESULT_PROFILE_UPDATED);
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Erreur lors de la mise à jour: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
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