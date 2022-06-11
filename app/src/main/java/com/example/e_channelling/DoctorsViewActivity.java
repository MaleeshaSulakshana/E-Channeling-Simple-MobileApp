package com.example.e_channelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.e_channelling.Adapters.DoctorAdapter;
import com.example.e_channelling.Constructors.Doctors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DoctorsViewActivity extends AppCompatActivity {

    private TextInputLayout searchDoctors;
    private ListView doctorsList;
    private DatabaseReference ref;
    private TextView txtCategory;
    ArrayList<Doctors> arrayList = new ArrayList<>();
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_view);

//        Get put extra values
        Intent intent = getIntent();
        category = intent.getStringExtra("category");

//        Get ui component
        doctorsList = (ListView) findViewById(R.id.doctorsList);
        txtCategory = (TextView) findViewById(R.id.txtCategory);

//        Call method for show data on list view
        showDoctors();
        txtCategory.setText(category);

//        Lick item click event
        doctorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Intent detailsPage = new Intent(DoctorsViewActivity.this, DoctorDetailsActivity.class);
                detailsPage.putExtra("doctorID", arrayList.get(i).getDoctorId());
                startActivity(detailsPage);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

    }

//    Show doctors on list view
    private void showDoctors()
    {
        DoctorAdapter doctorAdapter = new DoctorAdapter(this, R.layout.doctors_view_row, arrayList);
        doctorsList.setAdapter(doctorAdapter);

        ref = FirebaseDatabase.getInstance().getReference("doctors/");
        // Read from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String status = snapshot.child("category").getValue().toString();

                        if (status.equals(category)){
                            arrayList.add(new Doctors(snapshot.child("name").getValue().toString(),snapshot.getKey().toString(),snapshot.child("status").getValue().toString()));
                            doctorAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}