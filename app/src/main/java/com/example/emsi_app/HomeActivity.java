package com.example.emsi_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView dashboardAdminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser le TextView pour le nom du professeur
        dashboardAdminName = findViewById(R.id.dashboard_adminName);

        // Récupérer et afficher le nom du professeur
        loadProfessorName();

        // Configurer le bouton des trois points
        ImageButton btnMoreOptions = findViewById(R.id.btnMoreOptions);
        btnMoreOptions.setOnClickListener(v -> showPopupMenu(v));

        // Find the maps card and set click listener
        CardView mapsCard = findViewById(R.id.card5);
        mapsCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
            startActivity(intent);
        });

        // Add click listener for the assistant card
        CardView assistantCard = findViewById(R.id.card7);
        assistantCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, Assistant_virtuel.class);
            startActivity(intent);
        });

        CardView documentsCard = findViewById(R.id.card4);
        documentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, DocumentsActivity.class);
            startActivity(intent);
        });

        // Configurer le clic sur la carte de la liste d'absences
        CardView card3 = findViewById(R.id.card3);
        card3.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AttendanceActivity.class);
            startActivity(intent);
        });

        // Configurer le clic sur la carte planning
        CardView planningCard = findViewById(R.id.card6);
        planningCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });

        // Configurer le clic sur la carte des messages
        CardView messagesCard = findViewById(R.id.card2);
        messagesCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MessagesActivity.class);
            startActivity(intent);
        });

        // Configurer le clic sur la carte des rattrapages
        CardView makeupCard = findViewById(R.id.card1);
        makeupCard.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MakeupSessionsActivity.class);
            startActivity(intent);
        });
    }

    private void loadProfessorName() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    if (name != null && !name.isEmpty()) {
                        dashboardAdminName.setText(name);
                    }
                }
            })
            .addOnFailureListener(e -> {
                // En cas d'erreur, afficher un message par défaut
                dashboardAdminName.setText("Professeur");
            });
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.home_menu, popup.getMenu());
        
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.action_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            }
            else if (itemId == R.id.action_logout) {
                logout();
                return true;
            }
            return false;
        });
        
        popup.show();
    }

    private void logout() {
        // Déconnexion de Firebase
        mAuth.signOut();
        
        // Redirection vers l'écran de connexion
        Intent intent = new Intent(HomeActivity.this, Sign_up.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 