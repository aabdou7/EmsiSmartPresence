package com.example.emsi_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MakeupSessionAdapter extends RecyclerView.Adapter<MakeupSessionAdapter.ViewHolder> {
    private List<MakeupSession> sessions;

    public MakeupSessionAdapter(List<MakeupSession> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_makeup_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MakeupSession session = sessions.get(position);
        holder.courseNameText.setText(session.getCourseName());
        holder.dateText.setText(session.getDate());
        holder.timeText.setText(session.getStartTime() + " - " + session.getEndTime());
        holder.roomText.setText(session.getRoom());
        holder.statusText.setText(session.getStatus());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseNameText;
        TextView dateText;
        TextView timeText;
        TextView roomText;
        TextView statusText;

        ViewHolder(View view) {
            super(view);
            courseNameText = view.findViewById(R.id.textCourseName);
            dateText = view.findViewById(R.id.textDate);
            timeText = view.findViewById(R.id.textTime);
            roomText = view.findViewById(R.id.textRoom);
            statusText = view.findViewById(R.id.textStatus);
        }
    }
} 