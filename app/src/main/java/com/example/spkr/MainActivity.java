package com.example.spkr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button clickMe, scanner, viewRecord, summary;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private static final int WRITE_STORAGE_PERMISSION = 2;
    private Class<?> mClss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        clickMe = (Button) findViewById(R.id.btn_clickMe);
        scanner = (Button) findViewById(R.id.btn_scanner);
        viewRecord = (Button) findViewById(R.id.btn_viewRecord);
        summary = (Button) findViewById(R.id.btn_summary);

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Toast.makeText(MainActivity.this, "Going summary page...", Toast.LENGTH_SHORT).show();
                launchSummary();
            }
        });

        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Toast.makeText(MainActivity.this, getApplicationContext().getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Toast.makeText(MainActivity.this, "Opening Scanner...", Toast.LENGTH_SHORT).show();
                launchScanner();
            }
        });

        viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Toast.makeText(MainActivity.this, "Loading records...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RecordView.class);
                startActivity(intent);
            }
        });
    }

    private boolean validate (String text) {
        Pattern p = Pattern.compile("\\d*");
        Matcher m = p.matcher(text);
        return m.matches();
    }
    public void launchScanner() {
        launchActivity(ScannerActivity.class, ZXING_CAMERA_PERMISSION);
    }

    public void launchSummary() {
        launchActivity(SummaryActivity.class, WRITE_STORAGE_PERMISSION);
    }

    public void launchActivity(Class<?> clss, int permission) {
        switch (permission) {
            case 1:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mClss = clss;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                }
                break;

            case 2:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    mClss = clss;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
                }
                break;
        }

        Intent intent = new Intent(this, clss);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                break;

            case WRITE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
}