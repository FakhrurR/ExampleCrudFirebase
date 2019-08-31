package com.ngandroidaja.examplefirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ngandroidaja.examplefirebase.model.Biodata;

import static android.text.TextUtils.isEmpty;

public class UpdateDelActivity extends AppCompatActivity {

    private DatabaseReference database;
    private ProgressDialog progressDialog;
    private Button BtnEdit, BtnDel;
    private EditText EdtNama;
    private EditText EdtNotelp;
    private EditText EdtAlamat;
    private EditText EdtKampus;

    private String id, name, notelp, alamat, kampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_del);

        database = FirebaseDatabase.getInstance().getReference();

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("nama");
        notelp = getIntent().getStringExtra("notelp");
        alamat = getIntent().getStringExtra("alamat");
        kampus = getIntent().getStringExtra("kampus");

        EdtNama = findViewById(R.id.edt_nama);
        EdtNotelp = findViewById(R.id.edt_notelp);
        EdtAlamat = findViewById(R.id.edt_alamat);
        EdtKampus = findViewById(R.id.edt_kampus);

        EdtNama.setText(name);
        EdtNotelp.setText(notelp);
        EdtAlamat.setText(alamat);
        EdtKampus.setText(kampus);

        BtnEdit = findViewById(R.id.btn_edit);
        BtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(name) && isEmpty(notelp) && isEmpty(alamat) && isEmpty(kampus)) {
                    Snackbar.make(findViewById(R.id.btn_save), "Ada data yang kosong", Snackbar.LENGTH_LONG).show();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            EdtNama.getWindowToken(), 0);
                    progressDialog.dismiss();
                } else {
                    progressDialog = ProgressDialog.show(UpdateDelActivity.this,
                            null, "Please Wait..."
                            , true
                            , false);

                    name = EdtNama.getText().toString();
                    notelp = EdtNotelp.getText().toString();
                    alamat = EdtAlamat.getText().toString();
                    kampus = EdtKampus.getText().toString();

                    editUser(new Biodata(
                            name.toLowerCase(), notelp.toLowerCase(), alamat.toLowerCase()
                            , kampus.toLowerCase()), id);
                    progressDialog.dismiss();
                }
            }
        });

        BtnDel = findViewById(R.id.btn_delete);
        BtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void editUser(Biodata biodata, String id) {
        database.child("Biodata").child(id)
                .setValue(biodata).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                EdtNama.setText("");
                EdtNotelp.setText("");
                EdtAlamat.setText("");
                EdtKampus.setText("");

                Snackbar.make(findViewById(R.id.btn_edit), "Data Berhasil DiEdit", Snackbar.LENGTH_LONG).show();

                Intent intent = new Intent(UpdateDelActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void deleteUser() {
        database.child("Biodata").child(id)
                .removeValue().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();

                Snackbar.make(findViewById(R.id.btn_edit), "Data Berhasil DiDelete", Snackbar.LENGTH_LONG).show();

                Intent intent = new Intent(UpdateDelActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void clearData() {
        EdtNama.getText().clear();
        EdtNotelp.getText().clear();
        EdtAlamat.getText().clear();
        EdtKampus.getText().clear();
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setTitle("Peringatan!!!");

        alertDialogBuilder
                .setMessage("Anda Yakin Hapus?")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteUser();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
