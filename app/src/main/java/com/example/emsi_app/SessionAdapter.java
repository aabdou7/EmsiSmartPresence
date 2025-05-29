package com.example.emsi_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private List<Session> sessions;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public SessionAdapter(List<Session> sessions) {
        this.sessions = sessions;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        
        if (session.getTimestamp() != null) {
            holder.dateText.setText(dateFormat.format(session.getTimestamp().toDate()));
            holder.timeText.setText(timeFormat.format(session.getTimestamp().toDate()));
        }
        
        holder.deviceText.setText(session.getDeviceInfo());
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public void updateSessions(List<Session> newSessions) {
        this.sessions = newSessions;
        notifyDataSetChanged();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;
        TextView timeText;
        TextView deviceText;

        SessionViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.session_date);
            timeText = itemView.findViewById(R.id.session_time);
            deviceText = itemView.findViewById(R.id.session_device);
        }
    }
} 