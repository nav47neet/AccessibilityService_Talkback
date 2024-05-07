package com.example.accessibilityservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private TextView tvContent;
    private Button btnReadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvContent = findViewById(R.id.tvContent);
        btnReadFile = findViewById(R.id.btnReadFile);

        btnReadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
            }
        });

    }
    private void readFile() {
        // Read file content
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.sample);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileContent = contentBuilder.toString();

        // Display content in TextView
        tvContent.setText(fileContent);

        // Speak the content using TalkBack service
        speakWithTalkBack(fileContent);
    }

    private void speakWithTalkBack(String text) {
        Intent intent = new Intent(MainActivity.this, TalkBack.class);
        intent.putExtra(TalkBack.EXTRA_SPEAK, text);
        startService(intent);
    }

}