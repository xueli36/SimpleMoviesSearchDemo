package com.example.SimpleMoviesSearchDemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ConfigurationActivity extends AppCompatActivity {

    Button btnConfigure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        btnConfigure = findViewById(R.id.btnConfigure);

        btnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data
                try {
                    // Get user input TMDB API Key
                    TextView queryText = findViewById(R.id.APIKeyTextView);
                    final String inputQueryString = queryText.getText().toString();

                    // Encrypt TMDB API Key to be stored securely in the app
                    String key = "Bar12345Bar12345";

                    // Create key and cipher
                    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");

                    byte[] inputByte = inputQueryString.getBytes("UTF-8");

                    // Encrypt the text
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    String encrypted = new String(Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT));

                    Intent intent = new Intent();

                    intent.putExtra("EncryptedAPIKey", encrypted);
                    intent.putExtra("EncryptionKey", key);

                    setResult(RESULT_OK, intent);

                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
