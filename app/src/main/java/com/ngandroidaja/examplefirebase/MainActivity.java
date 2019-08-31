package com.ngandroidaja.examplefirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ngandroidaja.examplefirebase.adapter.BioAdapter;
import com.ngandroidaja.examplefirebase.model.Biodata;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    private ArrayList<Biodata> listbiodata;
    private BioAdapter bioAdapter;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_report);
        setSupportActionBar(toolbar);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddData.class);
                startActivity(intent);
            }
        });

        progressDialog = ProgressDialog.show(MainActivity.this,
                null, "Please Wait..."
                , true
                , false);

        databaseReference.child("Biodata").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listbiodata = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Biodata biodata = snapshot.getValue(Biodata.class);
                    Objects.requireNonNull(biodata).setKey(snapshot.getKey());
                    listbiodata.add(biodata);
                    progressDialog.dismiss();
                }
                bioAdapter = new BioAdapter(listbiodata, MainActivity.this);
                recyclerView.setAdapter(bioAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error gan :", databaseError.getMessage());
                progressDialog.dismiss();
            }
        });
    }
}

