package com.example.glow.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.JuniorDetailss;
import com.example.glow.Activity.StudentDetailss;
import com.example.glow.HomePatient;
import com.example.glow.R;
import com.example.glow.model.Junior;
import com.example.glow.model.Student;
import com.example.glow.network.InternetConnection;
import com.example.glow.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener{
    List<Map<String, Object>> arr_notification;
    TextView textNoitem;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    String currentUserId=null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    String from;
    Student student;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    int sum;
    NotificationAdapter adapter;
    SwipeRefreshLayout swipnotification;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId=user.getUid();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //arr_notification= (List<Map<String, Object>>) getIntent().getSerializableExtra("data");
        // if(arr_notification==null)
        arr_notification=new ArrayList<>();
        RecyclerView notification_recyclerView = findViewById(R.id.notification_recyclerView);
        textNoitem = findViewById(R.id.textNoitem);
        swipnotification = findViewById(R.id.swipnotification);
        swipnotification.setOnRefreshListener(this);
        swipnotification.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        adapter = new NotificationAdapter(arr_notification, NotificationActivity.this);
        notification_recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        notification_recyclerView.setAdapter(adapter);
        if(arr_notification.size()==0)
            textNoitem.setVisibility(View.VISIBLE);
        start();

    }
    void start(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        collectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            if (swipnotification.isRefreshing()) {
                swipnotification.setRefreshing(false);
            }
            dialog.dismiss();
            Map<String,Object> boss = task.getResult().getData();
            Map<String,Object> notification= (Map<String, Object>) boss.get("notification");
            List<String> keys;
            if(notification!=null)
                keys = new ArrayList<>(notification.keySet());
            else
                keys=new ArrayList<>();
            Collections.sort(keys);

            if(notification!=null)
                for(int i=keys.size()-1;i>=0;i--){
                    String key=keys.get(i);
                    System.out.println(key);
                    Map<String,Object> m = (Map<String, Object>) notification.get(key);
                    if(m!=null){
                        m.put("seen", "0");
                        arr_notification.add(m);
                    }
                }

            if(arr_notification.size()==0)
                textNoitem.setVisibility(View.VISIBLE);
            else
                textNoitem.setVisibility(View.INVISIBLE);
            HashMap<String,Object> bossMap=new HashMap<>();
            HashMap<String,Object> map=new HashMap<>();
            int i=arr_notification.size()-1;
            for(Map<String,Object> m:arr_notification){
                map.put(i+"",m);
                i--;
            }

            bossMap.put("notification",map);
            collectionReference.document(currentUserId)
                    .update(bossMap);
            adapter.notifyDataSetChanged();
        });
    }
    @Override
    public void onRefresh() {
        if(adapter!=null){
            adapter.clrar();
            start();
        }else {
            if (swipnotification.isRefreshing()) {
                swipnotification.setRefreshing(false);

            }
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public void FetchData(Intent intent, String from) {

        db.collection("StudentProfile").document(from).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documents = task.getResult();
                float sum = 0;
                Map<String, Object> data2 = (Map<String, Object>) documents.getData().get("rate");
                Map<String, Object> patient = (Map<String, Object>) documents.getData().get("patient");
                if (patient == null)
                    patient = new HashMap<>();

                if (data2 == null)
                    data2 = new HashMap<>();

                for (String key : data2.keySet()) {
                    String value = data2.get(key).toString();
                    sum += Float.parseFloat(value);
                }
                HashMap<String, Object> m = (HashMap<String, Object>) documents.getData();
                student = new Student(m.get("imageUrl").toString(), m.get("level").toString(), m.get("phone").toString(), sum / data2.size(), m.get("section").toString(), m.get("university").toString(), m.get("username").toString(), patient.size());
                intent.putExtra("studentdetailes",  student);
                startActivity(intent);
            }

        });
    }

    class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyHolder> {
        List<Map<String,Object>> arr;
        Context context;
        public NotificationAdapter(List<Map<String,Object>> arr, Context context) {
            this.arr = arr;
            this.context = context;
        }
        public  void clrar(){

            if(arr!=null){
                arr.clear();


            }else {
                arr=new ArrayList<>();
            }
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.notification_item,parent,false));
        }
        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {
            Map<String, Object> m=arr.get(i);
            holder.studentNameNotification.setText(m.get("userName").toString());
            if(m.get("imgUrl")!=null)
                Picasso.get().load(m.get("imgUrl").toString())
                        .placeholder(R.drawable.photo).into(holder.circleImageView);
            else
                holder.circleImageView.setImageResource(R.drawable.photo);
            holder.notificationData.setText(m.get("date")+"\nAt\n"+m.get("time"));
            holder.notificationCard.setOnClickListener(v -> {
                String type=m.get("type").toString();
                from=m.get("from").toString();
                Intent intent;
                if(type.equals("done")) {
                    intent = new Intent(context, JuniorDetailss.class);
                    FeatchData2(intent,from);
                }
                else {
                    intent = new Intent(context, StudentDetailss.class);
                    FetchData(intent,from);
                }

            });
        }
        @Override
        public int getItemCount() {
            return arr_notification.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView notificationData;
            TextView studentNameNotification;
            CircleImageView circleImageView;
            CardView notificationCard;
            public MyHolder(@NonNull View v) {
                super(v);
                notificationData=v.findViewById(R.id.notificationData);
                studentNameNotification=v.findViewById(R.id.studentNameNotification);
                notificationCard=v.findViewById(R.id.notificationCard);
                circleImageView=v.findViewById(R.id.circleImageView);
            }
        }
    }

    private void FeatchData2(Intent intent, String from) {

        db.collection("juniorProfile").document(from).get().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                DocumentSnapshot documents = task.getResult();
                Map<String, Object> patient= (Map<String, Object>) documents.getData().get("patient");
                if (patient==null)
                    patient=new HashMap<>();
                HashMap<String, Object> m = (HashMap<String, Object>) documents.getData();
                Junior junior=new Junior(m.get("imageUrl").toString(),m.get("level").toString()
                        ,m.get("phone").toString(),m.get("section").toString()
                        ,m.get("university").toString(),m.get("username").toString()
                        ,patient.size());
                intent.putExtra("juniorData",junior);
                startActivity(intent);

            }

        });
    }


}
