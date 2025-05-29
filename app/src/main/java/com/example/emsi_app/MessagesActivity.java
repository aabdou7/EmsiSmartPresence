package com.example.emsi_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
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

public class MessagesActivity extends AppCompatActivity {
    private static final String TAG = "MessagesActivity";
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialiser l'adaptateur avec une liste vide
        adapter = new MessageAdapter(new ArrayList<>());
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
                            // Charger les messages
                            loadMessages(professorId);
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

    private void loadMessages(String professorId) {
        Log.d(TAG, "Loading messages for professor ID: " + professorId);
        
        db.collection("messages")
            .whereEqualTo("professorId", professorId)
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Message> messages = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Message message = document.toObject(Message.class);
                    if (message != null) {
                        message.setId(document.getId());
                        messages.add(message);
                    }
                }
                Log.d(TAG, "Found " + messages.size() + " messages");
                
                // Mettre à jour l'adaptateur
                adapter = new MessageAdapter(messages);
                recyclerView.setAdapter(adapter);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading messages", e);
                // Afficher un message d'erreur à l'utilisateur
                Toast.makeText(MessagesActivity.this, 
                    "Erreur lors du chargement des messages. Veuillez réessayer.", 
                    Toast.LENGTH_LONG).show();
            });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}