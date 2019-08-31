package com.ngandroidaja.examplefirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ngandroidaja.examplefirebase.model.Biodata;

import java.util.List;

public class AddData extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private EditText EdtNama;
    private EditText EdtNotelp;
    private EditText EdtAlamat;
    private EditText EdtKampus;

    private List<Biodata> bios;

    private String nama;
    private String notelp;
    private String alamat;
    private String kampus;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        EdtNama = findViewById(R.id.edt_nama);
        EdtNotelp = findViewById(R.id.edt_notelp);
        EdtAlamat = findViewById(R.id.edt_alamat);
        EdtKampus = findViewById(R.id.edt_kampus);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Biodata");

        Button BtnSave = findViewById(R.id.btn_save);
        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(nama) && isEmpty(notelp) && isEmpty(alamat) && isEmpty(kampus)) {
                    Snackbar.make(findViewById(R.id.btn_save), "Ada data yang kosong", Snackbar.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            EdtNama.getWindowToken(), 0);
                } else {
                    progressDialog = ProgressDialog.show(AddData.this,
                            null, "Please Wait..."
                            , true
                            , false);

                    nama = EdtNama.getText().toString();
                    notelp = EdtNotelp.getText().toString();
                    alamat = EdtAlamat.getText().toString();
                    kampus = EdtKampus.getText().toString();

                    sendData(new Biodata(
                            nama.toLowerCase(), notelp.toLowerCase(), alamat.toLowerCase()
                            , kampus.toLowerCase()));
                }
            }
        });

        Button BtnClear = findViewById(R.id.btn_clear);
        BtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearData();
            }
        });
    }

    private boolean isEmpty(String s) {
        return !TextUtils.isEmpty(s);
    }

    private void sendData(Biodata biodata) {

        databaseReference.push().setValue(biodata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressDialog.dismiss();

                EdtNama.setText("");
                EdtNotelp.setText("");
                EdtAlamat.setText("");
                EdtKampus.setText("");

                Snackbar.make(findViewById(R.id.btn_save), "Data Berhasil Ditambahkan", Snackbar.LENGTH_LONG).show();

                clearData();
            }
        });


    }

    private void clearData() {
        EdtNama.getText().clear();
        EdtNotelp.getText().clear();
        EdtAlamat.getText().clear();
        EdtKampus.getText().clear();
    }
}

