package com.rishy.procom.possas;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button signOut;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Button delbut;
    private Button setmeet;
    private Button delcon;
    private Button smsButton;
    private Button complete;
    private Button endjob;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    Button submitfeed;
    int ratingNumber;


    AlertDialog b;
    AlertDialog.Builder dialogBuilder;

    private CountDownTimer timer;

    String clientnumber;
    String employeenumber;
    String employeeid;
    String clientid;

    Spinner empchoose;
    EditText detailemp;


    GMailSender sender;


    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_help:
                    mTextMessage.setText(R.string.title_help);
                    return true;
                case R.id.navigation_chat:
                    mTextMessage.setText(R.string.title_chat);
                    return true;
                case R.id.navigation_about:
                    mTextMessage.setText(R.string.title_about);
                    return true;
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
            }
            return false;
        }
    };


    public void ShowProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progressbar, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        b = dialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog() {

        b.dismiss();
    }


    public void dynamicButtons() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        mRootRef.child("users").child(currentUser.getUid()).child("Employee Type").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setmeet.setVisibility(View.GONE);

                }

                mRootRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Availability").getValue(String.class).equals("Not Available")) {
                            smsButton.setVisibility(View.VISIBLE);
                            delbut.setVisibility(View.GONE);
                            if (!dataSnapshot.child("Employee Type").exists()) {
                                if (dataSnapshot.child("Matched Usernumber").exists() &&
                                        !dataSnapshot.child("Location").exists()) {
                                    complete.setVisibility(View.VISIBLE);
                                }
                                setmeet.setVisibility(View.GONE);
                            }
                            if (dataSnapshot.child("Employee Type").exists()) {
                                endjob.setVisibility(View.VISIBLE);
                            }
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

        timer.start();

    }

    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Context cContext = getApplicationContext();

        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,}, 1);

        timer = new CountDownTimer(1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
//                ShowProgressDialog();
                try {

                    dynamicButtons();

                } catch (Exception e) {
                    Log.e("Error", "Error: " + e.toString());
                }
//                HideProgressDialog();
            }
        }.start();

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();


        sender = new GMailSender("procom.rane2018@gmail.com", "dunlop123");
//
//
//
//

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginAct.class));
                }
            }
        };


//        //get current user
        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        setmeet = (Button) findViewById(R.id.fixmeet);
        delbut = (Button) findViewById(R.id.delete);
        signOut = (Button) findViewById(R.id.signout);
        complete = (Button) findViewById(R.id.completemeet);
        endjob = (Button) findViewById(R.id.endjob);
        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        smsButton = (Button) findViewById(R.id.opensms);



        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Matched Usernumber").exists()) {
                            String usernumber = dataSnapshot.child("Matched Usernumber").getValue(String.class);
                            Log.e("eyy bhai", usernumber);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", usernumber, null)));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        //for service service report made yes no over email
        //sales business card collected yes no


        //if location = true then make make the meeting button visible


        endjob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setContentView(R.layout.checklist);


//                LocationManager locationManagerNetwork = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                if (ActivityCompat.checkSelfPermission(cContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(cContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                final Location locationBlue = locationManagerNetwork.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                final double latfinal = locationBlue.getLatitude();
//                final double lngfinal = locationBlue.getLongitude();
                    empchoose = (Spinner) findViewById(R.id.spinnerSASemp);
                    detailemp = (EditText) findViewById(R.id.DetailEmp);




                    //email karo

                    Button endit = (Button) findViewById(R.id.endit);

                    endit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            mRootRef.child("users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("Employee Type").exists()) {
                                        clientnumber = dataSnapshot.child("Matched Usernumber").getValue(String.class);
                                        employeenumber = dataSnapshot.child("Phone Number").getValue(String.class);
                                        final String employeeemail = dataSnapshot.child("email").getValue(String.class);
                                        mRootRef.child("users").child(currentUser.getUid()).child("Availability").setValue("Available");
                                        mRootRef.child("users").child(currentUser.getUid()).child("Requirement").removeValue();
                                        mRootRef.child("users").child(currentUser.getUid()).child("Location").removeValue();


                                        mRootRef.child("users").orderByChild("Phone Number").equalTo(clientnumber)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        ShowProgressDialog();
                                                        for(DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                                            clientid = childSnapshot.getKey();
                                                        }
                                                            Log.e(clientid, "hellofromtheptherside");
                                                            mRootRef.child("users").child(clientid).child("Location").removeValue();
                                                            mRootRef.child("users").child(clientid).child("Requirement").removeValue();
                                                            mRootRef.child("users").child(clientid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    final String clientemail = dataSnapshot.child("email").getValue(String.class);
                                                                    Log.e(clientemail, "hello1");
                                                                    Log.e(employeeemail,"hello2");
                                                                    final String empchoose1 = empchoose.getSelectedItem().toString().trim();
                                                                    final String detailemp1 = detailemp.getText().toString().trim();
                                                                        final String receipients = "rishyrane@gmail.com" + "," + employeeemail + "," + clientemail;

                                                                    try {

                                                                        sender.sendMail("Call Report", empchoose1 +detailemp1, "procom.rane2018@gmail.com", receipients);
//                                                                        sender.sendMail("Call Completed","The employee with email:" +employeeemail +",has completed the meeting with" +
//                                                                                        "the client with email:"+clientemail+"at lattitude:"+latfinal+",and longitude:"+lngfinal,
//                                                                                "procom.rane2018@gmail.com","procom.rane2018@gmail.com");
                                                                        HideProgressDialog();

                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();


                                                                    finish();

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

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                    });

                }

            });


        //only show this button to current user
        complete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                setContentView(R.layout.submitlayout);
                Log.e("hello","hi");


                submitfeed = (Button)findViewById(R.id.submit);
                final EditText e_feedback = (EditText)findViewById(R.id.Detail);
                final RatingBar simpleRatingBar = (RatingBar) findViewById(R.id.simpleRatingBar); // initiate a rating bar





                // employee completes work, email sent, employee gets free
                // user rates, rating is updated


                //ifcurrentuser is a user then unlock complete button




                submitfeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                        ratingNumber = (int)simpleRatingBar.getRating();
//send email
                        final String s_feedback = e_feedback.getText().toString().trim();


                        mRootRef.child("users")
                                .child(currentUser.getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        employeenumber = dataSnapshot.child("Matched Usernumber").getValue(String.class);
                                        final String clientemail1 =dataSnapshot.child("email").getValue(String.class);

                                        mRootRef.child("users")
                                                .orderByChild("Phone Number")
                                                .equalTo(employeenumber)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                                            employeeid = childSnapshot.getKey();
                                                            Log.e(employeeid,"hello");

                                                        }


                                                        mRootRef.child("users")
                                                                .child(employeeid)
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                        final String employeeemail1 = dataSnapshot.child("email").getValue(String.class);

                                                                        double appointments = dataSnapshot.child("Appointments").getValue(double.class);
                                                                        int app = (int)appointments;
                                                                        double currentrating = dataSnapshot.child("Rating").getValue(double.class);
                                                                        final double newrating = ((currentrating*appointments)+ratingNumber)/(app+1);
                                                                        mRootRef.child("users").child(employeeid).child("Rating").setValue(newrating);
                                                                        mRootRef.child("users").child(employeeid).child("Appointments").setValue(app+1);
                                                                        mRootRef.child("users").child(employeeid).child("Matched Usernumber").removeValue();
                                                                        mRootRef.child("users").child(currentUser.getUid()).child("Matched Usernumber").removeValue();
                                                                        mRootRef.child("users").child(currentUser.getUid()).child("Availability").setValue("Available");



                                                                        final String receipients1 = "rishyrane@gmail.com" + "," + employeeemail1 + "," + clientemail1;
                                                                        try {
                                                                            ShowProgressDialog();
                                                                            sender.sendMail("Rating and Feedback from: "+clientemail1, s_feedback+"Your Current rating is: "+newrating , "procom.rane2018@gmail.com", receipients1);
                                                                            HideProgressDialog();
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }

                                                                        Toast.makeText(MainActivity.this, "Thanks for the feedback" ,
                                                                                Toast.LENGTH_SHORT).show();
                                                                        finish();

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


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });





                    }

                });
            }

        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();

            }
        });

        delbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.deleteuser);
                delcon = (Button)findViewById(R.id.confirmdelete);

                delcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteacc();
                    }
                });


            }
        });


        setmeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,MeetingSetup.class));
            }
        });


    }


    //sign out method
    public void signOut() {
        auth.signOut();
        finish();
    }

    @Override
    protected void onStart()
    {
        super.onStart();


    }


    public void deleteacc(){
        Log.d("delete account", " deleteAccount");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        delrec(currentUser.getUid());
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("cool","OK! Works fine!");
                    startActivity(new Intent(MainActivity.this, LoginRegist.class));
                } else {
                    Log.w("wrong","Something is wrong!");
                }
            }
        });
    }

    private void delrec(String userid) {
        DatabaseReference userUID =FirebaseDatabase.getInstance().getReference().child("users").child(userid);
        Log.w("wrong","Something is really wrong!");
        userUID.removeValue();
        Log.w("wrong","Something is not wrong!");
        Toast.makeText(this,"User Deleted",Toast.LENGTH_LONG);

    }


//    public boolean getAddress(double lat, double lng, String userloc) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        boolean data = false;
//        try {
//            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
//            Address obj = addresses.get(0);
//            String add = obj.getAddressLine(0);
//            add = add + "\n" + obj.getCountryName();
//            add = add + "\n" + obj.getCountryCode();
//            add = add + "\n" + obj.getAdminArea();
//            add = add + "\n" + obj.getPostalCode();
//            add = add + "\n" + obj.getSubAdminArea();
//            add = add + "\n" + obj.getLocality();
//            add = add + "\n" + obj.getSubThoroughfare();
//
//            Log.v("IGA", "Address" + add);
//
//            if (add.contains(userloc)) {
//                data = true;
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        return data;
//    }

}




