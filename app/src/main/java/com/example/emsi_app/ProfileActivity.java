package com.example.emsi_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.Timestamp;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView profileName, profileEmail, profilePhone, profileDepartment;
    private TextView profileStatus, profileJoinDate, profileLastLogin, profileChain;
    private MaterialButton btnEditProfile, btnChangePassword, btnViewHistory;

    private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == EditProfileActivity.RESULT_PROFILE_UPDATED) {
                loadProfileData();
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mon Profil");

        // Initialiser les vues
        initializeViews();
        
        // Charger les données du profil
        loadProfileData();

        // Configurer les boutons
        setupButtons();
    }

    private void initializeViews() {
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);
        profileDepartment = findViewById(R.id.profile_department);
        profileStatus = findViewById(R.id.profile_status);
        profileJoinDate = findViewById(R.id.profile_join_date);
        profileLastLogin = findViewById(R.id.profile_last_login);
        profileChain = findViewById(R.id.profile_chain);
        
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnViewHistory = findViewById(R.id.btn_view_history);
    }

    private void loadProfileData() {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Récupérer l'email depuis Firebase Auth
        if (mAuth.getCurrentUser() != null) {
            profileEmail.setText(mAuth.getCurrentUser().getEmail());
        }

        // Informations statiques
        profileDepartment.setText("Informatique");
        profileChain.setText("GL");

        // Mettre à jour la dernière connexion et enregistrer la session
        updateLastLogin(userId);
        recordNewSession(userId);

        // Récupérer les données depuis Firestore
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Récupérer le nom et le statut
                    String name = documentSnapshot.getString("name");
                    String status = documentSnapshot.getString("statut");
                    String phone = documentSnapshot.getString("phone");
                    
                    if (name != null && !name.isEmpty()) {
                        profileName.setText(name);
                    } else {
                        profileName.setText("Non renseigné");
                    }
                    
                    if (status != null && !status.isEmpty()) {
                        profileStatus.setText(status);
                    } else {
                        profileStatus.setText("Étudiant");
                    }

                    if (phone != null && !phone.isEmpty()) {
                        profilePhone.setText(phone);
                    } else {
                        profilePhone.setText("Non renseigné");
                    }

                    // Récupérer les dates
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    
                    if (documentSnapshot.getTimestamp("date_inscription") != null) {
                        Date joinDate = documentSnapshot.getTimestamp("date_inscription").toDate();
                        profileJoinDate.setText(sdf.format(joinDate));
                    }

                    if (documentSnapshot.getTimestamp("derniere_connexion") != null) {
                        Date lastLogin = documentSnapshot.getTimestamp("derniere_connexion").toDate();
                        profileLastLogin.setText(sdf.format(lastLogin));
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, 
                    "Erreur lors du chargement du profil: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void updateLastLogin(String userId) {
        // Mettre à jour la dernière connexion dans Firestore
        db.collection("users").document(userId)
            .update("derniere_connexion", new Date())
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Erreur lors de la mise à jour de la dernière connexion", 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void recordNewSession(String userId) {
        // Créer une nouvelle session
        String sessionId = db.collection("users").document(userId)
            .collection("sessions").document().getId();
            
        Session session = new Session(
            sessionId,
            new Timestamp(new Date()),
            "Android " + android.os.Build.VERSION.RELEASE + " - " + 
            android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
        );

        // Enregistrer la session dans Firestore
        db.collection("users").document(userId)
            .collection("sessions").document(sessionId)
            .set(session)
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Erreur lors de l'enregistrement de la session", 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void setupButtons() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        btnViewHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, SessionHistoryActivity.class));
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