package com.rishy.procom.possas;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginRegist extends AppCompatActivity {

    private EditText inputEmail, inputPassword, confpass;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private FirebaseAuth auth;




    private void checkEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // email verified ...
            finish();
        } else {
            // error : email not verified ...
            Toast.makeText(LoginRegist.this, "Email Not Verified. Verifiy your Email to get access" ,
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void sendVerification(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginRegist.this, "Email Verification Sent. Accept The email to get access " + task.getException(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //passwords do not match do something about that

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        confpass = (EditText)findViewById(R.id.conpassword);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginRegist.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginRegist.this, LoginAct.class));

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String conpass = confpass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(conpass)) {
                    Toast.makeText(getApplicationContext(), "REEnter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!conpass.equals(password)){
                    Toast.makeText(getApplicationContext(), "Passwords Do not Match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginRegist.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(LoginRegist.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {

                                } else {Toast.makeText(LoginRegist.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                    sendVerification();
                                    checkEmail();
                                }
                            }
                        });

            }
        });


    }}
