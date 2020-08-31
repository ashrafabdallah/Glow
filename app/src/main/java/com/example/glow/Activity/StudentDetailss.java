package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
import com.example.glow.model.Student;
import com.example.glow.network.InternetConnection;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentDetailss extends AppCompatActivity {
    private TextView textName, textnumpatient, textphone, textsection, textlevel;
    private CircleImageView imageProfile;
    private ImageView drawerBackgroundImage;
    private RatingBar ratingBar2;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detailss);
        /*Toolbar toolbar = findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);*/
        textName = findViewById(R.id.drawerUserName);
        textnumpatient = findViewById(R.id.textnumpatient);
        textphone = findViewById(R.id.textphone);
        textsection = findViewById(R.id.textsection);
        textlevel = findViewById(R.id.textlevel);
        imageProfile = findViewById(R.id.drawerImageProfile);
        drawerBackgroundImage = findViewById(R.id.drawerBackgroundImage);

        ratingBar2 = findViewById(R.id.ratingBar2);

        Intent intent = getIntent();

            Student studentdetailes = intent.getExtras().getParcelable("studentdetailes");
            textName.setText(studentdetailes.getUsername());
            textnumpatient.setText(studentdetailes.getNumPatient() + "");
            textlevel.setText(studentdetailes.getLevel());
            textphone.setText(studentdetailes.getPhone());
            textsection.setText(studentdetailes.getSection());
            if (studentdetailes.getImageUrl() == null) {
                Picasso.get().load(R.drawable.logo).into(imageProfile);
                Picasso.get().load(R.drawable.logo).into(drawerBackgroundImage);
            }
            Picasso.get().load(studentdetailes.getImageUrl()).into(imageProfile);
            Picasso.get().load(studentdetailes.getImageUrl()).into(drawerBackgroundImage);
            ratingBar2.setRating(studentdetailes.getRate());
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

}
