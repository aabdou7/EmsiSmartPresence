package com.example.emsi_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class SessionHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_history);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Configurer la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialiser le RecyclerView
        recyclerView = findViewById(R.id.recycler_sessions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Charger l'historique des sessions
        loadSessionHistory();
    }

    private void loadSessionHistory() {
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
            .collection("sessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Session> sessions = new ArrayList<>();
                queryDocumentSnapshots.forEach(doc -> {
                    Session session = doc.toObject(Session.class);
                    session.setId(doc.getId());
                    sessions.add(session);
                });
                
                if (sessions.isEmpty()) {
                    Toast.makeText(this, 
                        "Aucune session trouvée", 
                        Toast.LENGTH_SHORT).show();
                }
                
                adapter.updateSessions(sessions);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, 
                    "Erreur lors du chargement de l'historique: " + e.getMessage(), 
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