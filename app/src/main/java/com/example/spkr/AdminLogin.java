package com.example.spkr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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


public class AdminLogin extends AppCompatActivity  {
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private List<Users> usersList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        this.RootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        joinNowButton = (Button) findViewById(R.id.main_joinnowbtn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        initUsersList();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminLogin.this, Login.class);
                startActivity(intent);
            }
        });


        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AdminLogin.this, Register.class);
                startActivity(intent);
            }
        });

        //firebase auth
        //String UserEmailKey = Paper.book().read(Prevalent.UserEmailKey);
        //String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        //ori auth
        String UserEmailKey = Paper.book().read(Prevalent.AdminPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.AdminPasswordKey);

        if (UserEmailKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserEmailKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserEmailKey, UserPasswordKey);

                loadingBar.setTitle("Already Logged in");
                loadingBar.setMessage("Please wait.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
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

    private void AllowAccess(final String phone, final String password)
    {
        boolean userExists = false;
        for (int i = 0; i< usersList.size(); i++) {
            if (usersList.get(i).getPhone().equals(phone)) {
                if (usersList.get(i).getPassword().equals(password)) {
                    Toast.makeText(AdminLogin.this, "You are already logged in...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Prevalent.currentOnlineUser = usersList.get(i);
                    userExists = true;
                    break;
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(AdminLogin.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(AdminLogin.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        if (userExists) {
            Intent intent = new Intent(AdminLogin.this, Home.class);
            startActivity(intent);
        }
    }
}