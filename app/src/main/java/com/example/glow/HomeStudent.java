package com.example.glow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.About;
import com.example.glow.Activity.DentailHistory;
import com.example.glow.Activity.StudentCheckup;
import com.example.glow.Activity.checkUpfromstudentChoosePatient;
import com.example.glow.model.Patient;
import com.example.glow.response.student.PatientListFromStudentRegisterToPatiebtProfile;
import com.example.glow.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeStudent extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    final static int BOOK_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentUserId = null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("StudentProfile");
    DrawerLayout drawerLayout;
    CircleImageView imageProfile;
    ImageView imageBackground;
    TextView studentName;
    TextView numberOfPatient;
    RatingBar ratingBar3;
    RecyclerView recyclerView;
    int index;
    String userIdBooked;
    List<Map<String, Object>> patientData;
    Adapter adapter;
    Toolbar appbar;
    TextView textNoitem;
    SwipeRefreshLayout swipStudent;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();

        swipStudent = findViewById(R.id.swipStudent);
        swipStudent.setOnRefreshListener(this);
        swipStudent.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();
        textNoitem = findViewById(R.id.textNoitem);

        appbar = findViewById(R.id.toolbar);
        appbar.setTitle("Student Home");
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawsvg);
        recyclerView = findViewById(R.id.recyclerViewHomeStudent);
        setupDrawer();
        start();
        db.collection("PatientProfile")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
//                                    list = new ArrayList<>();
//                                    onChange();
                                    Log.d("TAG", "New Msg: " );
                                    break;
                                case MODIFIED:
                                    patientData=new ArrayList<>();
                                    onChange();
                                    adapter.notifyDataSetChanged();
                                    Log.d("TAG", "Modified Msg: " );
                                    break;
                                case REMOVED:
//                                    list = new ArrayList<>();
//                                    onChange();
                                    Log.d("TAG", "Removed Msg: " );
                                    break;
                            }
                        }

                    }
                });
    }

    void start() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
            if (snapshot != null) {
                studentName.setText(snapshot.getString("username"));
                Map<String, Object> m = (Map<String, Object>) snapshot.get("rate");
                Map<String, Object> m2 = (Map<String, Object>) snapshot.get("patient");
                if (m == null)
                    m = new HashMap<>();
                float sum = 0;
                for (String key : m.keySet()) {
                    String value = m.get(key).toString();
                    sum += Float.parseFloat(value);
                }
                numberOfPatient.setText((m2 != null) ? m2.size() + "" : "0");
                ratingBar3.setRating(sum);
                if (snapshot.getString("imageUrl") != null) {
                    Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageProfile);
                    Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageBackground);
                } else {
                    imageProfile.setImageResource(R.drawable.photo);
                    imageBackground.setImageResource(R.drawable.photo);
                }
            }
        }).addOnCompleteListener(task -> {
            if (swipStudent.isRefreshing()) {
                swipStudent.setRefreshing(false);

            }
            dialog.dismiss();
            patientData = new ArrayList<>();
            adapter = new Adapter(patientData);
            recyclerView.setLayoutManager(new LinearLayoutManager(HomeStudent.this));
            recyclerView.setAdapter(adapter);
            //for (String id : listStudentId ){
            db.collection("PatientProfile")
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                Map<String, Object> m = document1.getData();
                                if(m.get("active")==null)
                                    m.put("active","0");
                                if(m.get("book")==null)
                                    m.put("book","0");
                                if(m.get("check")==null)
                                    m.put("check","0");
                                if (m.get("active").equals("1") && !m.get("book").equals("1") && m.get("check").equals("1"))
                                    patientData.add(m);
                            }
                            adapter.notifyDataSetChanged();
                            if (patientData.size() > 0)
                                textNoitem.setVisibility(View.GONE);
                            else
                                textNoitem.setVisibility(View.VISIBLE);


                        }
                    });
        });

    }
    void onChange(){
        patientData.clear();
            db.collection("PatientProfile")
                    .get()
                    .addOnCompleteListener(task1 -> {
                            task1.addOnCompleteListener(task -> {
                                patientData.clear();
                                adapter = new Adapter(patientData);
                                recyclerView.setAdapter(adapter);
                                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                    Map<String, Object> m = document1.getData();
                                    if(m.get("active")==null)
                                        m.put("active","0");
                                    if(m.get("book")==null)
                                        m.put("book","0");
                                    if(m.get("check")==null)
                                        m.put("check","0");
                                    if (m.get("active").equals("1") && !m.get("book").equals("1") && m.get("check").equals("1"))
                                        patientData.add(m);
                                    adapter.notifyDataSetChanged();

                                }

                                adapter.notifyDataSetChanged();
                                if (patientData.size() > 0)
                                    textNoitem.setVisibility(View.GONE);
                                else
                                    textNoitem.setVisibility(View.VISIBLE);
                            });

                    });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOOK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String book = data.getStringExtra("book");
                if (book.equals("1")) {
                    /*patientData.remove(index);
                    adapter.notifyDataSetChanged();*/
                    if (patientData.size() == 0)
                        textNoitem.setVisibility(View.VISIBLE);
                    Map<String, Object> m = new HashMap<>();
                    m.put("book", "1");
                    db.collection("PatientProfile").document(userIdBooked).update(m);
                }
            }
        }
    }

    void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        studentName = hView.findViewById(R.id.drawerUserName);
        imageProfile = hView.findViewById(R.id.drawerImageProfile);
        imageBackground = hView.findViewById(R.id.drawerBackgroundImage);
        numberOfPatient = hView.findViewById(R.id.numberOfPatient);
        ratingBar3 = hView.findViewById(R.id.ratingBar3);

        collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
            if (snapshot != null) {
                studentName.setText(snapshot.getString("username"));
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageProfile);
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageBackground);
            }
        });
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.Checkup:
                            startActivity(new Intent(HomeStudent.this, checkUpfromstudentChoosePatient.class));
                            break;
                        case R.id.Dental_history:
                            startActivity(new Intent(HomeStudent.this, DentailHistory.class).putExtra("type", "book"));
                            break;
                        case R.id.Update:
                            startActivity(new Intent(HomeStudent.this, StudentProfile.class));
                            break;
                        case R.id.about:
                            startActivity(new Intent(HomeStudent.this, About.class));
                            break;
                        case R.id.Logout:
                            startActivity(new Intent(HomeStudent.this, Login.class));

                            sharedPreferences.edit().putBoolean(Constant.LOGIN, false).apply();

                            FirebaseAuth.getInstance().signOut();
                            finish();

                            break;
                    }
                    return true;
                });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, appbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onRefresh() {
        adapter.clear();
        start();
    }


    class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {
        List<Map<String, Object>> patientData;
        public Adapter(List<Map<String, Object>> patientData) {
            this.patientData=patientData;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(HomeStudent.this).inflate(R.layout.item_home_student, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {
            holder.textView.setText(patientData.get(i).get("username").toString());
            if (patientData.get(i).get("imageUrl") != null)
                Picasso.get().load(patientData.get(i).get("imageUrl").toString()).placeholder(R.drawable.photo).into(holder.img);
            else
                holder.img.setImageResource(R.drawable.photo);
            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(HomeStudent.this, SendNotification.class);
                Map<String, Object> map = patientData.get(i);
                Patient patient = new Patient();
                if (map != null) {
                    patient.setUserId(map.get("userId").toString());
                    patient.setUserName(map.get("username").toString());
                    if (map.get("imageUrl") != null)
                        patient.setImageUrl(map.get("imageUrl").toString());
                    patient.setPhone(map.get("phone").toString());
                    patient.setAddress(map.get("address").toString());
                }
                intent.putExtra("data", patient);
                intent.putExtra("type", "student");
                intent.putExtra("studentName", studentName.getText());
                index = i;
                userIdBooked = map.get("userId").toString();
                startActivityForResult(intent, BOOK_CODE);
            });

        }

        @Override
        public int getItemCount() {
            return patientData.size();
        }

        public void clear() {
            patientData.clear();
            notifyDataSetChanged();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            CircleImageView img;
            TextView textView;
            CardView card;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.patientImageHomeStudent);
                textView = itemView.findViewById(R.id.patientNameHomeStudent);
                card = itemView.findViewById(R.id.cardHomeStudent);
            }
        }
    }
}

