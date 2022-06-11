package com.example.e_channelling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout search;
    private ListView categoriesList;
    private DatabaseReference ref;
    private ArrayList<String> arrayList = new ArrayList<>();
    private Button btnCovid;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Get ui component
        search = (TextInputLayout) findViewById(R.id.search);
        categoriesList = (ListView) findViewById(R.id.categoriesList);
        btnCovid = (Button) findViewById(R.id.btnCovid);

//        Call method for show data on list view
        showCategories();

        btnCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Ope covid 19 page
                Uri uri = Uri.parse("https://www.hpb.health.gov.lk/en");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                get touched value in list view
                String value = ((TextView) view).getText().toString();

//                Navigate to doctors activity
                Intent myIntent = new Intent(MainActivity.this, DoctorsViewActivity.class);
                myIntent.putExtra("category", value);
                startActivity(myIntent);
            }
        });

//        For search
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MainActivity.this.arrayAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

//    Show categories on list view
    private void showCategories()
    {
        arrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item, arrayList);
        categoriesList.setAdapter(arrayAdapter);

        ref = FirebaseDatabase.getInstance().getReference("doctorsCategories/");
        // Read from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String status = snapshot.child("status").getValue().toString();

                        if (status.equals("on")){
                            arrayList.add(snapshot.getKey().toString());
                        }

                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}