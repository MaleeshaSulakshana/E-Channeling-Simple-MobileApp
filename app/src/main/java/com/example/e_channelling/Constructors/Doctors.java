package com.example.e_channelling.Constructors;

public class Doctors {

    String doctorName, doctorId, doctorStatus;

    public Doctors(String doctorName, String doctorId, String doctorStatus) {
        this.doctorName = doctorName;
        this.doctorId = doctorId;
        this.doctorStatus = doctorStatus;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorStatus() {
        return doctorStatus;
    }
}
