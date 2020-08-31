package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
import com.example.glow.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class PatientDetailss extends AppCompatActivity {
    TextView textName, textPhone, textstudentname, textstudentphone;
    CircleImageView imageProfile;
    ImageView drawerBackgroundImage;
    CheckBox prosthesis, operative, fixedpro, endodonties, operativepostive;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("StudentProfile");

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detailss);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        textName = findViewById(R.id.drawerUserName);
        textPhone = findViewById(R.id.textPhone);
        textstudentname = findViewById(R.id.textstudentname);
        textstudentphone = findViewById(R.id.studentphone);
        imageProfile = findViewById(R.id.imageProfile);
        drawerBackgroundImage = findViewById(R.id.drawerBackgroundImage);

        prosthesis = findViewById(R.id.prosthesis);
        operative = findViewById(R.id.operative);
        fixedpro = findViewById(R.id.fixedpro);
        endodonties = findViewById(R.id.endodonties);
        operativepostive = findViewById(R.id.operativepostive);



        Intent intent = getIntent();
        String image = intent.getExtras().getString("image");
        String username = intent.getExtras().getString("username");
        String phone = intent.getExtras().getString("phone");
        Map<String, Object> data = (Map<String, Object>) intent.getSerializableExtra("AllData");
        if (image == null)
            Picasso.get().load(R.drawable.logo).into(imageProfile);
            Picasso.get().load(R.drawable.logo).into(drawerBackgroundImage);
        Picasso.get().load(image).into(imageProfile);
        Picasso.get().load(image).into(drawerBackgroundImage);
        textName.setText(username);
        textPhone.setText(phone);
        System.out.println("fixedProsthodontics : is" + data.get("fixedProsthodontics"));

        if (!data.get("prosthesis").equals("date")) {
            prosthesis.setChecked(true);

        }
        if (!data.get("amalgam").equals("date")) {
            operative.setChecked(true);

        }
        if (!data.get("fixedProsthodontics").equals("date")) {
            fixedpro.setChecked(true);

        }
        if (!data.get("endodontics").equals("date")) {
            endodonties.setChecked(true);

        }
        if (!data.get("composite").equals("date")) {
            operativepostive.setChecked(true);

        }
        collectionReference.document(data.get("studentID").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result = task.getResult();
                textstudentname.setText(result.get("username").toString());
                textstudentphone.setText(result.get("phone").toString());




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PatientDetailss.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
