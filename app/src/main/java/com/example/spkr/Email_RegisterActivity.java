package com.example.spkr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
//import com.google.firebase.quickstart.auth.R;
//import com.google.firebase.quickstart.auth.databinding.ActivityEmailpasswordBinding;

public class Email_RegisterActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private Button btn_login, btn_verify, btn_register, btn_logout, btn_reload, btn_home;
    private EditText edit_email, edit_password, details, status;

    @VisibleForTesting
    public ProgressBar mProgressBar;

    //private ActivityEmailpasswordBinding mBinding;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBinding = ActivityEmailpasswordBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_email_register);
        //setProgressBar(mBinding.progressBar);

        btn_login = (Button) findViewById(R.id.btn_loginEmail);
        btn_register = (Button) findViewById(R.id.btn_registerEmail);
        btn_verify = (Button) findViewById(R.id.btn_verifyAccount);
        btn_logout = (Button) findViewById(R.id.btn_logoutEmail);
        btn_reload = (Button) findViewById(R.id.btn_reload);
        btn_home = (Button) findViewById(R.id.btn_toHome);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        details = (EditText) findViewById(R.id.edit_emailDetails);
        status = (EditText) findViewById(R.id.edit_emailStatus);

        // Buttons
//        mBinding.emailSignInButton.setOnClickListener(this);
//        mBinding.emailCreateAccountButton.setOnClickListener(this);
//        mBinding.signOutButton.setOnClickListener(this);
//        mBinding.verifyEmailButton.setOnClickListener(this);
//        mBinding.reloadButton.setOnClickListener(this);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"To Home clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Register clicked",Toast.LENGTH_SHORT).show();
                createAccount(edit_email.getText().toString(), edit_password.getText().toString());
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Login clicked",Toast.LENGTH_SHORT).show();
                signIn(edit_email.getText().toString(), edit_password.getText().toString());
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Signout clicked",Toast.LENGTH_SHORT).show();
                signOut();
            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Send verification clicked",Toast.LENGTH_SHORT).show();
                sendEmailVerification();
            }
        });

        btn_reload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Reload clicked",Toast.LENGTH_SHORT).show();
                reload();
            }
        });
    }

    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    public void  showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //create dialog with task.getException().getMessage(); invalid credentials or duplicated
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Email_RegisterActivity.this, "Authentication failed.\n"+task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    //Sign in Activity?
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Email_RegisterActivity.this, "Authentication failed.\n"+task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                            // [START_EXCLUDE]
                            //checkForMultiFactorFailure(task.getException());
                            // [END_EXCLUDE]
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            status.setText("Authentication failed, why?");
                        }
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    public void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        btn_verify.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        btn_verify.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(Email_RegisterActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(Email_RegisterActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(Email_RegisterActivity.this,
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "reload", task.getException());
                    Toast.makeText(Email_RegisterActivity.this,
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edit_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edit_email.setError("Email Required.");
            valid = false;
        } else {
            edit_email.setError(null);
        }

        String password = edit_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edit_password.setError("Password Required.");
            valid = false;
        } else {
            edit_password.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            //status.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            status.setText("user email: "+user.getEmail()+"\nuser isEmailed verified: "+user.isEmailVerified());

            //details.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            details.setText("user uid: "+user.getUid());
            //mBinding.emailPasswordButtons.setVisibility(View.GONE);
            btn_verify.setVisibility(View.GONE);
            btn_reload.setVisibility(View.GONE);
            btn_register.setVisibility(View.GONE);
            btn_login.setVisibility(View.GONE);
            btn_home.setVisibility(View.VISIBLE);

            //mBinding.emailPasswordFields.setVisibility(View.GONE);
            edit_password.setVisibility(View.GONE);
            edit_email.setVisibility(View.GONE);

            //mBinding.signedInButtons.setVisibility(View.VISIBLE);

            if (user.isEmailVerified()) {
                btn_verify.setVisibility(View.GONE);
            } else {
                btn_verify.setVisibility(View.VISIBLE);
            }
        } else {
            status.setText("Signed out"); //R.string.signed_out);
            details.setText(null);

            //mBinding.emailPasswordButtons.setVisibility(View.VISIBLE);
            btn_verify.setVisibility(View.VISIBLE);
            btn_reload.setVisibility(View.VISIBLE);
            btn_register.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.VISIBLE);
            btn_home.setVisibility(View.GONE);

            //mBinding.emailPasswordFields.setVisibility(View.VISIBLE);
            edit_password.setVisibility(View.VISIBLE);
            edit_email.setVisibility(View.VISIBLE);

            //mBinding.signedInButtons.setVisibility(View.GONE);
            //btn_login.setVisibility(View.GONE);
        }
    }

//    private void checkForMultiFactorFailure(Exception e) {
//        // Multi-factor authentication with SMS is currently only available for
//        // Google Cloud Identity Platform projects. For more information:
//        // https://cloud.google.com/identity-platform/docs/android/mfa
//        if (e instanceof FirebaseAuthMultiFactorException) {
//            Log.w(TAG, "multiFactorFailure", e);
//            Intent intent = new Intent();
//            MultiFactorResolver resolver = ((FirebaseAuthMultiFactorException) e).getResolver();
//            intent.putExtra("EXTRA_MFA_RESOLVER", resolver);
//            setResult(MultiFactorActivity.RESULT_NEEDS_MFA_SIGN_IN, intent);
//            finish();
//        }
//    }

//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//
//        if (i == R.id.btn_registerEmail) {
//            createAccount(edit_email.getText().toString(), edit_password.getText().toString());
//        } else if (i == R.id.btn_loginEmail) {
//            signIn(edit_email.getText().toString(), edit_password.getText().toString());
//        } else if (i == R.id.btn_logoutEmail) {
//            signOut();
//        } else if (i == R.id.btn_verifyAccount) {
//            sendEmailVerification();
//        } else if (i == R.id.btn_reload) {
//            reload();
//        }
//    }
}
