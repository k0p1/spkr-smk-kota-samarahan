package com.example.spkr;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spkr.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPassword extends AppCompatActivity {
    private String check = "";
    private TextView pageTitle, titleQuestions;
    private EditText phoneNumber, question1;
    private Button verifyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        pageTitle = findViewById(R.id.page_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        question1 = findViewById(R.id.question_1);
        verifyButton = findViewById(R.id.verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumber.setVisibility(View.GONE);

        if (check.equals("settings")) {
            pageTitle.setText("SetSecurityQuestion");
            titleQuestions.setText("Please Answer the question below");
            verifyButton.setText("Set");

            displayPreviousAnswers();

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswers();

                }
            });

        } else if (check.equals("login")) {

            phoneNumber.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    verifyUser();
                }
            });
        }
    }

    private void setAnswers() {

        String answer1 = question1.getText().toString().toLowerCase();

        if (question1.equals("")) {

            Toast.makeText(ResetPassword.this, "Please Answer the question", Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("answer1", answer1);

            ref.child("Security Question").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        Toast.makeText(ResetPassword.this, "Successful, answer the question", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ResetPassword.this, Home.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void displayPreviousAnswers() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    final String ans1 = dataSnapshot.child("answer1").getValue().toString();

                    question1.setText(ans1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void verifyUser() {

        String phone = phoneNumber.getText().toString();
        String answer1 = question1.getText().toString().toLowerCase();

        if (!phone.equals("") && !answer1.equals("")) {

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        if (dataSnapshot.hasChild("Security Question")) {

                            String ans1 = dataSnapshot.child("Security Question").child("answer1").getValue().toString();

                            if (!ans1.equals(answer1)) {

                                Toast.makeText(ResetPassword.this, "Wrong answer", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                                builder.setTitle("New Password");

                                final EditText newPassword = new EditText(ResetPassword.this);
                                newPassword.setHint("Write New Password Here");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if (!newPassword.getText().toString().equals("")) {

                                            ref.child("password")
                                                    .setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(ResetPassword.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ResetPassword.this,Login.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        dialog.cancel();
                                        finish();
                                    }
                                });

                                builder.show();

                            }
                        } else {

                            Toast.makeText(ResetPassword.this, "You have not set the security question.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{

                        Toast.makeText(ResetPassword.this, "This phone number is not exist.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(this, "Please complete the form.",Toast.LENGTH_SHORT).show();
        }
    }
}