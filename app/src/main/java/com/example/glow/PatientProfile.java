package com.example.glow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
public class PatientProfile extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int GALLERY_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    private Uri imageUri;
    private String imageUrl = null;
    CircleImageView imageProfile;
    ImageView imageBackground;
    TextView textName;
    EditText patientAge;
    EditText editphone;
    EditText patientAddress;
    EditText patientSocialStatus;
   Button btnSave;
    String patientHeart="0",patientDiabetes="0",patientPressure="0",active="0";
    CheckBox patientHeartCheckBox;
    CheckBox patientDiabetesCheckBox;
    CheckBox patientPressureCheckBox;
    String userName="";
    String userToken;
    String firstTime="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();
        imageProfile = findViewById(R.id.imageProfile);
        imageBackground = findViewById(R.id.imagebakground);
        textName=findViewById(R.id.textName);
        imageProfile=findViewById(R.id.imageProfile);
        patientAge=findViewById(R.id.age);
        editphone=findViewById(R.id.editphone);
        patientAddress =findViewById(R.id.patientAddress);
        patientSocialStatus=findViewById(R.id.patientSocialStatus);
        patientHeartCheckBox=findViewById(R.id.patientHeart);
        patientDiabetesCheckBox =findViewById(R.id.patientDiabetes);
        patientPressureCheckBox=findViewById(R.id.patientPressure);
        patientDiabetesCheckBox.setOnCheckedChangeListener(this);
        patientHeartCheckBox.setOnCheckedChangeListener(this);
        patientPressureCheckBox.setOnCheckedChangeListener(this);
        btnSave=findViewById(R.id.btnSave);
        imageProfile.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageProfile:
                //get image from gallery/phone
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
            case R.id.btnSave:
                saveProfile();
                break;
        }
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
                            textName.setText(userName);
                            userToken=snapshot.getString("userToken");
                        }
                    }
                    collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
                        dialog.dismiss();
                        if(snapshot!=null) {
                            patientAge.setText(snapshot.getString("age"));
                            patientAddress.setText(snapshot.getString("address"));
                            patientSocialStatus.setText(snapshot.getString("socialStatus"));
                            editphone.setText(snapshot.getString("phone"));
                            patientHeart=snapshot.getString("heart");
                            patientDiabetes=snapshot.getString("diabetes");
                            patientPressure=snapshot.getString("pressure");
                            active=snapshot.getString("active");
                            firstTime=snapshot.getString("firsttime");
                            if(patientPressure!=null)
                                patientPressureCheckBox.setChecked(patientPressure.equals("1"));
                            if(patientDiabetes!=null)
                                patientDiabetesCheckBox.setChecked(patientDiabetes.equals("1"));
                            if(patientHeart!=null)
                                patientHeartCheckBox.setChecked(patientHeart.equals("1"));
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
    void saveProfile() {
        String aga = patientAge.getText().toString().trim();
        String phone = editphone.getText().toString().trim();
        String address = patientAddress.getText().toString().trim();
        String socialStatus = patientSocialStatus.getText().toString().trim();
        if (validation(aga, phone, address, socialStatus)) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            final StorageReference path = storageReference
                    .child("profile_images")
                    .child("patient_images_" + Timestamp.now().getSeconds());
            Map<String, String> m = new HashMap<>();
            m.put("firsttime", "1");
            m.put("userId", user.getUid());
            m.put("userToken", userToken);
            m.put("username", userName);
            m.put("age", aga);
            m.put("phone", phone);
            m.put("address", address);
            m.put("socialStatus", socialStatus);
            m.put("pressure", patientPressure);
            m.put("diabetes", patientDiabetes);
            m.put("heart", patientHeart);
            if(firstTime==null){
                m.put("active", active);
                m.put("done", "0");
                m.put("book", "0");
                m.put("follow", "0");
                m.put("check", "0");
            }
            if(imageUri!=null)
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
    void uploadData(Map m,Dialog dialog){
        if(firstTime!=null) {
            collectionReference.document(user.getUid()).update(m).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(PatientProfile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    if(firstTime==null) {

                        startActivity(new Intent(PatientProfile.this, HomePatient.class));
                        finish();
                    }else
                        finish();

                } else {
                    Toast.makeText(PatientProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }).addOnFailureListener(e -> {
                Toast.makeText(PatientProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }else{
            collectionReference.document(user.getUid()).set(m).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(PatientProfile.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PatientProfile.this, HomePatient.class));
                } else {
                    Toast.makeText(PatientProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }).addOnFailureListener(e -> {
                Toast.makeText(PatientProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean validation(String age, String phone, String address, String status) {
        boolean valied = true;
        if (age.isEmpty()) {
            valied = false;
            patientAge.setError("Enter your age");
            patientAge.requestFocus();
        } else {
            patientAge.setError(null);
        }
        if (phone.isEmpty()) {
            valied = false;
            editphone.setError("Enter your phone");
            editphone.requestFocus();
        } else {
            editphone.setError(null);
        }

        if (address.isEmpty()) {
            valied = false;
            patientAddress.setError("Enter your address");
            patientAddress.requestFocus();
        } else {
            patientAddress.setError(null);
        }

        if (status.isEmpty()) {
            valied = false;
            patientSocialStatus.setError("Enter your status");
            patientSocialStatus.requestFocus();
        } else {
            patientSocialStatus.setError(null);
        }
        return valied;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.patientHeart:patientHeart=(isChecked)?"1":"0";break;
            case R.id.patientDiabetes:patientDiabetes=(isChecked)?"1":"0";break;
            case R.id.patientPressure:patientPressure=(isChecked)?"1":"0";break;
        }
    }
}
