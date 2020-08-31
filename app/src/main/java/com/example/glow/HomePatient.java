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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.About;
import com.example.glow.Activity.DentalPatientDetail;
import com.example.glow.Activity.JuniorDetailss;
import com.example.glow.Activity.Selectstudentfrompatienthome;
import com.example.glow.Activity.StudentDetailss;
import com.example.glow.notification.NotificationActivity;
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
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomePatient extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    String currentUserId = null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("PatientProfile");
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircleImageView imageProfile;
    ImageView imageBackground;
    TextView patientName;
    Switch activeSwitch;
    String active = "0";
    String check = "0";
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    List<Object> arr;
    List<Map<String, Object>> arr_notification;
    Toolbar appbar;
    TextView textNoitem;
    TextView itemMessagesBadgeTextView;
    View badgeLayout;
    MenuItem itemMessages;
    ImageView iconButtonMessages;
    int sum;
    Adapter adapter;
    List<Map<String, Object>> studentData;
    SwipeRefreshLayout patientntSwip;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messages, menu);
        arr_notification = new ArrayList<>();
        itemMessages = menu.findItem(R.id.menu_messages);
        badgeLayout = itemMessages.getActionView();
        itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden

        iconButtonMessages = (ImageView) badgeLayout.findViewById(R.id.badge_icon_button);

        iconButtonMessages.setOnClickListener(view -> {
            /*HashMap<String,Object> bossMap=new HashMap<>();
            HashMap<String,Object> map=new HashMap<>();
            int z=0;

            for(Map<String,Object> m:arr_notification){
                map.put(z+"",m);
                z++;
            }
            bossMap.put("notification",map);
            collectionReference.document(currentUserId)
                    .update(bossMap);*/
            Intent i = new Intent(HomePatient.this, NotificationActivity.class);
            startActivity(i);
            itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_patient);
        patientntSwip = findViewById(R.id.patientntSwip);
        patientntSwip.setOnRefreshListener(this);
        patientntSwip.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();

        currentUserId = user.getUid();
        textNoitem = findViewById(R.id.textNoitem);
        appbar = findViewById(R.id.toolbar);
        appbar.setTitle("Patient Home");
        setSupportActionBar(appbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawsvg);
        recyclerView = findViewById(R.id.recyclerViewHomeStudent);
        setupDrawer();
        View hView = navigationView.getHeaderView(0);
        patientName = hView.findViewById(R.id.drawerUserName);
        imageProfile = hView.findViewById(R.id.drawerImageProfile);
        imageBackground = hView.findViewById(R.id.drawerBackgroundImage);
        activeSwitch = hView.findViewById(R.id.patientActiveButton);
        if (check.equals("1")) {
            activeSwitch.setEnabled(false);
        } else {
            activeSwitch.setEnabled(true);
        }
        activeSwitch.setOnCheckedChangeListener(this);
        studentData = new ArrayList<>();
        adapter = new Adapter(studentData, HomePatient.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomePatient.this));
        recyclerView.setAdapter(adapter);
        start();
        db.collection("StudentProfile")
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
                                    Log.d("TAG", "New Msg: ");
                                    break;
                                case MODIFIED:
//                                    arr = new ArrayList<>();
                                    adapter.clrar();
                                    arr = new ArrayList<>();
                                    onChange();

                                    Log.d("TAG", "Modified Msg: ");
                                    break;
                                case REMOVED:
//                                    list = new ArrayList<>();
//                                    onChange();
                                    Log.d("TAG", "Removed Msg: ");
                                    break;
                            }
                        }

                    }
                });
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
                                    Log.d("TAG", "New Msg: ");
                                    break;
                                case MODIFIED:
                                    arr_notification = new ArrayList<>();
                                    onChangeNotification();
                                    Log.d("TAG", "Modified Msg: ");
                                    break;
                                case REMOVED:
//                                    list = new ArrayList<>();
//                                    onChange();
                                    Log.d("TAG", "Removed Msg: ");
                                    break;
                            }
                        }

                    }
                });
    }

    void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.Dental_history:
                            startActivity(new Intent(this, DentalPatientDetail.class));
                            break;
                        case R.id.Update:
                            startActivity(new Intent(this, PatientProfile.class));
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

    void onChange() {
        collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
        }).addOnCompleteListener(task -> {


            DocumentSnapshot document = task.getResult();
            arr = (List<Object>) document.get("tasks");
            if (arr != null) {
                CollectionReference collectionReference = db.collection("StudentProfile");
                //for (String id : listStudentId ){
                for (int i = arr.size() - 1; i >= 0; i--) {
                    Map<String, String> m = (Map<String, String>) arr.get(i);
                    if (m.get("studentID") != null) {
                        collectionReference.document(m.get("studentID")).get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot document2 = task1.getResult();
                            Map<String, Object> temp = document2.getData();
                            studentData.add(temp);
                            adapter.notifyDataSetChanged();
                            textNoitem.setVisibility(View.GONE);
                        });
                    }
                }
                if (studentData.size() == 0)
                    textNoitem.setVisibility(View.VISIBLE);
                else
                    textNoitem.setVisibility(View.INVISIBLE);

            } else {
                textNoitem.setVisibility(View.VISIBLE);
            }
        });

    }

    void onChangeNotification() {
        collectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            sum = 0;
            Map<String, Object> boss = task.getResult().getData();
            Map<String, Object> notification = (Map<String, Object>) boss.get("notification");
            if (notification != null)
                for (String key : notification.keySet()) {
                    Map<String, Object> m = (Map<String, Object>) notification.get(key);
                    if (m != null) {
                        if (m.get("seen") != null)
                            sum += Integer.parseInt(m.get("seen") + "");
                        m.put("seen", "0");
                        arr_notification.add(m);
                    }
                }
            itemMessagesBadgeTextView.setText("" + sum);
            if (sum > 0)
                itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
            else
                itemMessagesBadgeTextView.setVisibility(View.INVISIBLE);

        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!check.equals("1")) {
            active = (isChecked) ? "1" : "0";
            Map<String, Object> m = new HashMap<>();
            m.put("active", active);
            collectionReference.document(currentUserId).update(m);
        } else {

            Toast.makeText(this, "You are booked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        adapter.clrar();
        start();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {
        List<Map<String, Object>> arr;
        Context context;

        public Adapter(List<Map<String, Object>> arr, Context context) {
            this.arr = arr;
            this.context = context;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_student_admin, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {
            holder.textView.setText(arr.get(i).get("username").toString());
            if (arr.get(i).get("imageUrl") != null)
                Picasso.get().load(arr.get(i).get("imageUrl").toString()).placeholder(R.drawable.photo).into(holder.img);
            else
                holder.img.setImageResource(R.drawable.photo);
            Map<String, Object> m = (Map<String, Object>) arr.get(i).get("rate");
            if (m == null)
                m = new HashMap<>();
            float sum = 0;
            for (String key : m.keySet()) {
                String value = m.get(key).toString();
                sum += Float.parseFloat(value);
            }
            holder.ratingBar.setRating(sum / m.size());
            holder.card.setOnClickListener(v -> {
                Intent intent = new Intent(HomePatient.this, Selectstudentfrompatienthome.class);
                intent.putExtra("studentId", arr.get(i).get("userId").toString());
                startActivityForResult(intent, 1);

            });
        }

        public void clrar() {

            if (arr != null) {
                arr.clear();
            } else {
                arr = new ArrayList<>();
            }
            if (studentData != null) {
                studentData.clear();
            } else {
                studentData = new ArrayList<>();
            }
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return arr.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            CircleImageView img;
            TextView textView;
            RatingBar ratingBar;
            CardView card;

            public MyHolder(@NonNull View itemView) {
                super(itemView);

                img = itemView.findViewById(R.id.circleImageView);
                textView = itemView.findViewById(R.id.textname);
                ratingBar = itemView.findViewById(R.id.ratingBar3);
                card = itemView.findViewById(R.id.studentAdmainCard);
            }
        }
    }

    void start() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        collectionReference.document(currentUserId).get().addOnSuccessListener(snapshot -> {
            adapter.clrar();
            if (snapshot != null) {
                patientName.setText(snapshot.getString("username"));
                check = snapshot.getString("username");
                if (snapshot.getString("active") != null)
                    activeSwitch.setChecked(snapshot.getString("active").equals("1"));
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageProfile);
                Picasso.get().load(snapshot.getString("imageUrl")).placeholder(R.drawable.photo).into(imageBackground);
            }
        }).addOnCompleteListener(task -> {
            if (patientntSwip.isRefreshing()) {
                patientntSwip.setRefreshing(false);

            }
            dialog.dismiss();
            DocumentSnapshot document = task.getResult();
            arr = (List<Object>) document.get("tasks");
            if (arr != null) {
                CollectionReference collectionReference = db.collection("StudentProfile");
                //for (String id : listStudentId ){
                for (int i = arr.size() - 1; i >= 0; i--) {
                    Map<String, String> m = (Map<String, String>) arr.get(i);
                    if (m.get("studentID") != null) {
                        collectionReference.document(m.get("studentID")).get().addOnCompleteListener(task1 -> {
                            DocumentSnapshot document2 = task1.getResult();
                            Map<String, Object> temp = document2.getData();
                            studentData.add(temp);
                            adapter.notifyDataSetChanged();
                            textNoitem.setVisibility(View.GONE);
                        });
                    }
                }
                if (studentData.size() == 0)
                    textNoitem.setVisibility(View.VISIBLE);
                else
                    textNoitem.setVisibility(View.INVISIBLE);

            } else {
                textNoitem.setVisibility(View.VISIBLE);
            }
        });
        // Notification
        collectionReference.document(currentUserId).get().addOnCompleteListener(task -> {
            sum = 0;
            Map<String, Object> boss = task.getResult().getData();
            Map<String, Object> notification = (Map<String, Object>) boss.get("notification");
            if (notification != null)
                for (String key : notification.keySet()) {
                    Map<String, Object> m = (Map<String, Object>) notification.get(key);
                    if (m != null) {
                        if (m.get("seen") != null)
                            sum += Integer.parseInt(m.get("seen") + "");
                        m.put("seen", "0");
                        arr_notification.add(m);
                    }
                }
            itemMessagesBadgeTextView.setText("" + sum);
            if (sum > 0)
                itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
            else
                itemMessagesBadgeTextView.setVisibility(View.INVISIBLE);

        });
    }


}
