package com.example.emsi_app;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleActivity";
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TabLayout tabLayout;
    private String currentDay = "Lundi";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialiser la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser les vues
        recyclerView = findViewById(R.id.scheduleRecyclerView);
        tabLayout = findViewById(R.id.tabLayout);

        // Configurer RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialiser l'adaptateur avec une liste vide
        adapter = new ScheduleAdapter(new ArrayList<>());
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
                            // Configurer TabLayout
                            setupTabLayout(professorId);
                            
                            // Charger l'emploi du temps initial
                            updateSchedule(professorId);
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

    private void setupTabLayout(String professorId) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentDay = tab.getText().toString();
                Log.d(TAG, "Selected day: " + currentDay);
                updateSchedule(professorId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateSchedule(String professorId) {
        Log.d(TAG, "Updating schedule for professor ID: " + professorId);
        List<Schedule> allSchedules = ScheduleManager.getScheduleForProfessor(professorId);
        List<Schedule> daySchedules = new ArrayList<>();
        
        // Filtrer les cours pour le jour sélectionné
        for (Schedule schedule : allSchedules) {
            if (schedule.getDay().equals(currentDay)) {
                daySchedules.add(schedule);
            }
        }
        
        Log.d(TAG, "Total schedules: " + allSchedules.size());
        Log.d(TAG, "Schedules for " + currentDay + ": " + daySchedules.size());
        
        // Mettre à jour l'adaptateur
        adapter = new ScheduleAdapter(daySchedules);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 