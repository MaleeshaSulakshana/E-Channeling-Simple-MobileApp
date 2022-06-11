package com.example.e_channelling;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_channelling.Constructors.AddAppointment;
import com.example.e_channelling.Utiles.EmailValidator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class AddAppointmentActivity extends AppCompatActivity {

    private String timeSlot, doctorId, txtDoctorName, date, txtDoctorCategory, state;
    private DatabaseReference ref;
    private TextView doctorName, doctorStatusPosition, doctorCategory, appointmentTime, appointmentDate;
    private TextInputLayout fname, lname, mobileNumber, email;
    private Button btnAdd, btnCancel;
    int randomNumber = 00000000;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView doctorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

//        Get put extra values
        Intent intent = getIntent();
        timeSlot = intent.getStringExtra("timeSlot");
        doctorId = intent.getStringExtra("doctorId");
        state = intent.getStringExtra("state");

//        Get ui component
        doctorName = (TextView) findViewById(R.id.doctorName);
        doctorStatusPosition = (TextView) findViewById(R.id.doctorStatusPosition);
        doctorCategory = (TextView) findViewById(R.id.doctorCategory);
        appointmentTime = (TextView) findViewById(R.id.appointmentTime);
        appointmentDate = (TextView) findViewById(R.id.appointmentDate);

        fname = (TextInputLayout) findViewById(R.id.fname);
        lname = (TextInputLayout) findViewById(R.id.lname);
        mobileNumber = (TextInputLayout) findViewById(R.id.mobileNumber);
        email = (TextInputLayout) findViewById(R.id.email);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        doctorImage = (ImageView) findViewById(R.id.doctorImage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Get today date
        date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

//        Show detail
        appointmentTime.setText("Time : "+timeSlot);
        appointmentDate.setText("Date : "+date);
        showDoctorDetails();

//        For cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

//        For add button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAppointment();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

//    Method for show doctor details
    private void showDoctorDetails()
    {
        // Read from the database
        ref = FirebaseDatabase.getInstance().getReference("doctors/"+doctorId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    txtDoctorName = dataSnapshot.child("name").getValue().toString();
                    txtDoctorCategory= dataSnapshot.child("category").getValue().toString();
                    doctorName.setText(dataSnapshot.child("name").getValue().toString());
                    doctorStatusPosition.setText(dataSnapshot.child("status").getValue().toString());
                    doctorCategory.setText("Specialization : "+dataSnapshot.child("category").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Show doctor profile image
        storageReference.child("Profile_Pictures/" + doctorId+".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(doctorImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

//    Method for cancel
    private void cancel()
    {
        Intent intent = new Intent(AddAppointmentActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

//    Method for add appointment
    @RequiresApi()
    private void addAppointment()
    {
        String sfname = fname.getEditText().getText().toString();
        String slname = lname.getEditText().getText().toString();
        String smobile = mobileNumber.getEditText().getText().toString();
        String semail = email.getEditText().getText().toString();

//        Check fields is empty
        if (sfname.isEmpty() || slname.isEmpty() || smobile.isEmpty() || semail.isEmpty()){
            Toast.makeText(AddAppointmentActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();

//            Validate email
        } else if (!EmailValidator.isValidEmail(semail)){
            Toast.makeText(AddAppointmentActivity.this, "Please check your email pattern!", Toast.LENGTH_SHORT).show();

//            validate mobile number length
        } else if (smobile.length() != 10) {
            Toast.makeText(AddAppointmentActivity.this, "Please check your mobile number!", Toast.LENGTH_SHORT).show();

//            Insert data to firebase
        } else {
//            Generate random number
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                randomNumber = ThreadLocalRandom.current().nextInt(100000000, 999999999);
            }

            String dateTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            String random = "Appointment"+dateTime+String.valueOf(randomNumber);

            ref = FirebaseDatabase.getInstance().getReference("appointment/"+random);
            AddAppointment addAppointment = new AddAppointment(sfname, slname, semail, date, timeSlot, doctorId, smobile);
            ref.setValue(addAppointment);

            ref = FirebaseDatabase.getInstance().getReference("doctors/"+doctorId+"/"+state);
            ref.child(timeSlot).setValue("booked");

//            Clear text fields
            clearFields();

            Toast.makeText(AddAppointmentActivity.this, "Please wait ...!", Toast.LENGTH_SHORT).show();

//            Make delay
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

//                Navigate to details page
                Intent intent = new Intent(AddAppointmentActivity.this, ViewAppointmentDetailsActivity.class);
                intent.putExtra("appointmentId", random);
                intent.putExtra("doctorName", txtDoctorName);
                intent.putExtra("doctorCategory", txtDoctorCategory);

                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
            }, 100);

        }

    }

//    Method for clear text fields
    private void clearFields()
    {
        fname.getEditText().setText("");
        lname.getEditText().setText("");
        mobileNumber.getEditText().setText("");
        email.getEditText().setText("");
    }
}