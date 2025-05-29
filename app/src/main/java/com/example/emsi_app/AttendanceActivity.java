package com.example.emsi_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsi_app.adapters.StudentAttendanceAdapter;
import com.example.emsi_app.models.Attendance;
import com.example.emsi_app.models.Group;
import com.example.emsi_app.models.Site;
import com.example.emsi_app.models.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {
    private MaterialAutoCompleteTextView groupSpinner;
    private MaterialAutoCompleteTextView siteSpinner;
    private Button dateButton;
    private RecyclerView studentsRecyclerView;
    private EditText remarksEditText;
    private Button saveButton;

    private List<Group> groups;
    private List<Student> students;
    private List<Attendance> attendances;
    private Date selectedDate;
    private StudentAttendanceAdapter adapter;
    private FirestoreManager firestoreManager;
    private List<Site> sites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Initialiser FirestoreManager
        firestoreManager = new FirestoreManager();

        // Initialiser les vues
        initializeViews();
        
        // Initialiser les données
        initializeData();
        
        // Configurer les listeners
        setupListeners();
    }

    private void initializeViews() {
        groupSpinner = findViewById(R.id.groupSpinner);
        siteSpinner = findViewById(R.id.siteSpinner);
        dateButton = findViewById(R.id.dateButton);
        studentsRecyclerView = findViewById(R.id.studentsRecyclerView);
        remarksEditText = findViewById(R.id.remarksEditText);
        saveButton = findViewById(R.id.saveButton);

        // Configurer RecyclerView
        studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initializeData() {
        // Charger les sites depuis Firestore
        firestoreManager.getSites(new FirestoreManager.SitesCallback() {
            @Override
            public void onSuccess(List<Site> sitesList) {
                runOnUiThread(() -> {
                    sites = sitesList;
                    Log.d("AttendanceActivity", "Sites chargés: " + sites.size());
                    
                    if (sites.isEmpty()) {
                        Toast.makeText(AttendanceActivity.this, 
                            "Aucun site trouvé", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Configurer l'AutoCompleteTextView des sites
                    ArrayAdapter<Site> siteAdapter = new ArrayAdapter<>(AttendanceActivity.this,
                            android.R.layout.simple_dropdown_item_1line, sites);
                    siteSpinner.setAdapter(siteAdapter);

                    // Charger les groupes après avoir chargé les sites
                    loadGroups();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("AttendanceActivity", "Erreur chargement sites: " + error);
                    Toast.makeText(AttendanceActivity.this, 
                        "Erreur chargement sites: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });

        // Initialiser la date
        selectedDate = new Date();
        updateDateButton();
    }

    private void loadGroups() {
        firestoreManager.getGroups(new FirestoreManager.GroupsCallback() {
            @Override
            public void onSuccess(List<Group> groupsList) {
                runOnUiThread(() -> {
                    groups = groupsList;
                    Log.d("AttendanceActivity", "Groupes chargés: " + groups.size());
                    
                    // Configurer l'AutoCompleteTextView des groupes
                    ArrayAdapter<Group> groupAdapter = new ArrayAdapter<>(AttendanceActivity.this,
                            android.R.layout.simple_dropdown_item_1line, groups);
                    groupSpinner.setAdapter(groupAdapter);

                    // Charger les étudiants du premier groupe
                    if (!groups.isEmpty()) {
                        loadStudentsForGroup(groups.get(0).getId());
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e("AttendanceActivity", "Erreur chargement groupes: " + error);
                    Toast.makeText(AttendanceActivity.this, 
                        "Erreur chargement groupes: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupListeners() {
        // Listener pour le changement de site
        siteSpinner.setOnItemClickListener((parent, view, position, id) -> {
            Site selectedSite = (Site) parent.getItemAtPosition(position);
            filterGroupsBySite(selectedSite);
        });

        // Listener pour le changement de groupe
        groupSpinner.setOnItemClickListener((parent, view, position, id) -> {
            Group selectedGroup = (Group) parent.getItemAtPosition(position);
            loadStudentsForGroup(selectedGroup.getId());
        });

        // Listener pour le bouton de date
        dateButton.setOnClickListener(v -> showDatePicker());

        // Listener pour le bouton de sauvegarde
        saveButton.setOnClickListener(v -> saveAttendance());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    selectedDate = calendar.getTime();
                    updateDateButton();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateButton() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        String dateText = String.format("%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
        dateButton.setText(dateText);
    }

    private void loadStudentsForGroup(String groupId) {
        firestoreManager.getStudentsByGroup(groupId, new FirestoreManager.StudentsCallback() {
            @Override
            public void onSuccess(List<Student> studentsList) {
                runOnUiThread(() -> {
                    students = studentsList;
                    attendances = new ArrayList<>();
                    for (Student student : students) {
                        attendances.add(new Attendance(null, student.getId(), 
                            groupId, selectedDate, true, ""));
                    }
                    adapter = new StudentAttendanceAdapter(students, attendances);
                    studentsRecyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(AttendanceActivity.this, "Erreur: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveAttendance() {
        String remarks = remarksEditText.getText().toString();
        String selectedGroupName = groupSpinner.getText().toString();
        
        // Trouver le groupe sélectionné dans la liste
        Group selectedGroup = null;
        for (Group group : groups) {
            if (group.getName().equals(selectedGroupName)) {
                selectedGroup = group;
                break;
            }
        }

        if (selectedGroup == null) {
            Toast.makeText(this, "Veuillez sélectionner un groupe", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreManager.saveAttendance(selectedGroup.getId(), selectedDate, attendances, remarks,
            new FirestoreManager.AttendanceCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(AttendanceActivity.this, 
                            "Présences enregistrées avec succès", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(AttendanceActivity.this, 
                            "Erreur: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
    }

    private void filterGroupsBySite(Site selectedSite) {
        if (selectedSite == null || groups == null) {
            Log.e("AttendanceActivity", "Site ou groupes null");
            return;
        }

        Log.d("AttendanceActivity", "Filtrage des groupes pour le site: " + selectedSite.getName());
        List<Group> filteredGroups = new ArrayList<>();
        for (Group group : groups) {
            if (group != null && group.getSiteId() != null && 
                group.getSiteId().equals(selectedSite.getId())) {
                filteredGroups.add(group);
                Log.d("AttendanceActivity", "Groupe ajouté: " + group.getName());
            }
        }

        ArrayAdapter<Group> groupAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, filteredGroups);
        groupSpinner.setAdapter(groupAdapter);

        if (!filteredGroups.isEmpty()) {
            loadStudentsForGroup(filteredGroups.get(0).getId());
        } else {
            Log.d("AttendanceActivity", "Aucun groupe trouvé pour ce site");
        }
    }
} 