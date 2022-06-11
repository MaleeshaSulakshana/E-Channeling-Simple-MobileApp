package com.example.e_channelling.Constructors;

public class AddAppointment {

    String FirstName, LastName, Email, Date, Time, DoctorId, MobileNumber;

    public AddAppointment(String firstName, String lastName, String email, String date, String time, String doctorId, String mobileNumber) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        Date = date;
        Time = time;
        DoctorId = doctorId;
        MobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return Email;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getDoctorId() {
        return DoctorId;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }
}
