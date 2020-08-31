package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Selectstudentfrompatienthome extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {
    private FirebaseAuth firebaseAuth;
    final static int DONE_CODE=1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentStudenttId=null;
    String currentUserId=null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    TextView studentName;
    CheckBox prosthesis;
    CheckBox amalgam;
    CheckBox fixedProsthodontics;
    CheckBox endodontics;
    CheckBox composite;
    RatingBar ratingBar;
    TextView prosthesisDate;
    TextView amalgamDate;
    TextView fixedProsthodonticsDate;
    TextView endodonticsDate;
    TextView compositeDate;
    CircleImageView profile_image;
    Map<String, Object> m;
    Map<String, String> mRate;
    List<Object> arr;
    float rate ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectstudentfrompatienthome);
        Intent intent=getIntent();
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId=user.getUid();
        m = new HashMap<>();
        arr = new ArrayList<>();
        currentStudenttId=intent.getStringExtra("studentId");
        studentName=findViewById(R.id.studentName);
        ratingBar=findViewById(R.id.ratingBar);
        prosthesis=findViewById(R.id.prosthesis);
        amalgam=findViewById(R.id.amalgam);
        fixedProsthodontics=findViewById(R.id.fixedProsthodontics);
        endodontics=findViewById(R.id.endodontics);
        composite=findViewById(R.id.composite);
        prosthesisDate=findViewById(R.id.prosthesisDate);
        amalgamDate=findViewById(R.id.amalgamDate);
        fixedProsthodonticsDate=findViewById(R.id.fixedProsthodonticsDate);
        endodonticsDate=findViewById(R.id.endodonticsDate);
        compositeDate=findViewById(R.id.compositeDate);
        profile_image=findViewById(R.id.circleImageView3);
        prosthesis.setEnabled(false);
        amalgam.setEnabled(false);
        fixedProsthodontics.setEnabled(false);
        endodontics.setEnabled(false);
        composite.setEnabled(false);
        ratingBar.setOnRatingBarChangeListener(this);
        start();
    }
    void start(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        collectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            dialog.dismiss();
            CollectionReference studentReference = db.collection("StudentProfile");
            studentReference.document(currentStudenttId).get().addOnCompleteListener(task1 -> {
                DocumentSnapshot document = task1.getResult();
                mRate=  (Map<String, String>) document.get("rate");
                if(mRate==null)
                    mRate=new HashMap<>();
                if(mRate.get(currentUserId)!=null)
                    rate=Float.parseFloat(mRate.get(currentUserId));
                else
                    rate=0;
                ratingBar.setRating(rate);
                String imgUrl=document.getString("imageUrl");
                Picasso.get().load(imgUrl).placeholder(R.drawable.photo).into(profile_image);
                studentName.setText(document.getString("username"));

            });

            DocumentSnapshot document = task.getResult();
            arr=(List<Object>) document.get("tasks");
            Map<String,String> map= (Map<String, String>) arr.get(arr.size()-1);

            m.put("prosthesis", map.get("prosthesis"));
            m.put("amalgam",  map.get("amalgam"));
            m.put("fixedProsthodontics", map.get("fixedProsthodontics"));
            m.put("endodontics",  map.get("endodontics"));
            m.put("composite",  map.get("composite"));
            if(!m.get("prosthesis").equals("date")) {
                prosthesis.setChecked(true);
                prosthesisDate.setText(map.get("prosthesis"));
            }
            if(!m.get("amalgam").equals("date")) {
                amalgam.setChecked(true);
                amalgamDate.setText(map.get("amalgam"));
            }
            if(!m.get("fixedProsthodontics").equals("date")) {
                fixedProsthodontics.setChecked(true);
                fixedProsthodonticsDate.setText(map.get("fixedProsthodontics"));
            }
            if(!m.get("endodontics").equals("date")) {
                endodontics.setChecked(true);
                endodonticsDate.setText(map.get("endodontics"));
            }
            if(!m.get("composite").equals("date")) {
                composite.setChecked(true);
                compositeDate.setText(map.get("composite"));
            }

        }).addOnFailureListener(e -> {
            dialog.dismiss();
        });

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        CollectionReference collectionReference = db.collection("StudentProfile");
        mRate.put(currentUserId, rating+"");
        Map<String, Object> temp=new HashMap<>();
        temp.put("rate", mRate);
        collectionReference.document(currentStudenttId).update(temp);
        }
}
