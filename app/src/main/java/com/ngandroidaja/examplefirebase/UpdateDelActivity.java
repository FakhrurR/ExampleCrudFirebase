package com.ngandroidaja.examplefirebase;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import static android.text.TextUtils.isEmpty;

public class UpdateDelActivity extends AppCompatActivity {

    private DatabaseReference database;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private EditText EdtNama;
    private EditText EdtNotelp;
    private EditText EdtAlamat;
    private EditText EdtKampus;
    private TextView textViewImage;
    private Uri imageUri;
    private ImageView img;
    private TextView uriGambar;
    private String imageId;

    private String id, name, notelp, alamat, kampus, urigambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_del);

        database = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("nama");
        notelp = getIntent().getStringExtra("notelp");
        alamat = getIntent().getStringExtra("alamat");
        kampus = getIntent().getStringExtra("kampus");
        urigambar = getIntent().getStringExtra("url");

        EdtNama = findViewById(R.id.edt_nama);
        EdtNotelp = findViewById(R.id.edt_notelp);
        EdtAlamat = findViewById(R.id.edt_alamat);
        EdtKampus = findViewById(R.id.edt_kampus);
        img = findViewById(R.id.img_photo);
        textViewImage = findViewById(R.id.img_name);

        EdtNama.setText(name);
        EdtNotelp.setText(notelp);
        EdtAlamat.setText(alamat);
        EdtKampus.setText(kampus);
        Glide.with(getApplicationContext())
                .load(urigambar)
                .error(R.drawable.ic_launcher_background)
                .into(img);

        textViewImage.setVisibility(View.INVISIBLE);

        Button btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
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
                    urigambar = textViewImage.getText().toString();

                    editUser(new Biodata(
                            name.toLowerCase(), notelp.toLowerCase(), alamat.toLowerCase()
                            , kampus.toLowerCase(), urigambar), id);
                }
            }
        });

        Button BtnChoose = findViewById(R.id.btn_choose);
        BtnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        Button btnDel = findViewById(R.id.btn_delete);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        Button BtnUpload = findViewById(R.id.btn_upload);
        BtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(UpdateDelActivity.this,
                        null, "Please Wait,Upload in progress..."
                        , true
                        , false);

                uploadFile();
            }
        });
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
                Toast.makeText(UpdateDelActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    textViewImage.setText(Objects.requireNonNull(downloadUri).toString());
                }
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
                Snackbar.make(findViewById(R.id.btn_edit), "Data Berhasil DiDelete", Snackbar.LENGTH_LONG).show();

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
                        Intent intent = new Intent(UpdateDelActivity.this, MainActivity.class);
                        startActivity(intent);
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

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
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
}
