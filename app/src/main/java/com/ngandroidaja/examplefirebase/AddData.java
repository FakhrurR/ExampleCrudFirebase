package com.ngandroidaja.examplefirebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ngandroidaja.examplefirebase.model.Biodata;

import java.io.IOException;
import java.util.Objects;

public class AddData extends AppCompatActivity {
    private final String TAG = AddData.class.getName();
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private EditText EdtNama;
    private EditText EdtNotelp;
    private EditText EdtAlamat;
    private EditText EdtKampus;
    private TextView uriGambar;
    private ImageView img;


    private Biodata biodata;

    private String nama;
    private String notelp;
    private String alamat;
    private String kampus;
    private String urigambar;
    private String imageId;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        EdtNama = findViewById(R.id.edt_nama);
        EdtNotelp = findViewById(R.id.edt_notelp);
        EdtAlamat = findViewById(R.id.edt_alamat);
        EdtKampus = findViewById(R.id.edt_kampus);
        uriGambar = findViewById(R.id.img_name);
        img = findViewById(R.id.img_photo);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Biodata");
        storageReference = FirebaseStorage.getInstance().getReference("Image");


        Button BtnChoose = findViewById(R.id.btn_choose);
        BtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        Button BtnSave = findViewById(R.id.btn_save);
        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(nama) && isEmpty(notelp) && isEmpty(alamat) && isEmpty(kampus) && isEmpty(urigambar)) {
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
                    urigambar = uriGambar.getText().toString();

                    sendData(new Biodata(
                            nama.toLowerCase(), notelp.toLowerCase(), alamat.toLowerCase()
                            , kampus.toLowerCase(), urigambar));

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

        Button BtnUpload = findViewById(R.id.btn_upload);
        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(AddData.this,
                        null, "Please Wait,Upload in progress..."
                        , true
                        , false);

                uploadFile();
            }
        });
    }

    private boolean isEmpty(String s) {
        return !TextUtils.isEmpty(s);
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    private void uploadFile() {

        imageId = System.currentTimeMillis() + "." + getExtension(imageUri);

        final StorageReference mRef = storageReference.child(imageId);

        mRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return mRef.getDownloadUrl();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddData.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    uriGambar.setText(Objects.requireNonNull(downloadUri).toString());
                }
            }
        });
    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                img.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendData(Biodata biodata) {

        databaseReference.push().setValue(biodata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressDialog.dismiss();

                clearData();

                Snackbar.make(findViewById(R.id.btn_save), "Data Berhasil Ditambahkan", Snackbar.LENGTH_LONG).show();

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

