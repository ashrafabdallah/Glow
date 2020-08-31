package com.example.glow;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.network.RetrofitClient;
import com.example.glow.network.api.Api;
import com.example.glow.response.student.StudentProfileData;
import com.example.glow.response.student.StudentProfileResponse;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StudentProfile extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("StudentProfile");
    private Uri imageUri;
    private String imageUrl = null;
    CircleImageView imageProfile;
    ImageView imageBackground;
    Button studentProfileSaveButton;
    TextView studentProfileName;
    EditText studentProfileUniversity;
    EditText studentProfilePhone;
    EditText studentProfileLevel;
    EditText studentProfileEducation;
    String STUDENT_ID;
    SharedPreferences sharedPreferences;
    String userName="";
    String userToken;
    String firstTime="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        STUDENT_ID=sharedPreferences.getString("studentId",null);
        imageProfile = findViewById(R.id.imageProfile);
        imageBackground = findViewById(R.id.imagebakground);
        studentProfileSaveButton=findViewById(R.id.studentProfileSaveButton);
        studentProfileName=findViewById(R.id.studentProfileName);
        studentProfileUniversity=findViewById(R.id.studentProfileUniversity);
        studentProfilePhone=findViewById(R.id.studentProfilePhone);
        studentProfileLevel=findViewById(R.id.studentProfileLevel);
        studentProfileEducation=findViewById(R.id.studentProfileEducation);
        studentProfileSaveButton.setOnClickListener(this);
        imageProfile.setOnClickListener(this);
        start();

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.studentProfileSaveButton) {
            String university = studentProfileUniversity.getText().toString().trim();
            String phone = studentProfilePhone.getText().toString().trim();
            String level = studentProfileLevel.getText().toString().trim();
            String section = studentProfileEducation.getText().toString().trim();
            if (validation(university, phone, level, section)) {
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.loading_bar);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                final StorageReference path = storageReference
                        .child("profile_images")
                        .child("student_images_" + Timestamp.now().getSeconds());
                Map<String, String> m = new HashMap<>();
                m.put("userId", user.getUid());
                m.put("userToken", userToken);
                m.put("username", userName);
                m.put("firsttime", "1");
                m.put("university", university);
                m.put("phone", phone);
                m.put("level", level);
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
            }
        }else{
            //get image from gallery/phone
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_CODE);
        }
    }

    void uploadData(Map m,Dialog dialog){
        collectionReference.document(user.getUid()).set(m).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                dialog.dismiss();
                Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();

                if(firstTime==null) {
                    startActivity(new Intent(this, HomeStudent.class));
                    finish();
                }else
                    finish();
            }else{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    void start(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        CollectionReference collection = db.collection("Users");
        final String currentUserId = user.getUid();
        collection
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    assert queryDocumentSnapshots != null;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            userName=snapshot.getString("username");
                            studentProfileName.setText(userName);
                            userToken=snapshot.getString("userToken");

                        }
                    }
                    collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
                        dialog.dismiss();
                        if(snapshot!=null) {
                            firstTime=snapshot.getString("university");
                            studentProfileUniversity.setText(snapshot.getString("university"));
                            studentProfilePhone.setText(snapshot.getString("phone"));
                            studentProfileLevel.setText(snapshot.getString("level"));
                            studentProfileEducation.setText(snapshot.getString("section"));
                            if(snapshot.getString("imageUrl")!=null){
                                Picasso.get().load(snapshot.getString("imageUrl")).error(R.drawable.photo).into(imageProfile);
                                Picasso.get().load(snapshot.getString("imageUrl")).error(R.drawable.photo).into(imageBackground);
                            }else{
                                imageProfile.setImageResource(R.drawable.photo);
                                imageBackground.setImageResource(R.drawable.photo);
                            }
                            imageUrl=snapshot.getString("imageUrl");
                        }
                    });
                });
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
                    imageBackground.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.i("TAG", "Some exception " + e);
                }
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean validation(String university, String phone, String level, String section) {
        boolean validate = true;
        if (university.isEmpty()) {
            validate = false;
            studentProfileUniversity.setError("please enter your university");
            studentProfileUniversity.requestFocus();
        } else {
            studentProfileUniversity.setError(null);
        }
        if (phone.isEmpty() ) {
            validate = false;
            studentProfilePhone.setError("please enter your phone");
            studentProfilePhone.requestFocus();
        } else {
            studentProfilePhone.setError(null);
        }
        if (level.isEmpty() ) {
            validate = false;
            studentProfileLevel.setError("please enter your level");
            studentProfileLevel.requestFocus();
        } else {
            studentProfileLevel.setError(null);
        }
        if (section.isEmpty() ) {
            validate = false;
            studentProfileEducation.setError("please enter your section");
            studentProfileEducation.requestFocus();
        } else {
            studentProfileEducation.setError(null);
        }
        return validate;
    }


}
