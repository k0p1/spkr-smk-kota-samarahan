package com.example.spkr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spkr.Model.Users;
import com.example.spkr.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView ForgetPasswordLink;
    private List<Users> usersList = new ArrayList<>();
    private DatabaseReference RootRef;
    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_passwordinput);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me);
        RootRef = FirebaseDatabase.getInstance().getReference().child(parentDbName);
        Paper.init(this);
        initUsersList();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Login.this, ResetPassword.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });
    }

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phone, password);
        }
    }

    public void initUsersList() {
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!usersList.isEmpty()){
                    usersList.clear();
                }

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Users usersData = postSnapshot.getValue(Users.class);
                        usersList.add(usersData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AllowAccessToAccount(final String phone, final String password) {
        boolean userExists = false;

        if (chkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.AdminPhoneKey, phone);
            Paper.book().write(Prevalent.AdminPasswordKey, password);
        }

        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getPhone().equals(phone)) {
                if (usersList.get(i).getPassword().equals(password)) {
                    Toast.makeText(Login.this, "You are already logged in...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Prevalent.currentOnlineUser = usersList.get(i);
                    userExists = true;
                    break;
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(Login.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }

        if(userExists) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        }
    }
}