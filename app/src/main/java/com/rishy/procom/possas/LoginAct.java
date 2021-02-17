package com.rishy.procom.possas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class LoginAct extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;
    private DatabaseReference mDatabase;





    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private void saveUserInfo(final String userId, final String email) {
        final DatabaseReference usersRef = mRootRef.child("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    usersRef.setValue(new User(email,"customer",0.0,"Available",0.0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void checkEmail() {

        FirebaseUser user = getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            // email verified ...


            startActivity(new Intent(LoginAct.this, MainActivity.class));

        } else {
            // error : email not verified ...
            Toast.makeText(LoginAct.this, "Email Not Verified. Verifiy your Email to get access" ,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference();





        // set the view now
        setContentView(R.layout.reallogin);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();


        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Get Firebase auth instance

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAct.this, LoginRegist.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginAct.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }






                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginAct.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError("Password too short, enter minimum 6 characters!");
                                    } else {
                                        Toast.makeText(LoginAct.this, "Wrong Password", Toast.LENGTH_LONG).show();
                                        //check this out
                                    }
                                } else {
                                    checkEmail();
                                    saveUserInfo(currentFirebaseUser.getUid(),email);
                                }

                            }
                        });
            }


        });
    }
}