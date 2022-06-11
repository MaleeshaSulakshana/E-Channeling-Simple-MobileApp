package com.example.e_channelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_channelling.Constructors.AddAppointment;
import com.example.e_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ThreadLocalRandom;

public class ViewAppointmentDetailsActivity extends AppCompatActivity {

    private String appointmentId, txtDoctorName, txtDoctorCategory;
    private DatabaseReference ref;
    private TextView doctorName, doctorCategory, appointmentTime, appointmentDate;
    private TextInputLayout patientFName, patientLName, patientMobileNumber, patientEmail;
    private Button btnEdit, btnDelete, btnYes, btnNo;
    private Dialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_details);

//        Get put extra values
        Intent intent = getIntent();
        appointmentId = intent.getStringExtra("appointmentId");
        txtDoctorName = intent.getStringExtra("doctorName");
        txtDoctorCategory = intent.getStringExtra("doctorCategory");

//        Get ui component
        doctorName = (TextView) findViewById(R.id.doctorName);
        doctorCategory = (TextView) findViewById(R.id.doctorCategory);
        appointmentTime = (TextView) findViewById(R.id.appointmentTime);
        appointmentDate = (TextView) findViewById(R.id.appointmentDate);

        patientFName = (TextInputLayout) findViewById(R.id.patientFName);
        patientLName = (TextInputLayout) findViewById(R.id.patientLName);
        patientMobileNumber = (TextInputLayout) findViewById(R.id.patientMobileNumber);
        patientEmail = (TextInputLayout) findViewById(R.id.patientEmail);

        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        deleteDialog = new Dialog(ViewAppointmentDetailsActivity.this);
        deleteDialog.setContentView(R.layout.delete_dialog_box);

//        Show details
        doctorName.setText("Doctor : "+txtDoctorName);
        doctorCategory.setText("Specialization : "+txtDoctorCategory);
        showAppointmentDetails();


//        For edit button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAppointmentDetails();
            }
        });

//        For delete button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointmentDetails();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show appointment details
    private void showAppointmentDetails()
    {
        // Read from the database
        ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    patientFName.getEditText().setText(dataSnapshot.child("firstName").getValue().toString());
                    patientLName.getEditText().setText(dataSnapshot.child("lastName").getValue().toString());
                    patientMobileNumber.getEditText().setText(dataSnapshot.child("mobileNumber").getValue().toString());
                    patientEmail.getEditText().setText(dataSnapshot.child("email").getValue().toString());

                    appointmentTime.setText("Time : "+dataSnapshot.child("time").getValue().toString());
                    appointmentDate.setText("Date : "+dataSnapshot.child("date").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    Method for update appointment
    private void editAppointmentDetails()
    {
        String sfname = patientFName.getEditText().getText().toString();
        String slname = patientLName.getEditText().getText().toString();
        String smobile = patientMobileNumber.getEditText().getText().toString();
        String semail = patientEmail.getEditText().getText().toString();

//        Check fields is empty
        if (sfname.isEmpty() || slname.isEmpty() || smobile.isEmpty() || semail.isEmpty()){
            Toast.makeText(ViewAppointmentDetailsActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();

//            Validate email
        } else if (!EmailValidator.isValidEmail(semail)){
            Toast.makeText(ViewAppointmentDetailsActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//            validate mobile number length
        } else if (smobile.length() != 10) {
            Toast.makeText(ViewAppointmentDetailsActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

//            Update data in firebase
        } else {
            ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
            ref.child("firstName").setValue(sfname);
            ref.child("lastName").setValue(slname);
            ref.child("mobileNumber").setValue(smobile);
            ref.child("email").setValue(semail);

            Toast.makeText(ViewAppointmentDetailsActivity.this, "Appointment updated successful!", Toast.LENGTH_SHORT).show();

        }

    }

//    Method for remove appointment from firebase
    private void deleteAppointmentDetails()
    {
        deleteDialog.show();

        btnYes = (Button) deleteDialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = FirebaseDatabase.getInstance().getReference("appointment/"+appointmentId);
                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(ViewAppointmentDetailsActivity.this, "Appointment delete successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewAppointmentDetailsActivity.this, "Appointment delete not successful!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

//                Navigate to main activity page
                Intent intent = new Intent(ViewAppointmentDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        btnNo = (Button) deleteDialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.dismiss();
            }
        });

    }

}