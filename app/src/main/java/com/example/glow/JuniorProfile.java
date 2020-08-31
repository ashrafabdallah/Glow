package com.example.glow;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.model.Junior;
import com.example.glow.network.InternetConnection;
import com.example.glow.network.RetrofitClient;
import com.example.glow.network.api.Api;
import com.example.glow.request.JuniorRequest;
import com.example.glow.response.juniorprofile.Datum;
import com.example.glow.response.juniorprofile.JuniorProfileResponse;
import com.example.glow.util.Constant;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class JuniorProfile extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;

    FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("juniorProfile");
    private Uri imageUri;
    private String imageUrl = null;
    String userName = "";
    TextView textName;
    ImageView imagebakground;
    CircleImageView imageProfile;
    Guideline guideline16;
    EditText edituniversty;
    EditText editphone;
    EditText editlvel;
    EditText editection;
    Button btnSave;
    String firstTime="0";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junior_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        textName = findViewById(R.id.textName);
        imagebakground = findViewById(R.id.imagebakground);
        imageProfile = findViewById(R.id.imageProfile);
        guideline16 = findViewById(R.id.guideline16);
        edituniversty = findViewById(R.id.age);
        editphone = findViewById(R.id.editphone);
        editlvel = findViewById(R.id.patientAddress);
        editection = findViewById(R.id.patientSocialStatus);
        btnSave = findViewById(R.id.btnSave);
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        start();
        imageProfile.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, GALLERY_CODE);
        });

    }

    private void start() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        CollectionReference collection = db.collection("Users");
        String userUid = user.getUid();
        collection
                .whereEqualTo("userId", userUid)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        dialog.dismiss();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            userName = snapshot.getString("username");
                            textName.setText(userName);
                        }
                    }
                    collectionReference.document(userUid).get().addOnSuccessListener(snapshot -> {
                        if (snapshot != null) {
                            firstTime=snapshot.getString("university");
                            edituniversty.setText(snapshot.getString("university"));
                            editlvel.setText(snapshot.getString("level"));
                            editphone.setText(snapshot.getString("phone"));
                            editection.setText(snapshot.getString("section"));
                            if(snapshot.getString("imageUrl")!=null){
                                Picasso.get().load(snapshot.getString("imageUrl")).error(R.drawable.photo).into(imageProfile);
                                Picasso.get().load(snapshot.getString("imageUrl")).error(R.drawable.photo).into(imagebakground);
                            }else{
                                imageProfile.setImageResource(R.drawable.photo);
                                imagebakground.setImageResource(R.drawable.photo);
                            }
                            imageUrl = snapshot.getString("imageUrl");

                        }
                    });

                });

    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                saveProfile();
                break;
        }
    }

    private void saveProfile() {
        String university = edituniversty.getText().toString().trim();
        String phone = editphone.getText().toString().trim();
        String level = editlvel.getText().toString().trim();
        String section = editection.getText().toString().trim();
        if (validation(university, phone, level, section)) {
            if (InternetConnection.checkConnection(JuniorProfile.this)) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.loading_bar);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                final StorageReference path = storageReference
                        .child("profile_images")
                        .child("junior_images_" + Timestamp.now().getSeconds());
                Map<String, String> m = new HashMap<>();
                m.put("userId", user.getUid());
                m.put("username", userName);
                m.put("firsttime", "1");
                m.put("university", university);
                m.put("level", level);
                m.put("phone", phone);
                m.put("section", section);

                if (imageUri != null)
                    path.putFile(imageUri).addOnSuccessListener(taskSnapshot -> path.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        m.put("imageUrl", imageUrl);
                    }).addOnCompleteListener(task -> {
                        uploadData(m, dialog);
                    }));
                else {
                    m.put("imageUrl", imageUrl);
                    uploadData(m, dialog);
                }


            } else {
                Toast.makeText(this, "No Internet.....", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void uploadData(Map m, Dialog dialog) {
        collectionReference.document(user.getUid()).set(m).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                Toast.makeText(JuniorProfile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                if(firstTime==null) {

                    startActivity(new Intent(this, HomeJunior.class));
                    finish();
                }else
                    finish();
            } else {
                Toast.makeText(JuniorProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(JuniorProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private boolean validation(String university, String phone, String level, String section) {
        boolean valied = true;
        if (university.isEmpty()) {
            valied = false;
            edituniversty.setError("Enter your university");
            edituniversty.requestFocus();
        } else {
            edituniversty.setError(null);
        }
        if (phone.isEmpty()) {
            valied = false;
            editphone.setError("Enter your phone");
            editphone.requestFocus();
        } else {
            editphone.setError(null);
        }

        if (level.isEmpty()) {
            valied = false;
            editlvel.setError("Enter your level");
            editlvel.requestFocus();
        } else {
            editlvel.setError(null);
        }

        if (section.isEmpty()) {
            valied = false;
            editection.setError("Enter your Section");
            editection.requestFocus();
        } else {
            editection.setError(null);
        }
        return valied;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imageProfile.setImageBitmap(bitmap);
                    imagebakground.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.i("TAG", "Some exception " + e);
                }
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
