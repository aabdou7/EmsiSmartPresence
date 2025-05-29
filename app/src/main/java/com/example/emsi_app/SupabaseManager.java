package com.example.emsi_app;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseManager {
    private static final String SUPABASE_URL = "https://wlsvlpogjeefvymtdlhj.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indsc3ZscG9namVlZnZ5bXRkbGhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDgxMTE1NjMsImV4cCI6MjA2MzY4NzU2M30.wO3x7C40IVF89b4pD18xxhLD4dLF_JT3r7CbkdtJ7eg";
    private static final String BUCKET_NAME = "emsi-docs";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Context context;
    private final Gson gson;
    
    public SupabaseManager(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    public interface UploadCallback {
        void onProgress(int progress);
        void onSuccess(String url);
        void onError(String error);
    }

    public interface ListCallback {
        void onSuccess(List<DocumentItem> documents);
        void onError(String error);
    }
    
    public void uploadDocument(Uri fileUri, UploadCallback callback) {
        new Thread(() -> {
            try {
                Log.d("SupabaseManager", "Début de l'upload...");
                // Créer un fichier temporaire
                File tempFile = createTempFileFromUri(fileUri);
                Log.d("SupabaseManager", "Fichier temporaire créé: " + tempFile.getAbsolutePath());
                
                // Uploader le fichier
                String fileName = getFileNameFromUri(fileUri);
                String path = fileName;
                Log.d("SupabaseManager", "Chemin de destination: " + path);
                
                // Créer le corps de la requête
                RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("application/octet-stream"), tempFile))
                    .build();
                
                // Créer la requête
                String uploadUrl = SUPABASE_URL + "/storage/v1/object/" + BUCKET_NAME + "/" + path;
                Log.d("SupabaseManager", "URL d'upload: " + uploadUrl);
                
                Request request = new Request.Builder()
                    .url(uploadUrl)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("apikey", SUPABASE_KEY)
                    .addHeader("Content-Type", "application/octet-stream")
                    .post(requestBody)
                    .build();
                
                // Exécuter la requête
                Log.d("SupabaseManager", "Envoi de la requête...");
                Response response = client.newCall(request).execute();
                Log.d("SupabaseManager", "Réponse reçue. Code: " + response.code());
                
                if (response.isSuccessful()) {
                    // Obtenir l'URL publique
                    String publicUrl = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + path;
                    Log.d("SupabaseManager", "Upload réussi. URL publique: " + publicUrl);
                    
                    // Notifier le succès
                    callback.onSuccess(publicUrl);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Erreur inconnue";
                    Log.e("SupabaseManager", "Erreur lors de l'upload: " + errorBody);
                    callback.onError("Erreur lors de l'upload: " + errorBody);
                }
                
            } catch (Exception e) {
                Log.e("SupabaseManager", "Exception lors de l'upload", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public void listDocuments(ListCallback callback) {
        new Thread(() -> {
            try {
                Log.d("SupabaseManager", "Récupération de la liste des documents...");
                Log.d("SupabaseManager", "URL: " + SUPABASE_URL);
                Log.d("SupabaseManager", "Key: " + SUPABASE_KEY);
                
                // Créer la requête
                String listUrl = SUPABASE_URL + "/storage/v1/object/list/" + BUCKET_NAME;
                Log.d("SupabaseManager", "URL de liste: " + listUrl);
                
                Request request = new Request.Builder()
                    .url(listUrl)
                    .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                    .addHeader("apikey", SUPABASE_KEY)
                    .get()
                    .build();
                
                Log.d("SupabaseManager", "Headers: Authorization=Bearer " + SUPABASE_KEY + ", apikey=" + SUPABASE_KEY);
                
                // Exécuter la requête
                Response response = client.newCall(request).execute();
                Log.d("SupabaseManager", "Réponse reçue. Code: " + response.code());
                
                String responseBody = null;
                if (response.body() != null) {
                    responseBody = response.body().string();
                    Log.d("SupabaseManager", "Réponse body: " + responseBody);
                }
                
                if (response.isSuccessful() && responseBody != null) {
                    JsonArray files = gson.fromJson(responseBody, JsonArray.class);
                    List<DocumentItem> documents = new ArrayList<>();
                    
                    for (JsonElement element : files) {
                        JsonObject file = element.getAsJsonObject();
                        String name = file.get("name").getAsString();
                        String path = name;
                        String url = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + path;
                        
                        documents.add(new DocumentItem(
                            name.replaceAll("_", " ").replaceAll("-", " "),
                            "Document",
                            url
                        ));
                    }
                    
                    callback.onSuccess(documents);
                } else {
                    Log.e("SupabaseManager", "Erreur lors de la récupération: " + responseBody);
                    callback.onError("Erreur lors de la récupération: " + responseBody);
                }
                
            } catch (Exception e) {
                Log.e("SupabaseManager", "Exception lors de la récupération", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }
    
    private File createTempFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload_", null, context.getCacheDir());
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        outputStream.close();
        inputStream.close();
        
        return tempFile;
    }
    
    private String getFileNameFromUri(Uri uri) {
        String result = uri.getLastPathSegment();
        if (result != null) {
            result = result.replaceAll("_", " ").replaceAll("-", " ");
        }
        return result != null ? result : "document_" + System.currentTimeMillis();
    }
} 