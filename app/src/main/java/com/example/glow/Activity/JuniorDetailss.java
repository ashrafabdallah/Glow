package com.example.glow.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glow.R;
import com.example.glow.model.Junior;
import com.squareup.picasso.Picasso;

public class JuniorDetailss extends AppCompatActivity {
    private TextView textName,textnumpatient,textphone,textsection,textlevel,textuniversity;
    private CircleImageView imageProfile;
    ImageView drawerBackgroundImage;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_junior_detailss);

       /* Toolbar toolbar=findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        */
        Intent intent = getIntent();

        textName=findViewById(R.id.drawerUserName);
        textnumpatient=findViewById(R.id.textnumpatient);
        textphone=findViewById(R.id.textphone);
        textsection=findViewById(R.id.textsection);
        textlevel=findViewById(R.id.textlevel);
        imageProfile=findViewById(R.id.drawerImageProfile);
        drawerBackgroundImage=findViewById(R.id.drawerBackgroundImage);
        textuniversity=findViewById(R.id.textuniversity);


        Junior junior = intent.getExtras().getParcelable("juniorData");
        textName.setText(junior.getUsername());
        textnumpatient.setText(junior.getNumPatient()+"");
        textlevel.setText(junior.getLevel());
        textphone.setText(junior.getPhone());
        textsection.setText(junior.getSection());
        textuniversity.setText(junior.getUniversity());
        if(junior.getImageUrl()==null) {
            Picasso.get().load(R.drawable.photo).into(imageProfile);
            Picasso.get().load(R.drawable.photo).into(drawerBackgroundImage);
        }
        Picasso.get().load(junior.getImageUrl()).into(imageProfile);
        Picasso.get().load(junior.getImageUrl()).into(drawerBackgroundImage);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
