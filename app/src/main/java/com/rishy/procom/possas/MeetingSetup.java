package com.rishy.procom.possas;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.compare;
import static java.lang.Double.parseDouble;

public class MeetingSetup extends AppCompatActivity {

    private Button fixm;
    private DatabaseReference mDatabase;

    private EditText inpName, inpPhnum;
    private Spinner inptype, inpLoc;


    public String employeekey;
    public String employeenumber;
    public String employeeemail;
    public String clientemail;
    public String employeename;
    public double employeerating;
    public double defrat = 0.0;

    public String name;
    public String location;
    public String typereq;
    public String phnum;

    public EditText details;


    GMailSender sender;


    AlertDialog b;
    AlertDialog.Builder dialogBuilder;



    public void ShowProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(MeetingSetup.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progressbar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog(){

        b.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.meetingsetup);
        fixm = (Button)findViewById(R.id.fix);


        inpName = (EditText) findViewById(R.id.name);
        inpPhnum = (EditText)findViewById(R.id.number);
        inpLoc = (Spinner) findViewById(R.id.spinnerlocation);
        inptype = (Spinner) findViewById(R.id.spinnerSAS);
        details = (EditText) findViewById(R.id.Detail);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unfortunately There are no employees free right now. " +
                "We will contact you on the number you have submitted when an employee is free!")
                .setTitle("Employees Unavailable");

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            public void onClick(DialogInterface dialog, int id) {
                sender = new GMailSender("procom.rane2018@gmail.com", "dunlop123");
                // Add subject, Body, your mail Id, and receiver mail Id.
                String email = currentUser.getEmail();
                String receivers1 = email + "," + "procom.rane2018@gmail.com";
                String bodyq = "Unfortunately There are no employees free right now. " +
                        "We will contact you on the number you have submitted when an employee is free! ";
                try {
                    sender.sendMail("No Employee Free", bodyq, "procom.rane2018@gmail.com", receivers1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
                finish();
            }
        });



        final AlertDialog dialog = builder.create();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.

                Builder().permitAll().build();



        StrictMode.setThreadPolicy(policy);


        fixm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                mDatabase.child("users")
                        .orderByChild("Availability")
                        .equalTo("Available")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                                name = inpName.getText().toString().trim();
                                phnum = inpPhnum.getText().toString().trim();
                                location = inpLoc.getSelectedItem().toString().trim();
                                typereq = inptype.getSelectedItem().toString().trim();
                                final String problemdet = details.getText().toString().trim();
                                updateUserInfo(name,phnum,location,typereq);



                                mDatabase.child("users").orderByChild("Employee Type").equalTo(typereq).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot childSnapshot) {

                                        String nametext ;
                                        String emailtext ;
                                        String numbertext ;
                                        String ratingtext ;




                                        for (DataSnapshot loopSnapshot: childSnapshot.getChildren()) {
                                            if(loopSnapshot.child("Rating").getValue(double.class)>defrat ) {

                                                    if (loopSnapshot.child("Availability").getValue(String.class).equals("Available")) {
                                                        employeename = loopSnapshot.child("Name").getValue(String.class);
                                                        employeeemail = loopSnapshot.child("email").getValue(String.class);
                                                        employeenumber = loopSnapshot.child("Phone Number").getValue(String.class);
                                                        employeerating = loopSnapshot.child("Rating").getValue(double.class);
                                                        Log.e(employeeemail, employeename);

                                                        nametext = "Employee Name " + employeename;
                                                        emailtext = "Email " + employeeemail;
                                                        numbertext = "Phone Number " + employeenumber;
                                                        ratingtext = "Rating " + employeerating;

                                                        defrat = employeerating;


                                                        setContentView(R.layout.employeeselectionsuccessful);

                                                        final Button okayexit = (Button) findViewById(R.id.okayexit);
                                                        TextView name2 = (TextView) findViewById(R.id.name2);
                                                        TextView number2 = (TextView) findViewById(R.id.number2);
                                                        TextView email2 = (TextView) findViewById(R.id.email2);
                                                        TextView rating2 = (TextView) findViewById(R.id.rating2);


                                                        //get chatting using sms or whatsapp make contact button
                                                        //send an email to the employee and the user with all the detail


                                                        name2.setText(nametext);
                                                        number2.setText(numbertext);
                                                        email2.setText(emailtext);
                                                        rating2.setText(ratingtext);

                                                        mDatabase.child("users").child(currentUser.getUid()).child("Availability").setValue("Not Available");
                                                        mDatabase.child("users").child(currentUser.getUid()).child("Matched Usernumber").setValue(employeenumber);

                                                        final String smsnumber = employeenumber;


                                                            mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    clientemail = dataSnapshot.child("email").getValue(String.class);
                                                                    name = dataSnapshot.child("Name").getValue(String.class);
                                                                    final String clientreq = dataSnapshot.child("Requirement").getValue(String.class);
                                                                    final String clientlocation = dataSnapshot.child("Location").getValue(String.class);



                                                                    okayexit.setOnClickListener(new View.OnClickListener() {

                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            ShowProgressDialog();

                                                                            try {



                                                                                sender = new GMailSender("procom.rane2018@gmail.com", "dunlop123");
                                                                                // Add subject, Body, your mail Id, and receiver mail Id.

                                                                                Log.e(clientemail, employeeemail);
                                                                                String receivers1 = clientemail + "," + employeeemail;
                                                                                String body = "Your meeting has been setup! Employee is " + employeename + "Client is" + name + " Here are the details of the problem" + problemdet +
                                                                                        "The Location of the same is:" + clientlocation + "The Meeting Type is:" + clientreq;

                                                                                sender.sendMail("Meeting Set", body, "procom.rane2018@gmail.com", receivers1);


                                                                                Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();


                                                                            } catch (Exception ex) {


                                                                            }

                                                                            HideProgressDialog();

                                                                            finish();

                                                                        }
                                                                    });


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });


                                                    }

                                            }

                                            try {
                                                Thread.sleep(5000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            mDatabase.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(!dataSnapshot.child("Matched Usernumber").exists())
                                                    {
                                                        dialog.show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });


                                        }

                                        //how to put thzis if statement







                                        // put validation (xx-xx-xxxx) while entering phone number
                                        // make sure admin is the last employee in the list
                                        mDatabase.child("users")
                                                .orderByChild("Name")
                                                .equalTo(employeename)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                                            employeekey = childSnapshot.getKey();
                                                            mDatabase.child("users").child(employeekey).child("Availability").setValue("Not Available");
                                                            mDatabase.child("users").child(employeekey).child("Location").setValue(location);
                                                            mDatabase.child("users").child(employeekey).child("Requirement").setValue(typereq);
                                                            mDatabase.child("users").child(employeekey).child("Matched Usernumber").setValue(phnum);
                                                            Log.e("HELLO", employeekey);

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });



                                    }




                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

//
//                                mDatabase.child("users").child(currentUser.getUid()).child("Availability").setValue("Not Available");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



            }
        });


    }





////                                }
//////                                else{
//////                                    employeekey=admin
//////                                }
////                            }
//////                            else{
//////                                    employeekey=admin try again
//////                                }
////                        }
//
//
//                }
//            }

        //validation to be done


    private void updateUserInfo(String name , String phnum , String location, String typereq) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        mDatabase.child("users").child(currentUser.getUid()).child("Name").setValue(name);
        mDatabase.child("users").child(currentUser.getUid()).child("Location").setValue(location);
        mDatabase.child("users").child(currentUser.getUid()).child("Phone Number").setValue(phnum);
        mDatabase.child("users").child(currentUser.getUid()).child("Requirement").setValue(typereq);




    }





}

