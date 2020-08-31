package com.example.glow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.glow.ActivityAdmin.FragmentDentist;
import com.example.glow.ActivityAdmin.FragmentPatient;
import com.example.glow.ActivityAdmin.FragmentStudent;
import com.example.glow.util.Constant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHome extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

       Toolbar toolbar =  findViewById(R.id.toolbar);
       // toolbar.setSubtitle("Test Subtitle");
        toolbar.inflateMenu(R.menu.admin);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Logout2:
                        startActivity(new Intent(AdminHome.this, Login.class));
                        FirebaseAuth.getInstance().signOut();
                        sharedPreferences.edit().putBoolean(Constant.LOGIN, false).apply();
                        finish();
                        break;

                }

                return false;
            }
        });
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentStudent()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.student:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentStudent()).commit();

                        break;
                    case R.id.dentist:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentDentist()).commit();

                        break;
                    case R.id.patient:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentPatient()).commit();

                        break;
                }
                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout2:
                startActivity(new Intent(this, Login.class));
                FirebaseAuth.getInstance().signOut();
                sharedPreferences.edit().putBoolean(Constant.LOGIN, false).apply();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
