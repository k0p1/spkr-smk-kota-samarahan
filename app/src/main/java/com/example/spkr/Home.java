package com.example.spkr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Home extends AppCompatActivity {

    private ProgressBar progressBar;
    private FloatingActionButton qrScan;
    private Button clickMe, scanner, viewRecord, summary, logout;
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private static final int WRITE_STORAGE_PERMISSION = 2;
    private Class<?> mClss;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        clickMe = (Button) findViewById(R.id.btn_clickMe);
        scanner = (Button) findViewById(R.id.btn_scanner);
        viewRecord = (Button) findViewById(R.id.btn_viewRecord);
        summary = (Button) findViewById(R.id.btn_summary);
        logout = (Button) findViewById(R.id.btn_logout);

        qrScan = findViewById(R.id.fab_qrScanner);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_view, R.id.navigation_qr, R.id.navigation_reports)
                .build();

        qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Opening Scanner...", Toast.LENGTH_SHORT).show();
                launchScanner();
            }
        });

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_qr:
                        Toast.makeText(Home.this, "Opening Scanner...", Toast.LENGTH_SHORT).show();
                        launchScanner();
                        return true;

                    case R.id.navigation_reports:
                        Toast.makeText(Home.this, "Going summary page...", Toast.LENGTH_SHORT).show();
                        launchSummary();
                        return true;

                    case R.id.navigation_view:
                        Toast.makeText(Home.this, "Loading records...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Home.this, LaptopView.class);
                        startActivity(intent);
                        return true;

                    default:
                        Toast.makeText(Home.this, "Not sure what is clicked though...", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mauth.getCurrentUser() != null){
                        mauth.signOut();
                        Intent intent = new Intent(getApplicationContext(), PageLogin.class);
                        startActivity(intent);
                    }
                } catch (NullPointerException ex) {
                    Toast.makeText(Home.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), PageLogin.class);
                    startActivity(intent);
                }
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Toast.makeText(Home.this, "Going summary page...", Toast.LENGTH_SHORT).show();
                launchSummary();
            }
        });

        clickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Toast.makeText(Home.this, Uri.parse(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()).toString(), Toast.LENGTH_LONG).show();
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Toast.makeText(Home.this, "Opening Scanner...", Toast.LENGTH_SHORT).show();
                launchScanner();
            }
        });

        viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Toast.makeText(Home.this, "Loading records...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, LaptopView.class);
                startActivity(intent);
            }
        });
    }

    public void launchScanner() {
        launchActivity(ScannerActivity.class, ZXING_CAMERA_PERMISSION);
        this.onPause();
    }

    public void launchSummary() {
        launchActivity(SummaryActivity.class, WRITE_STORAGE_PERMISSION);
        this.onPause();
    }

    public void launchActivity(Class<?> clss, int permission) {
        mClss = clss;
        Intent intent = new Intent(this, mClss);
        switch (permission) {
            case ZXING_CAMERA_PERMISSION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
                }
                else {
                    startActivity(intent);
                }
                break;

            case WRITE_STORAGE_PERMISSION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION);
                }
                else {
                    startActivity(intent);
                }
                break;
        }
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
                return;


            case WRITE_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant access storage permission to export report", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }
}