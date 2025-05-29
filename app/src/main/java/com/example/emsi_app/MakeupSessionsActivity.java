package com.example.emsi_app;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MakeupSessionsActivity extends AppCompatActivity {
    private static final String TAG = "MakeupSessionsActivity";
    private RecyclerView recyclerView;
    private MakeupSessionAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<MakeupSession> sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_sessions);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize data
        sessions = new ArrayList<>();
        // TODO: Load sessions from database
        
        // Setup adapter
        adapter = new MakeupSessionAdapter(sessions);
        recyclerView.setAdapter(adapter);
        
        // Récupérer l'ID de l'utilisateur connecté
        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "User ID from Auth: " + userId);
        
        // Récupérer les informations du professeur depuis Firestore
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Document exists in Firestore");
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null) {
                        Log.d(TAG, "Document data: " + data.toString());
                        // Chercher le professorId dans les données
                        String professorId = null;
                        for (Map.Entry<String, Object> entry : data.entrySet()) {
                            if (entry.getKey().trim().equals("professorId")) {
                                professorId = entry.getValue().toString();
                                break;
                            }
                        }
                        Log.d(TAG, "Professor ID from Firestore: " + professorId);
                        
                        if (professorId != null) {
                            // Charger les sessions de rattrapage
                            loadMakeupSessions(professorId);
                        } else {
                            Log.e(TAG, "professorId is null in Firestore document");
                        }
                    }
                } else {
                    Log.e(TAG, "No such document for user ID: " + userId);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting document", e);
            });
    }

    private void loadMakeupSessions(String professorId) {
        Log.d(TAG, "Loading makeup sessions for professor ID: " + professorId);
        
        db.collection("makeup_sessions")
            .whereEqualTo("professorId", professorId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                sessions.clear();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    MakeupSession session = document.toObject(MakeupSession.class);
                    if (session != null) {
                        session.setId(document.getId());
                        sessions.add(session);
                    }
                }
                Log.d(TAG, "Found " + sessions.size() + " makeup sessions");
                
                // Mettre à jour l'adaptateur
                adapter = new MakeupSessionAdapter(sessions);
                recyclerView.setAdapter(adapter);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading makeup sessions", e);
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 