package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
import com.example.glow.SendNotification;
import com.example.glow.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentCheckup extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    final static int DONE_CODE = 1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentPatientId = null;
    String currentUserId;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    CollectionReference cp = db.collection("StudentProfile");

    CheckBox prosthesis;
    CheckBox amalgam;
    CheckBox fixedProsthodontics;
    CheckBox endodontics;
    CheckBox composite;
    Button btnchecked;
    Button btnFollow;
    TextView prosthesisDate;
    TextView amalgamDate;
    TextView fixedProsthodonticsDate;
    TextView endodonticsDate;
    TextView compositeDate;
    CircleImageView profile_image;
    Map<String, Object> m;
    List<Object> arr;
    Patient patientdetailes;
    String follow = "0";
    String studentName;
    Map<String, Object> mapPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_checkup);
        Intent intent = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();
        m = new HashMap<>();
        arr = new ArrayList<>();
        currentPatientId = intent.getStringExtra("patientID");
        prosthesis = findViewById(R.id.prosthesis);
        amalgam = findViewById(R.id.amalgam);
        fixedProsthodontics = findViewById(R.id.fixedProsthodontics);
        endodontics = findViewById(R.id.endodontics);
        composite = findViewById(R.id.composite);
        btnchecked = findViewById(R.id.btnchecked);
        btnFollow = findViewById(R.id.btnFollow);
        prosthesisDate = findViewById(R.id.prosthesisDate);
        amalgamDate = findViewById(R.id.amalgamDate);
        fixedProsthodonticsDate = findViewById(R.id.fixedProsthodonticsDate);
        endodonticsDate = findViewById(R.id.endodonticsDate);
        compositeDate = findViewById(R.id.compositeDate);
        profile_image = findViewById(R.id.profile_image);
        btnchecked.setOnClickListener(this);
        btnFollow.setOnClickListener(this);
        prosthesis.setEnabled(false);
        amalgam.setEnabled(false);
        fixedProsthodontics.setEnabled(false);
        endodontics.setEnabled(false);
        composite.setEnabled(false);
        start();
    }

    void start() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        cp.document(currentUserId).get().addOnCompleteListener(task -> {
            studentName = task.getResult().getData().get("username").toString();
        });
        collectionReference.document(currentPatientId).get().addOnCompleteListener(task -> {
            dialog.dismiss();
            DocumentSnapshot document = task.getResult();
            arr = (List<Object>) document.get("tasks");
            Map<String, String> map;
            if (arr == null) {
                arr = new ArrayList<>();
                map = new HashMap<>();
            } else
                map = (Map<String, String>) arr.get(arr.size() - 1);
            if (map == null)
                map = new HashMap<>();
            if (document.get("imageUrl") != null)
                Picasso.get().load(document.get("imageUrl").toString()).placeholder(R.drawable.photo).error(R.drawable.photo).into(profile_image);
            else
                profile_image.setImageResource(R.drawable.photo);
            follow = document.get("follow").toString();
            m.put("studentID", currentUserId);
            m.put("prosthesis", map.get("prosthesis"));
            m.put("amalgam", map.get("amalgam"));
            m.put("fixedProsthodontics", map.get("fixedProsthodontics"));
            m.put("endodontics", map.get("endodontics"));
            m.put("composite", map.get("composite"));
            if (!m.get("prosthesis").equals("date")) {
                prosthesis.setChecked(true);
                prosthesisDate.setText(map.get("prosthesis"));
            }
            if (!m.get("amalgam").equals("date")) {
                amalgam.setChecked(true);
                amalgamDate.setText(map.get("amalgam"));
            }
            if (!m.get("fixedProsthodontics").equals("date")) {
                fixedProsthodontics.setChecked(true);
                fixedProsthodonticsDate.setText(map.get("fixedProsthodontics"));
            }
            if (!m.get("endodontics").equals("date")) {
                endodontics.setChecked(true);
                endodonticsDate.setText(map.get("endodontics"));
            }
            if (!m.get("composite").equals("date")) {
                composite.setChecked(true);
                compositeDate.setText(map.get("composite"));
            }
            if (document.get("imageUrl") != null) {
                patientdetailes = new Patient(document.get("active").toString(), document.get("address").toString()
                        , document.get("age").toString(), document.get("imageUrl").toString(), document.get("username").toString()
                        , document.get("userId").toString(), document.get("phone").toString());
            }

        }).addOnFailureListener(e -> {
            dialog.dismiss();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DONE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String done = data.getStringExtra("follow");
                if (done.equals("1")) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("follow", "1");
                    db.collection("PatientProfile").document(currentPatientId).update(m);
                    finish();
                    follow = "1";
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnFollow) {
            Intent i = new Intent(StudentCheckup.this, SendNotification.class);
            i.putExtra("data", patientdetailes);
            i.putExtra("type", "follow");
            i.putExtra("studentName", studentName);
            startActivityForResult(i, DONE_CODE);
        } else {
            String date = DateFormat.format("yyyy-MM-dd 'at' HH:mm:ss", new Date()).toString();
            m.put("endTaskDate", date);
            if (follow.equals("1"))
                arr.set(arr.size() - 1, m);
            else
                arr.add(m);
            setResult(Activity.RESULT_OK, new Intent());

            Map<String, Object> map = new HashMap<>();
            map.put("tasks", arr);
            map.put("check", "0");
            map.put("active", "0");
            map.put("done", "0");
            map.put("book", "0");
            map.put("follow", "0");
            collectionReference.document(currentPatientId).update(map);
            cp.document(currentUserId).get().addOnCompleteListener(task -> {
                studentName = task.getResult().getData().get("username").toString();
                Map<String, Object> mapPatient = (Map<String, Object>) task.getResult().getData().get("patient");
                if (mapPatient == null)
                    mapPatient = new HashMap<>();
                mapPatient.put(currentPatientId, "1");
                Map<String, Object> temp = new HashMap<>();
                temp.put("patient", mapPatient);
                cp.document(currentUserId).update(temp);
            });
            finish();
        }
    }

    /*
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String date= DateFormat.format("yyyy-MM-dd 'at' HH:mm:ss", new Date()).toString();
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
    */
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
