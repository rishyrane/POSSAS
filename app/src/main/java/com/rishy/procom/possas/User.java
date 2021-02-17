package com.rishy.procom.possas;


import com.google.firebase.database.IgnoreExtraProperties;

public class User {

        public String phNum;
        public String name;
        public String email;
        public String accType;
        public String location;
        public double Rating;
        public String Availability;
        public double Appointments;

        public User() {
                // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }



// need empty contractor

                public User( String email, String accType, double Rating, String Availability, double Appointments) {
                        this.email = email;
                        this.accType = accType;
                        this.Rating = Rating;
                        this.Availability = Availability;
                        this.Appointments = Appointments;

                }


                public String getEmail() {
                        return email;
                }







}
