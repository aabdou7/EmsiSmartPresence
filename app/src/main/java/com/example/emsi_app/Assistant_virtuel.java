package com.example.emsi_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Assistant_virtuel extends AppCompatActivity {

    private final String API_KEY = "AIzaSyDkhpjBui3zHy385AW-fXZy0UvnWoRmMgw";
    private TextView txtResponse;
    private EditText editMessage;
    private Button btnSend;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_assistant);

        // Configuration du client OkHttp avec timeout
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Récupération des Vues du Layout
        txtResponse = findViewById(R.id.geminianswer);             
        editMessage = findViewById(R.id.prompt);
        btnSend = findViewById(R.id.btnSend);

        // Gestion d'événement du click sur le bouton send
        btnSend.setOnClickListener(v -> {
            String userMessage = editMessage.getText().toString();
            if (!userMessage.isEmpty()) {
                btnSend.setEnabled(false); // Désactive le bouton pendant la requête
                sendMessageToGemini(userMessage);
            } else {
                Toast.makeText(this, "Veuillez entrer un message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToGemini(String message) {
        JSONObject json = new JSONObject();

        try {
            // Construction du corps JSON à envoyer à l'API
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", message);
            JSONObject contentItem = new JSONObject();
            contentItem.put("parts", new JSONArray().put(part));
            json.put("contents", new JSONArray().put(contentItem));
        } catch (JSONException e) {
            handleError("Erreur lors de la création du JSON: " + e.getMessage());
            return;
        }

        // Création du RequestBody
        RequestBody body = RequestBody.create(json.toString(), JSON);

        // URL de l'API Google Gemini
        String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

        // Création de la requête HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        // Exécution de la requête dans un thread séparé
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : null;

                if (response.isSuccessful() && responseBody != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        String text = candidates
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> {
                            txtResponse.setText(text);
                            btnSend.setEnabled(true);
                        });
                    } catch (JSONException e) {
                        handleError("Erreur lors du parsing de la réponse: " + e.getMessage());
                    }
                } else {
                    handleError("Erreur du serveur: " + (responseBody != null ? responseBody : "Réponse vide"));
                }
            } catch (IOException e) {
                handleError("Erreur de connexion: " + e.getMessage());
            }
        }).start();
    }

    private void handleError(String errorMessage) {
        runOnUiThread(() -> {
            txtResponse.setText(errorMessage);
            btnSend.setEnabled(true);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        });
    }
}
