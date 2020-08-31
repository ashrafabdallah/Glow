package com.example.glow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.About;
import com.example.glow.Activity.DentailHistory;
import com.example.glow.Activity.check_up_from_juinor_choosepatient;
import com.example.glow.model.Patient;
import com.example.glow.network.InternetConnection;
import com.example.glow.util.Constant;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeJunior extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    final static int DONE_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentUserId = null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("juniorProfile");
    ImageButton draw;
    byte flag = 0;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView imageProfile;
    ImageView imageBackground;
    TextView patientName;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    AdapterPatientListFromJunior adapter;
    List<Patient> list;
    TextView textNoitem;
    int index;
    String userIdBooked;
    Toolbar appbar;
    SwipeRefreshLayout swipjunior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_junior);
        swipjunior = findViewById(R.id.swipjunior);
        swipjunior.setOnRefreshListener(this);
        swipjunior.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();

        textNoitem = findViewById(R.id.textNoitem);
        recyclerView = findViewById(R.id.recyclerViewHomeJunior);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appbar = findViewById(R.id.toolbar);
        appbar.setTitle("Junior Home");
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawsvg);
        list = new ArrayList<>();
        adapter = new AdapterPatientListFromJunior(this);
        recyclerView.setAdapter(adapter);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();

        setupDrawer();
        View hView = navigationView.getHeaderView(0);
        patientName = hView.findViewById(R.id.drawerUserName);
        imageProfile = hView.findViewById(R.id.drawerImageProfile);
        imageBackground = hView.findViewById(R.id.drawerBackgroundImage);
        if (InternetConnection.checkConnection(HomeJunior.this)) {
            start();
        } else {
            Toast.makeText(this, "No Internet.....", Toast.LENGTH_SHORT).show();
        }

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
                                    list = new ArrayList<>();
                                    onChange();
                                    adapter.clear();

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
                dialog.dismiss();
                patientName.setText(snapshot.getString("username"));
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageProfile);
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageBackground);
            }
        });
        db.collection("PatientProfile").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.dismiss();
                if (swipjunior.isRefreshing()) {
                    swipjunior.setRefreshing(false);

                }
                for (DocumentSnapshot querySnapshot : task.getResult()) {
                    if(querySnapshot.getString("active")!=null)
                    if (querySnapshot.getString("active").equals("1")) {
                        textNoitem.setVisibility(View.GONE);
                        Patient patientdetailes = new Patient(querySnapshot.getString("active"), querySnapshot.getString("address")
                                , querySnapshot.getString("age"), querySnapshot.getString("imageUrl"), querySnapshot.getString("username")
                                , querySnapshot.getString("userId"), querySnapshot.getString("phone")
                        );
                        patientdetailes.setDone(querySnapshot.getString("done"));
                        patientdetailes.setCheck(querySnapshot.getString("check"));
                        if (patientdetailes.getActive().equals("1") && !patientdetailes.getDone().equals("1") && !patientdetailes.getCheck().equals("1"))
                            list.add(patientdetailes);
                    } else {
                        textNoitem.setVisibility(View.VISIBLE);
                    }
                }
                if (list.size() == 0)
                    textNoitem.setVisibility(View.VISIBLE);
                else
                    textNoitem.setVisibility(View.GONE);

                adapter.setList(list);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(HomeJunior.this, "Problem......", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            if (swipjunior.isRefreshing()) {
                swipjunior.setRefreshing(false);

            }

        });


    }
    void onChange(){
        db.collection("PatientProfile").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                list=new ArrayList<>();
                for (DocumentSnapshot querySnapshot : task.getResult()) {
                    if (querySnapshot.getString("active").equals("1")) {
                        textNoitem.setVisibility(View.GONE);
                        Patient patientdetailes = new Patient(querySnapshot.getString("active"), querySnapshot.getString("address")
                                , querySnapshot.getString("age"), querySnapshot.getString("imageUrl"), querySnapshot.getString("username")
                                , querySnapshot.getString("userId"), querySnapshot.getString("phone")
                        );
                        patientdetailes.setDone(querySnapshot.getString("done"));
                        patientdetailes.setCheck(querySnapshot.getString("check"));
                        if (patientdetailes.getActive().equals("1") && !patientdetailes.getDone().equals("1") && !patientdetailes.getCheck().equals("1"))
                            list.add(patientdetailes);
                    } else {
                        textNoitem.setVisibility(View.VISIBLE);
                    }
                }
                if (list.size() == 0)
                    textNoitem.setVisibility(View.VISIBLE);
                else
                    textNoitem.setVisibility(View.GONE);
                adapter.setList(list);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(HomeJunior.this, "Problem......", Toast.LENGTH_SHORT).show();
            if (swipjunior.isRefreshing()) {
                swipjunior.setRefreshing(false);

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DONE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String done = data.getStringExtra("done");
                if (done.equals("1")) {
                    /*list.remove(index);
                    adapter.notifyDataSetChanged();*/
                    if (list.size() == 0)
                        textNoitem.setVisibility(View.VISIBLE);

                    Map<String, Object> m = new HashMap<>();
                    m.put("done", "1");
                    db.collection("PatientProfile").document(userIdBooked).update(m);
                }
            }
        }
    }

    void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.Checkup:
                            startActivity(new Intent(this, check_up_from_juinor_choosepatient.class));
                            break;
                        case R.id.Dental_history:
                            startActivity(new Intent(this, DentailHistory.class).putExtra("type", "done"));
                            break;
                        case R.id.Update:
                            startActivity(new Intent(this, JuniorProfile.class));
                            break;
                        case R.id.about:
                            startActivity(new Intent(this, About.class));
                            break;
                        case R.id.Logout:
                            startActivity(new Intent(this, Login.class));
                            FirebaseAuth.getInstance().signOut();
                            sharedPreferences.edit().putBoolean(Constant.LOGIN, false).apply();
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
        start();
        adapter.clear();
    }


    public class AdapterPatientListFromJunior extends RecyclerView.Adapter<AdapterPatientListFromJunior.PatientViewHolder> {
        List<Patient> list;
        Context context;

        public AdapterPatientListFromJunior(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_junior, parent, false);

            return new PatientViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
            Patient patient = list.get(position);
            holder.textname.setText(patient.getUserName());
            Picasso.get().load(patient.getImageUrl()).placeholder(R.drawable.photo).into(holder.circleImageView);
            holder.container.setOnClickListener(v -> {
                Intent i = new Intent(context, SendNotification.class);
                i.putExtra("data", patient);
                i.putExtra("type", "junior");
                i.putExtra("studentName", patientName.getText());
                index = position;
                userIdBooked = patient.getUserId();
                startActivityForResult(i, DONE_CODE);
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public void setList(List<Patient> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        class PatientViewHolder extends RecyclerView.ViewHolder {
            CircleImageView circleImageView;
            TextView textname;
            Button btnDone;
            ConstraintLayout container;

            public PatientViewHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.circleImageView);
                textname = itemView.findViewById(R.id.textname);
                container = itemView.findViewById(R.id.container);

            }
        }

    }
}
