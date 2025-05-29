package com.example.emsi_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.titleText.setText(message.getTitle());
        holder.contentText.setText(message.getContent());
        holder.dateText.setText(message.getDate());

        // Changer la couleur du titre selon le type de message
        if ("important".equals(message.getType())) {
            holder.titleText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else if ("announcement".equals(message.getType())) {
            holder.titleText.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView contentText;
        TextView dateText;

        MessageViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.titleTextView);
            contentText = view.findViewById(R.id.contentTextView);
            dateText = view.findViewById(R.id.dateTextView);
        }
    }
}