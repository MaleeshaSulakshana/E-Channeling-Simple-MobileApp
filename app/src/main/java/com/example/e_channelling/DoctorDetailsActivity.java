package com.example.e_channelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DoctorDetailsActivity extends AppCompatActivity {
    private GridView morningGrid, eveningGrid;
    private List<String> morningSlots=new ArrayList<String>();
    private List<String> eveningSlots=new ArrayList<String>();
    private String doctorId;
    private DatabaseReference ref;
    private TextView doctorName, doctorStatusPosition, doctorCategory;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ImageView doctorImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

//        Get put extra values
        Intent intent = getIntent();
        doctorId = intent.getStringExtra("doctorID");

        morningGrid = (GridView) findViewById(R.id.morningGrid);
        eveningGrid = (GridView) findViewById(R.id.eveningGrid);
        doctorName = (TextView) findViewById(R.id.doctorName);
        doctorStatusPosition = (TextView) findViewById(R.id.doctorStatusPosition);
        doctorCategory = (TextView) findViewById(R.id.doctorCategory);
        doctorImage = (ImageView) findViewById(R.id.doctorImage);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Show data from firebase
        showMorningTimeSlots();
        showEveningTimeSlots();
        showDoctorDetails();


//        Lick item click event
        morningGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(DoctorDetailsActivity.this, AddAppointmentActivity.class);
                intent.putExtra("timeSlot", ((TextView) view).getText().toString());
                intent.putExtra("doctorId", doctorId);
                intent.putExtra("state", "morning");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        eveningGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent intent = new Intent(DoctorDetailsActivity.this, AddAppointmentActivity.class);
                intent.putExtra("timeSlot", ((TextView) view).getText().toString());
                intent.putExtra("doctorId", doctorId);
                intent.putExtra("state", "evening");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

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

//    Show morning time slots
    private void showMorningTimeSlots()
    {
        // Read from the database
        ref = FirebaseDatabase.getInstance().getReference("doctors/"+doctorId+"/morning");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String status = snapshot.getValue().toString();
//                        Get available time slots
                        if (status.equals("available")){
                            String data = snapshot.getKey().toString();
                            morningSlots.add(data);
                        }
                    }
                }
                morningGrid.setAdapter(new ArrayAdapter<String>(DoctorDetailsActivity.this,R.layout.cell,morningSlots));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    Show evening time slots
    private void showEveningTimeSlots()
    {
        // Read from the database
        ref = FirebaseDatabase.getInstance().getReference("doctors/"+doctorId+"/evening");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String status = snapshot.getValue().toString();
//                        Get available time slots
                        if (status.equals("available")){
                            String data = snapshot.getKey().toString();
                            eveningSlots.add(data);
                        }
                    }
                }
                eveningGrid.setAdapter(new ArrayAdapter<String>(DoctorDetailsActivity.this,R.layout.cell,eveningSlots));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}