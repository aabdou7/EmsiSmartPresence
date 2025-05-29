package com.example.emsi_app;

import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {
    // Les IDs des professeurs dans Firestore
    private static final String PROFESSOR1_ID = "PROF1"; // ID du premier professeur
    private static final String PROFESSOR2_ID = "PROF2"; // ID du deuxième professeur

    public static List<Schedule> getScheduleForProfessor(String professorId) {
        List<Schedule> schedules = new ArrayList<>();
        
        if (professorId.equals(PROFESSOR1_ID)) {
            // Emploi du temps du premier professeur
            // Lundi
            schedules.add(new Schedule(
                "Lundi",
                "08:30",
                "10:30",
                "Programmation Java",
                "Groupe 1",
                "Centre",
                "Salle 101",
                "Cours"
            ));
            
            schedules.add(new Schedule(
                "Lundi",
                "10:45",
                "12:45",
                "Base de données",
                "Groupe 2",
                "Roudani",
                "Salle 102",
                "TP"
            ));

            // Mardi
            schedules.add(new Schedule(
                "Mardi",
                "08:30",
                "10:30",
                "Programmation Java",
                "Groupe 3",
                "Les Orangers",
                "Salle 103",
                "TD"
            ));

            schedules.add(new Schedule(
                "Mardi",
                "14:00",
                "16:00",
                "Base de données",
                "Groupe 1",
                "Moulay Youssef",
                "Salle 104",
                "Cours"
            ));

            // Mercredi
            schedules.add(new Schedule(
                "Mercredi",
                "08:30",
                "10:30",
                "Programmation Java",
                "Groupe 2",
                "Centre",
                "Salle 105",
                "TP"
            ));

            // Jeudi
            schedules.add(new Schedule(
                "Jeudi",
                "10:45",
                "12:45",
                "Base de données",
                "Groupe 3",
                "Roudani",
                "Salle 106",
                "TD"
            ));

            // Vendredi
            schedules.add(new Schedule(
                "Vendredi",
                "14:00",
                "16:00",
                "Programmation Java",
                "Groupe 1",
                "Les Orangers",
                "Salle 107",
                "Cours"
            ));
            
        } else if (professorId.equals(PROFESSOR2_ID)) {
            // Emploi du temps du deuxième professeur
            // Lundi
            schedules.add(new Schedule(
                "Lundi",
                "08:30",
                "10:30",
                "Réseaux",
                "Groupe 1",
                "Moulay Youssef",
                "Salle 201",
                "Cours"
            ));
            
            schedules.add(new Schedule(
                "Lundi",
                "10:45",
                "12:45",
                "Sécurité",
                "Groupe 2",
                "Centre",
                "Salle 202",
                "TP"
            ));

            // Mardi
            schedules.add(new Schedule(
                "Mardi",
                "08:30",
                "10:30",
                "Réseaux",
                "Groupe 3",
                "Roudani",
                "Salle 203",
                "TD"
            ));

            // Mercredi
            schedules.add(new Schedule(
                "Mercredi",
                "14:00",
                "16:00",
                "Sécurité",
                "Groupe 1",
                "Les Orangers",
                "Salle 204",
                "Cours"
            ));

            // Jeudi
            schedules.add(new Schedule(
                "Jeudi",
                "08:30",
                "10:30",
                "Réseaux",
                "Groupe 2",
                "Moulay Youssef",
                "Salle 205",
                "TP"
            ));

            // Vendredi
            schedules.add(new Schedule(
                "Vendredi",
                "10:45",
                "12:45",
                "Sécurité",
                "Groupe 3",
                "Centre",
                "Salle 206",
                "TD"
            ));
        }
        
        return schedules;
    }
} 