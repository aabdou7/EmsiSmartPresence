package com.example.emsi_app;

import com.example.emsi_app.models.Attendance;
import com.example.emsi_app.models.Group;
import com.example.emsi_app.models.Student;
import com.example.emsi_app.models.Site;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;

public class FirestoreManager {
    private final FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }

    public interface GroupsCallback {
        void onSuccess(List<Group> groups);
        void onError(String error);
    }

    public interface StudentsCallback {
        void onSuccess(List<Student> students);
        void onError(String error);
    }

    public interface AttendanceCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface SitesCallback {
        void onSuccess(List<Site> sites);
        void onError(String error);
    }

    public void getSites(SitesCallback callback) {
        Log.d("FirestoreManager", "Début de la récupération des sites");
        db.collection("sites")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Site> sites = new ArrayList<>();
                Log.d("FirestoreManager", "Nombre de sites trouvés: " + queryDocumentSnapshots.size());
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String siteName = document.getString("name");
                    Log.d("FirestoreManager", "Site trouvé: " + siteName + " (ID: " + document.getId() + ")");
                    
                    Site site = new Site(
                        document.getId(),
                        siteName
                    );
                    sites.add(site);
                }
                callback.onSuccess(sites);
            })
            .addOnFailureListener(e -> {
                Log.e("FirestoreManager", "Erreur lors de la récupération des sites: " + e.getMessage());
                callback.onError(e.getMessage());
            });
    }

    public void getGroups(GroupsCallback callback) {
        db.collection("groups")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Group> groups = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Group group = new Group(
                        document.getId(),
                        document.getString("name"),
                        document.getString("siteId")
                    );
                    groups.add(group);
                }
                callback.onSuccess(groups);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getStudentsByGroup(String groupId, StudentsCallback callback) {
        db.collection("students")
            .whereEqualTo("groupId", groupId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Student> students = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Student student = new Student(
                        document.getId(),
                        document.getString("name"),
                        document.getString("groupId")
                    );
                    students.add(student);
                }
                callback.onSuccess(students);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getStudentsByGroupAndSite(String groupId, String site, StudentsCallback callback) {
        // D'abord, vérifier si le groupe existe avec ce site
        db.collection("groups")
            .whereEqualTo("site", site)
            .get()
            .addOnSuccessListener(groupQuery -> {
                if (groupQuery.isEmpty()) {
                    callback.onError("Aucun groupe trouvé pour ce site");
                    return;
                }

                // Ensuite, récupérer les étudiants du groupe
                db.collection("students")
                    .whereEqualTo("groupId", groupId)
                    .get()
                    .addOnSuccessListener(studentQuery -> {
                        List<Student> students = new ArrayList<>();
                        for (QueryDocumentSnapshot document : studentQuery) {
                            Student student = new Student(
                                document.getId(),
                                document.getString("name"),
                                document.getString("groupId")
                            );
                            students.add(student);
                        }
                        callback.onSuccess(students);
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void saveAttendance(String groupId, Date date, List<Attendance> attendances, String remarks, AttendanceCallback callback) {
        // D'abord, récupérer les informations du groupe
        db.collection("groups")
            .document(groupId)
            .get()
            .addOnSuccessListener(groupDocument -> {
                String groupName = groupDocument.getString("name");
                String siteId = groupDocument.getString("siteId");

                // Ensuite, récupérer le nom du site
                db.collection("sites")
                    .document(siteId)
                    .get()
                    .addOnSuccessListener(siteDocument -> {
                        String siteName = siteDocument.getString("name");

                        // Pour chaque présence, récupérer le nom de l'étudiant
                        for (Attendance attendance : attendances) {
                            db.collection("students")
                                .document(attendance.getStudentId())
                                .get()
                                .addOnSuccessListener(studentDocument -> {
                                    String studentName = studentDocument.getString("name");

                                    // Créer le document de présence avec toutes les informations
                                    Map<String, Object> attendanceData = new HashMap<>();
                                    attendanceData.put("student_id", attendance.getStudentId());
                                    attendanceData.put("student_name", studentName);
                                    attendanceData.put("group_id", groupId);
                                    attendanceData.put("group_name", groupName);
                                    attendanceData.put("site_id", siteId);
                                    attendanceData.put("site_name", siteName);
                                    attendanceData.put("date", date);
                                    attendanceData.put("is_present", attendance.isPresent());
                                    attendanceData.put("remarks", remarks);

                                    db.collection("attendances")
                                        .add(attendanceData)
                                        .addOnSuccessListener(documentReference -> {
                                            // Si c'est la dernière présence, appeler le callback de succès
                                            if (attendance == attendances.get(attendances.size() - 1)) {
                                                callback.onSuccess();
                                            }
                                        })
                                        .addOnFailureListener(e -> callback.onError(e.getMessage()));
                                })
                                .addOnFailureListener(e -> callback.onError(e.getMessage()));
                        }
                    })
                    .addOnFailureListener(e -> callback.onError(e.getMessage()));
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void addGroup(String name, String site, GroupsCallback callback) {
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("name", name);
        groupData.put("site", site);

        db.collection("groups")
            .add(groupData)
            .addOnSuccessListener(documentReference -> {
                getGroups(callback);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void addStudent(String name, String groupId, StudentsCallback callback) {
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("name", name);
        studentData.put("groupId", groupId);

        db.collection("students")
            .add(studentData)
            .addOnSuccessListener(documentReference -> {
                getStudentsByGroup(groupId, callback);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
} 