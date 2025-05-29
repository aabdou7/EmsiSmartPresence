package com.example.emsi_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {
    private List<DocumentItem> documents;
    private Context context;

    public DocumentsAdapter(List<DocumentItem> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocumentItem document = documents.get(position);
        holder.tvFileName.setText(document.getName());
        holder.tvFileSize.setText(document.getType());

        // Cliquer sur l'élément pour ouvrir le document
        holder.itemView.setOnClickListener(v -> {
            String path = document.getUrl();
            if (path != null && !path.isEmpty()) {
                openDocument(path);
            } else {
                Toast.makeText(context, "Chemin du document non disponible", Toast.LENGTH_SHORT).show();
            }
        });

        // Cliquer sur l'icône pour télécharger
        holder.btnDownload.setOnClickListener(v -> {
            String path = document.getUrl();
            if (path != null && !path.isEmpty()) {
                downloadDocument(path, document.getName());
            } else {
                Toast.makeText(context, "Chemin du document non disponible", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDocument(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/pdf"); // Adjust MIME type as needed
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Document not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Impossible d'ouvrir le document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadDocument(String path, String fileName) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "application/pdf"); // Adjust MIME type as needed
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "Téléchargement démarré", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Document not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Impossible de télécharger le document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateDocuments(List<DocumentItem> newDocuments) {
        this.documents = newDocuments;
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        TextView tvFileSize;
        ImageButton btnDownload;

        DocumentViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
} 