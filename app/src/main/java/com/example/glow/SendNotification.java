package com.example.glow;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.glow.model.Patient;
import com.example.glow.notification.ApiServies;
import com.example.glow.notification.Client;
import com.example.glow.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class SendNotification extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("PatientProfile");
    ImageButton imageback;
    CircleImageView imageProfile;
    TextView textname;
    TextView textaddress;
    TextView textphone;
    Button btnnotification;
    ImageView drawerBackgroundImage;
    TextView texttime, textdate;
    FirebaseFirestore mFirebaseFirestore;
    Patient patient;
    private int mYear, mMonth, mDay;
    ConstraintLayout VisableCon;
    CheckBox prosthesis, operative, fixedpro, endodonties, operativepostive;
    String doneOrBook;
    List<Object> arr;

    private String currentuserid;
    private String currentuserImage;
    private String currentuserName;
    private ApiServies apiServies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        currentuserid = FirebaseAuth.getInstance().getUid();
        imageProfile = findViewById(R.id.imageProfile);
        drawerBackgroundImage = findViewById(R.id.drawerBackgroundImage);
        textname = findViewById(R.id.drawerUserName);
        textaddress = findViewById(R.id.textaddress);
        textphone = findViewById(R.id.textphone);
        VisableCon = findViewById(R.id.VisableCon);
        btnnotification = findViewById(R.id.btnnotification);
        Intent intent = getIntent();

        patient = intent.getExtras().getParcelable("data");

        if(getIntent().getStringExtra("type").equals("student")) {
            VisableCon.setVisibility(View.VISIBLE);
            setVisible();

            doneOrBook = "book";
            CollectionReference collectionReference = db.collection("StudentProfile");
            collectionReference.document(currentuserid).get().addOnSuccessListener(documentSnapshot -> {
                Map<String,Object> map=documentSnapshot.getData();
                if(map.get("imageUrl")!=null)
                    currentuserImage=map.get("imageUrl").toString();
                currentuserName=map.get("username").toString();
            });
        }
        else if(getIntent().getStringExtra("type").equals("follow")){
            VisableCon.setVisibility(View.VISIBLE);
            setVisible();
            doneOrBook="follow";
            CollectionReference collectionReference = db.collection("StudentProfile");
            collectionReference.document(currentuserid).get().addOnSuccessListener(documentSnapshot -> {
                Map<String,Object> map=documentSnapshot.getData();
                if(map.get("imageUrl")!=null)
                    currentuserImage=map.get("imageUrl").toString();
                currentuserName=map.get("username").toString();
            });
        }
        else {
            VisableCon.setVisibility(View.GONE);

            doneOrBook = "done";
            CollectionReference collectionReference = db.collection("juniorProfile");
            collectionReference.document(currentuserid).get().addOnSuccessListener(documentSnapshot -> {
                Map<String,Object> map=documentSnapshot.getData();
                if(map.get("imageUrl")!=null)
                    currentuserImage=map.get("imageUrl").toString();
                currentuserName=map.get("username").toString();
            });

        }
        apiServies= Client.getRetrofit("https://fcm.googleapis.com/").create(ApiServies.class);
        if (patient.getImageUrl() != null) {
            Picasso.get().load(patient.getImageUrl()).into(imageProfile);
            Picasso.get().load(patient.getImageUrl()).into(drawerBackgroundImage);
        } else {
            Picasso.get().load(R.drawable.photo).into(imageProfile);
            Picasso.get().load(R.drawable.photo).into(drawerBackgroundImage);
        }
        textphone.setText(patient.getPhone());
        textname.setText(patient.getUserName());
        textaddress.setText(patient.getAddress());
    }

    private void setVisible() {
        prosthesis = findViewById(R.id.prosthesis);
        operative = findViewById(R.id.operative);
        fixedpro = findViewById(R.id.fixedpro);
        endodonties = findViewById(R.id.endodonties);
        operativepostive = findViewById(R.id.operativepostive);
        start();
    }
    void start(){
        Map<String, Object> m=new HashMap<>();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        collectionReference.document(patient.getUserId()).get().addOnCompleteListener(task -> {
            dialog.dismiss();
            DocumentSnapshot document = task.getResult();
            arr=(List<Object>) document.get("tasks");
            Map<String,String> map;
            if(arr==null){
                arr=new ArrayList<>();
                map=new HashMap<>();
            }
            else
                map= (Map<String, String>) arr.get(arr.size()-1);
            if(map==null)
                map=new HashMap<>();
            if (!map.get("prosthesis").equals("date")) {
                prosthesis.setChecked(true);

            }
            if (!map.get("amalgam").equals("date")) {
                operative.setChecked(true);

            }
            if (!map.get("fixedProsthodontics").equals("date")) {
                fixedpro.setChecked(true);

            }
            if (!map.get("endodontics").equals("date")) {
                endodonties.setChecked(true);

            }
            if (!map.get("composite").equals("date")) {
                operativepostive.setChecked(true);

            }
        }).addOnFailureListener(e -> {
            dialog.dismiss();
        });

    }
    public void onViewClicked(View view) {
        ShowDialog();
    }

    private void ShowDialog() {
        Dialog dialog = new Dialog(SendNotification.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button btnsend = dialog.findViewById(R.id.btnsend);
        Button btncancel = dialog.findViewById(R.id.btncancel);
        textdate = dialog.findViewById(R.id.textdate);
        texttime = dialog.findViewById(R.id.texttime);
        ImageView imagedate = dialog.findViewById(R.id.imagedate);
        ImageView imagetime = dialog.findViewById(R.id.imagetime);
        dialog.show();
        imagetime.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(SendNotification.this, timelistner, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        btncancel.setOnClickListener(v -> dialog.dismiss());
        imagedate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog DPD = new DatePickerDialog(
                    SendNotification.this, mDateSetListener, mYear, mMonth,
                    mDay);
            DPD.show();
        });

        btnsend.setOnClickListener(v -> {

            collectionReference.document(patient.getUserId()).get().addOnCompleteListener(myTask -> {
                Map<String,Object> tempMap=myTask.getResult().getData();
                if((!tempMap.get("done").equals("1")&&doneOrBook.equals("done"))||(tempMap.get("done").equals("1")&&!tempMap.get("book").equals("1")&&doneOrBook.equals("book"))
                        ||(tempMap.get("done").equals("1")&&tempMap.get("book").equals("1")&&doneOrBook.equals("follow"))){
                    Dialog dialog1= new Dialog(SendNotification.this);
                    dialog1.setContentView(R.layout.loading_bar);
                    dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog1.show();
                    String date = textdate.getText().toString();
                    String time = texttime.getText().toString();
                    if (!date.isEmpty() && !time.isEmpty()) {
                        String message = "Waiting";

                        Map<String, String> notificationdata = new HashMap<>();
                        notificationdata.put("message", message);
                        notificationdata.put("date", date);
                        notificationdata.put("time", time);
                        notificationdata.put("from", currentuserid);
                        notificationdata.put("done", "1");
                        notificationdata.put("seen", "1");
                        notificationdata.put("imgUrl", currentuserImage);
                        notificationdata.put("userName", currentuserName);
                        //Toast.makeText(this,  getIntent().getStringExtra("studentName"), Toast.LENGTH_SHORT).show();
                        notificationdata.put("type", doneOrBook);
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);



                        mFirebaseFirestore.collection("PatientProfile").document(patient.getUserId()).collection("Notification")
                                .add(notificationdata).addOnSuccessListener(documentReference -> {
                            dialog1.dismiss();
                            dialog.dismiss();

                            notificationdata.put("DocumentId",documentReference.getId());
                            Map<String,Object> map= (Map<String, Object>) tempMap.get("notification");
                            if(map==null)
                                map=new HashMap<>();
                            HashMap<String,Object> bossMap=new HashMap<>();
                            map.put((map.size())+"",notificationdata);
                            bossMap.put("notification",map);
                            collectionReference.document(patient.getUserId())
                                    .update(bossMap);



                            Toast.makeText(SendNotification.this, "Send success....", Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            Map<String,Object> m =new HashMap<>();

                            m.put(doneOrBook,"1");
                            mFirebaseFirestore.collection("PatientProfile").document(patient.getUserId())
                                    .update(m).addOnCompleteListener(task -> {
                                returnIntent.putExtra(doneOrBook,"1");
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            });
                        }).addOnFailureListener(e -> {
                            dialog1.dismiss();
                            dialog.dismiss();
                            Toast.makeText(SendNotification.this, "Error...", Toast.LENGTH_SHORT).show();
                        });
                    }else {
                        dialog1.dismiss();
                        Toast.makeText(this, "Please Choose Time and Date", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    Toast.makeText(this, "Sorry,this patient not available", Toast.LENGTH_SHORT).show();
                    finish();

                }

            });

        });
    }
    private TimePickerDialog.OnTimeSetListener timelistner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String AM_PM;
            if (hourOfDay < 12) {
                AM_PM = "AM";
            } else {
                AM_PM = "PM";
            }
            texttime.setText(String.valueOf(hourOfDay) + " : " + minute + " " + AM_PM);
        }
    };
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            updateDisplay();
        }
    };
    private void updateDisplay() {
        // TODO Auto-generated method stub
        if (mMonth + 1 < 10 && mDay > 10)
            textdate.setText(new StringBuilder()
                    .append(mYear).append("-").append("0").append(mMonth + 1).append("-").append(mDay));
        else if (mMonth + 1 > 10 && mDay < 10)
            textdate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-").append(mMonth + 1).append("-").append("0").append(mDay));

        else if (mMonth + 1 < 10 && mDay < 10)
            textdate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-").append("0").append(mMonth + 1).append("-").append("0").append(mDay));
        else
            textdate.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
    }
}
