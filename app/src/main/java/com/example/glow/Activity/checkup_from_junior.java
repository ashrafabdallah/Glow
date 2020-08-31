package com.example.glow.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class checkup_from_junior extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentPatientId=null;
    String currentUserId=null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    CollectionReference cp = db.collection("juniorProfile");

    CheckBox prosthesis;
    CheckBox amalgam;
    CheckBox fixedProsthodontics;
    CheckBox endodontics;
    CheckBox composite;
    Button btnchecked;
    TextView prosthesisDate;
    TextView amalgamDate;
    TextView fixedProsthodonticsDate;
    TextView endodonticsDate;
    TextView compositeDate;
    CircleImageView profile_image;
    Map<String, Object> m;
    List<Object> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkup_from_junior);
        Intent intent=getIntent();
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId=user.getUid();
        m = new HashMap<>();
        arr = new ArrayList<>();
        currentPatientId=intent.getStringExtra("patientID");
        prosthesis=findViewById(R.id.prosthesis);
        amalgam=findViewById(R.id.amalgam);
        fixedProsthodontics=findViewById(R.id.fixedProsthodontics);
        endodontics=findViewById(R.id.endodontics);
        composite=findViewById(R.id.composite);
        btnchecked=findViewById(R.id.btnchecked);
        prosthesisDate=findViewById(R.id.prosthesisDate);
        amalgamDate=findViewById(R.id.amalgamDate);
        fixedProsthodonticsDate=findViewById(R.id.fixedProsthodonticsDate);
        endodonticsDate=findViewById(R.id.endodonticsDate);
        compositeDate=findViewById(R.id.compositeDate);
        profile_image=findViewById(R.id.profile_image);
        btnchecked.setOnClickListener(this);
        prosthesis.setOnCheckedChangeListener(this);
        amalgam.setOnCheckedChangeListener(this);
        fixedProsthodontics.setOnCheckedChangeListener(this);
        endodontics.setOnCheckedChangeListener(this);
        composite.setOnCheckedChangeListener(this);
        start();
    }
    void start(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        collectionReference.document(currentPatientId).get().addOnCompleteListener(task -> {
            dialog.dismiss();
            DocumentSnapshot document = task.getResult();
            arr=(List<Object>) document.get("tasks");
            String imgUrl=document.getString("imageUrl");
            Picasso.get().load(imgUrl).placeholder(R.drawable.photo).into(profile_image);
            if(arr==null) {
                arr = new ArrayList<>();
            }
                m.put("prosthesis", "date");
                m.put("amalgam", "date");
                m.put("fixedProsthodontics", "date");
                m.put("endodontics", "date");
                m.put("composite", "date");
        }).addOnFailureListener(e -> {
            dialog.dismiss();
        });

    }
    @Override
    public void onClick(View v) {
        setResult(Activity.RESULT_OK,new Intent());
        arr.add(m);
        Map<String,Object> map = new HashMap<>();
        map.put("tasks", arr);
        map.put("check", "1");

        collectionReference.document(currentPatientId).update( map);
        cp.document(currentUserId).get().addOnCompleteListener(task -> {
            DocumentSnapshot doc = task.getResult();
            Map<String, Object> mapPatient = (Map<String, Object>) doc.getData().get("patient");
            if(mapPatient==null)
                mapPatient= new HashMap<>();
            mapPatient.put(currentPatientId, "1");
            Map<String, Object> temp = new HashMap<>();
            temp.put("patient",mapPatient);
            cp.document(currentUserId).update(temp);
        });
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String date=DateFormat.format("yyyy-MM-dd 'at' HH:mm:ss", new Date()).toString();
        switch(buttonView.getId()){
            case R.id.prosthesis:
            if(isChecked) {
                prosthesisDate.setText(date);
                updateTasks("prosthesis",date);
            }
            else {
                prosthesisDate.setText("date");
                updateTasks("prosthesis","date");
            }
            break;
            case R.id.amalgam:
            if(isChecked) {
                amalgamDate.setText(date);
                updateTasks("amalgam",date);
            }else {
                amalgamDate.setText("date");
                updateTasks("amalgam","date");

            }break;
            case R.id.fixedProsthodontics:
            if(isChecked) {
                updateTasks("fixedProsthodontics",date);
                fixedProsthodonticsDate.setText(date);
            }else {
                fixedProsthodonticsDate.setText("date");
                updateTasks("fixedProsthodontics","date");

            }break;
            case R.id.endodontics:
            if(isChecked) {
                updateTasks("endodontics",date);
                endodonticsDate.setText(date);
            }else {
                endodonticsDate.setText("date");
                updateTasks("endodontics","date");
            }break;
            case R.id.composite:
            if(isChecked) {
                updateTasks("composite",date);
                compositeDate.setText(date);
            }else {
                updateTasks("composite","date");
                compositeDate.setText("date");
            }break;

        }
    }
    void updateTasks(String seek,String value){
        m.put(seek, value);
    }
}
