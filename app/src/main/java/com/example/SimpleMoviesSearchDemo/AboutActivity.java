package com.example.SimpleMoviesSearchDemo;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView linkText = findViewById(R.id.AboutDescription);
        linkText.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
