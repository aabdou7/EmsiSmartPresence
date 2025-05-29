package com.example.emsi_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emsi_app.R;
import com.example.emsi_app.models.Attendance;
import com.example.emsi_app.models.Student;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {
    private List<Student> students;
    private List<Attendance> attendances;

    public StudentAttendanceAdapter(List<Student> students, List<Attendance> attendances) {
        this.students = students;
        this.attendances = attendances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_attendance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        Attendance attendance = attendances.get(position);

        holder.studentNameTextView.setText(student.getName());
        holder.presentCheckBox.setChecked(attendance.isPresent());

        holder.presentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            attendance.setPresent(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView studentNameTextView;
        CheckBox presentCheckBox;

        ViewHolder(View view) {
            super(view);
            studentNameTextView = view.findViewById(R.id.studentNameTextView);
            presentCheckBox = view.findViewById(R.id.presentCheckBox);
        }
    }
} 