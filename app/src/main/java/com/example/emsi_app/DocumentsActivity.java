package com.example.emsi_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private ListView listViewCours;
    private ListView listViewTPs;
    private ArrayAdapter<String> adapterCours;
    private ArrayAdapter<String> adapterTPs;
    private List<String> coursList;
    private List<String> tpsList;
    private static final int PICK_DOCUMENT_REQUEST = 1;
    private String currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        listViewCours = findViewById(R.id.listViewCours);
        listViewTPs = findViewById(R.id.listViewTPs);
        MaterialButton btnLoadCours = findViewById(R.id.btnLoadCours);
        MaterialButton btnLoadTPs = findViewById(R.id.btnLoadTPs);

        coursList = new ArrayList<>();
        tpsList = new ArrayList<>();

        adapterCours = new ArrayAdapter<>(this, R.layout.document_item, R.id.documentName, coursList);
        adapterTPs = new ArrayAdapter<>(this, R.layout.document_item, R.id.documentName, tpsList);

        listViewCours.setAdapter(adapterCours);
        listViewTPs.setAdapter(adapterTPs);

        listViewCours.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = coursList.get(position);
            openDocument("cours", fileName);
        });

        listViewTPs.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = tpsList.get(position);
            openDocument("tps", fileName);
        });

        listViewCours.setOnItemLongClickListener((parent, view, position, id) -> {
            String fileName = coursList.get(position);
            showDeleteDialog("cours", fileName, position);
            return true;
        });

        listViewTPs.setOnItemLongClickListener((parent, view, position, id) -> {
            String fileName = tpsList.get(position);
            showDeleteDialog("tps", fileName, position);
            return true;
        });

        loadDocuments();

        btnLoadCours.setOnClickListener(v -> {
            currentCategory = "cours";
            openDocumentPicker();
        });

        btnLoadTPs.setOnClickListener(v -> {
            currentCategory = "tps";
            openDocumentPicker();
        });
    }

    private void openDocument(String category, String fileName) {
        try {
            File file = new File(getFilesDir() + "/" + category + "/" + fileName);
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".provider",
                        file);

                String mimeType = getMimeType(fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Essayer d'abord avec le type MIME spécifique
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    // Si aucun gestionnaire spécifique n'est trouvé, essayer avec un type générique
                    intent.setDataAndType(uri, "*/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Aucune application ne peut ouvrir ce type de fichier", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Le fichier n'existe plus", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'ouverture du fichier: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteDialog(String category, String fileName, int position) {
        new AlertDialog.Builder(this)
            .setTitle("Supprimer le document")
            .setMessage("Voulez-vous vraiment supprimer ce document ?")
            .setPositiveButton("Oui", (dialog, which) -> {
                File file = new File(getFilesDir() + "/" + category + "/" + fileName);
                if (file.delete()) {
                    if (category.equals("cours")) {
                        coursList.remove(position);
                        adapterCours.notifyDataSetChanged();
                    } else {
                        tpsList.remove(position);
                        adapterTPs.notifyDataSetChanged();
                    }
                    Toast.makeText(this, "Document supprimé avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Non", null)
            .show();
    }

    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mpeg";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            case "html":
            case "htm":
                return "text/html";
            case "xml":
                return "application/xml";
            case "json":
                return "application/json";
            default:
                return "*/*";
        }
    }

    private void loadDocuments() {
        String coursPath = getFilesDir() + "/cours";
        String tpsPath = getFilesDir() + "/tps";

        File coursDir = new File(coursPath);
        File tpsDir = new File(tpsPath);

        coursList.clear();
        tpsList.clear();

        if (coursDir.exists() && coursDir.listFiles() != null) {
            for (File file : coursDir.listFiles()) {
                if (file.isFile()) {
                    coursList.add(file.getName());
                }
            }
        }

        if (tpsDir.exists() && tpsDir.listFiles() != null) {
            for (File file : tpsDir.listFiles()) {
                if (file.isFile()) {
                    tpsList.add(file.getName());
                }
            }
        }

        adapterCours.notifyDataSetChanged();
        adapterTPs.notifyDataSetChanged();

        if (coursList.isEmpty()) {
            Toast.makeText(this, "Aucun cours disponible", Toast.LENGTH_SHORT).show();
        }
        if (tpsList.isEmpty()) {
            Toast.makeText(this, "Aucun TP disponible", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    saveDocument(uri, currentCategory);
                }
            }
        }
    }

    private void saveDocument(Uri uri, String category) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String fileName = getFileName(uri);
            File outputDir = new File(getFilesDir(), category);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File outputFile = new File(outputDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();
            Toast.makeText(this, "Document enregistré avec succès", Toast.LENGTH_SHORT).show();
            loadDocuments();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur lors de l'enregistrement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try {
                String[] projection = {android.provider.MediaStore.MediaColumns.DISPLAY_NAME};
                android.database.Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(0);
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
} 