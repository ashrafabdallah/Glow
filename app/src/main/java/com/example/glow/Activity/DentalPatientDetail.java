package com.example.glow.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.example.glow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DentalPatientDetail extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authStateListener;
  private FirebaseUser user;
  //Connection to Firestore
  private FirebaseFirestore db = FirebaseFirestore.getInstance();
  private CollectionReference collectionReference = db.collection("PatientProfile");
  CheckBox Allergies;
  CheckBox Anemia;
  CheckBox Asthma ;
  CheckBox Bleedingdisorder;
  CheckBox Diabetes  ;
  CheckBox Epilepsy;
  CheckBox Heartdisease ;
  CheckBox Hepatitis;
  CheckBox Historyofanticoagulants;
  CheckBox Historyofcorticosteroids;
  CheckBox Historyofradiotherapy  ;
  CheckBox Jaundice ;
  CheckBox Pregnancy;
  CheckBox Raisedbloodpressure  ;
  CheckBox Rheumatic;
  CheckBox Others;
  String sAllergies="0";
  String sAnemia="0";
  String sAsthma ="0";
  String sBleedingdisorder="0";
  String sDiabetes ="0" ;
  String sEpilepsy="0";
  String sHeartdisease="0" ;
  String sHepatitis="0";
  String sHistoryofanticoagulants="0";
  String sHistoryofcorticosteroids="0";
  String sHistoryofradiotherapy  ="0";
  String sJaundice ="0";
  String sPregnancy="0";
  String sRaisedbloodpressure  ="0";
  String sRheumatic="0";
  String sOthers="0";
  String currentUserId;
  Map<String, Object> m;
  CircleImageView circleImageView2;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dental_patient_detail);
    user = FirebaseAuth.getInstance().getCurrentUser();
    m = new HashMap<>();
    circleImageView2=findViewById(R.id.circleImageView2);
    Allergies=findViewById(R.id.Allergies);
    Anemia=findViewById(R.id.Anemia);
    Asthma=findViewById(R.id.Asthma);
    Bleedingdisorder=findViewById(R.id.Bleedingdisorder);
    Diabetes=findViewById(R.id.Diabetes);
    Epilepsy=findViewById(R.id.Epilepsy);
    Heartdisease=findViewById(R.id.Heartdisease);
    Hepatitis=findViewById(R.id.Hepatitis);
    Historyofanticoagulants=findViewById(R.id.Historyofanticoagulants);
    Historyofcorticosteroids=findViewById(R.id.Historyofcorticosteroids);
    Historyofradiotherapy=findViewById(R.id.Historyofradiotherapy);
    Jaundice=findViewById(R.id.Jaundice);
    Pregnancy=findViewById(R.id.Pregnancy);
    Raisedbloodpressure=findViewById(R.id.Raisedbloodpressure);
    Rheumatic=findViewById(R.id.Rheumatic);
    Others=findViewById(R.id.Others);
    currentUserId = user.getUid();
    Intent intent=getIntent();

    if(intent.getStringExtra("junior")==null) {
      if(intent.getStringExtra("patientID")!=null)
          currentUserId=intent.getStringExtra("patientID");
        Allergies.setEnabled(false);
        Anemia.setEnabled(false);
        Asthma.setEnabled(false);
        Bleedingdisorder.setEnabled(false);
        Diabetes.setEnabled(false);
        Epilepsy.setEnabled(false);
        Heartdisease.setEnabled(false);
        Hepatitis.setEnabled(false);
        Historyofanticoagulants.setEnabled(false);
        Historyofcorticosteroids.setEnabled(false);
        Historyofradiotherapy.setEnabled(false);
        Jaundice.setEnabled(false);
        Pregnancy.setEnabled(false);
        Raisedbloodpressure.setEnabled(false);
        Rheumatic.setEnabled(false);
        Others.setEnabled(false);
        Allergies.setEnabled(false);

    }else{
      currentUserId=intent.getStringExtra("patientID");
      Allergies.setOnCheckedChangeListener(this);
      Anemia.setOnCheckedChangeListener(this);
      Asthma.setOnCheckedChangeListener(this);
      Bleedingdisorder.setOnCheckedChangeListener(this);
      Diabetes.setOnCheckedChangeListener(this);
      Epilepsy.setOnCheckedChangeListener(this);
      Heartdisease.setOnCheckedChangeListener(this);
      Hepatitis.setOnCheckedChangeListener(this);
      Historyofanticoagulants.setOnCheckedChangeListener(this);
      Historyofcorticosteroids.setOnCheckedChangeListener(this);
      Historyofradiotherapy.setOnCheckedChangeListener(this);
      Jaundice.setOnCheckedChangeListener(this);
      Pregnancy.setOnCheckedChangeListener(this);
      Raisedbloodpressure.setOnCheckedChangeListener(this);
      Rheumatic.setOnCheckedChangeListener(this);
      Others.setOnCheckedChangeListener(this);
      Allergies.setOnCheckedChangeListener(this);
    }
    start();
  }


  void start(){
    final Dialog dialog = new Dialog(this);
    dialog.setContentView(R.layout.loading_bar);
    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    dialog.show();
        collectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
          dialog.dismiss();
          DocumentSnapshot document = task.getResult();
          Map<String,String> snapshot = (Map<String,String>) document.get("history");
          if(document.get("imageUrl")!=null)
              Picasso.get().load(document.get("imageUrl").toString()).placeholder(R.drawable.photo).into(circleImageView2);
          else
            circleImageView2.setImageResource(R.drawable.photo);
          m.put("Allergies", sAllergies);
          m.put("Anemia", sAnemia);
          m.put("Asthma", sAsthma);
          m.put("Bleedingdisorder", sBleedingdisorder);
          m.put("Diabetes", sDiabetes);
          m.put("Epilepsy", sEpilepsy);
          m.put("Heartdisease", sHeartdisease);
          m.put("Hepatitis", sHepatitis);
          m.put("Historyofanticoagulants", sHistoryofanticoagulants);
          m.put("Historyofcorticosteroids", sHistoryofcorticosteroids);
          m.put("Historyofradiotherapy", sHistoryofradiotherapy);
          m.put("Jaundice", sJaundice);
          m.put("Pregnancy", sPregnancy);
          m.put("Raisedbloodpressure", sRaisedbloodpressure);
          m.put("Rheumatic", sRheumatic);
          m.put("Others", sOthers);
          if(snapshot!=null) {
            Allergies.setChecked(snapshot.get("Allergies").equals("1"));
            Anemia.setChecked(snapshot.get("Anemia").equals("1"));
            Asthma.setChecked(snapshot.get("Asthma").equals("1"));
            Bleedingdisorder.setChecked(snapshot.get("Bleedingdisorder").equals("1"));
            Diabetes.setChecked(snapshot.get("Diabetes").equals("1"));
            Epilepsy.setChecked(snapshot.get("Epilepsy").equals("1"));
            Heartdisease.setChecked(snapshot.get("Heartdisease").equals("1"));
            Hepatitis.setChecked(snapshot.get("Hepatitis").equals("1"));
            Historyofanticoagulants.setChecked(snapshot.get("Historyofanticoagulants").equals("1"));
            Historyofcorticosteroids.setChecked(snapshot.get("Historyofcorticosteroids").equals("1"));
            Historyofradiotherapy.setChecked(snapshot.get("Historyofradiotherapy").equals("1"));
            Jaundice.setChecked(snapshot.get("Jaundice").equals("1"));
            Pregnancy.setChecked(snapshot.get("Pregnancy").equals("1"));
            Raisedbloodpressure.setChecked(snapshot.get("Raisedbloodpressure").equals("1"));
            Rheumatic.setChecked(Objects.requireNonNull(snapshot.get("Rheumatic")).equals("1"));
            Others.setChecked(snapshot.get("Others").equals("1"));
          }else{
              Map<String,Object> map = new HashMap<>();
              map.put("history", m);
            collectionReference.document(currentUserId).update(map);
          }
        }).addOnFailureListener(e -> {
          dialog.dismiss();
        });

  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    switch (buttonView.getId()){
      case R.id.Allergies:if(isChecked)updateHistory("Allergies", "1");else updateHistory("Allergies", "0");break;
      case R.id.Anemia:if(isChecked)updateHistory("Anemia", "1");else updateHistory("Anemia", "0");break;
      case R.id.Asthma:if(isChecked)updateHistory("Asthma", "1");else updateHistory("Asthma", "0");break;
      case R.id.Bleedingdisorder:if(isChecked)updateHistory("Bleedingdisorder", "1");else updateHistory("Bleedingdisorder", "0");break;
      case R.id.Diabetes:if(isChecked)updateHistory("Diabetes", "1");else updateHistory("Diabetes", "0");break;
      case R.id.Epilepsy:if(isChecked)updateHistory("Epilepsy", "1");else updateHistory("Epilepsy", "0");break;
      case R.id.Heartdisease:if(isChecked)updateHistory("Heartdisease", "1");else updateHistory("Heartdisease", "0");break;
      case R.id.Hepatitis:if(isChecked)updateHistory("Hepatitis", "1");else updateHistory("Hepatitis", "0");break;
      case R.id.Historyofanticoagulants:if(isChecked)updateHistory("Historyofanticoagulants", "1");else updateHistory("Historyofanticoagulants", "0");break;
      case R.id.Historyofcorticosteroids:if(isChecked)updateHistory("Historyofcorticosteroids", "1");else updateHistory("Historyofcorticosteroids", "0");break;
      case R.id.Historyofradiotherapy:if(isChecked)updateHistory("Historyofradiotherapy", "1");else updateHistory("Historyofradiotherapy", "0");break;
      case R.id.Jaundice:if(isChecked)updateHistory("Jaundice", "1");else updateHistory("Jaundice", "0");break;
      case R.id.Raisedbloodpressure:if(isChecked)updateHistory("Raisedbloodpressure", "1");else updateHistory("Raisedbloodpressure", "0");break;
      case R.id.Pregnancy:if(isChecked)updateHistory("Pregnancy", "1");else updateHistory("Pregnancy", "0");break;
      case R.id.Rheumatic:if(isChecked)updateHistory("Rheumatic", "1");else updateHistory("Rheumatic", "0");break;
      case R.id.Others:if(isChecked)updateHistory("Others", "1");else updateHistory("Others", "0");break;

    }
  }
  void updateHistory(String seek,String value){
    m.put(seek, value);
    Map<String,Object> map = new HashMap<>();
    map.put("history", m);
    collectionReference.document(currentUserId).update(map);
  }
}
