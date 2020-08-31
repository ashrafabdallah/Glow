package com.example.glow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.glow.util.Constant;

public class Splash extends AppCompatActivity {
  SharedPreferences sharedPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
    boolean login = sharedPreferences.getBoolean(Constant.LOGIN, false);
    String type = sharedPreferences.getString(Constant.TYPE, null);
    if (login) {
      if (type.equals("junior")) {
        Intent i = new Intent(Splash.this, HomeJunior.class);
        startActivity(i);
        finish();
      } else if (type.equals("patient")) {
        Intent i = new Intent(Splash.this, HomePatient.class);

        startActivity(i);
        finish();
      } else if (type.equals("student")) {
        Intent i = new Intent(Splash.this, HomeStudent.class);
        startActivity(i);
        finish();
      } else if (type.equals("admin")) {

        startActivity(new Intent(Splash.this, AdminHome.class));
        finish();
      }
    } else {

      startActivity(new Intent(Splash.this, Login.class));
      finish();
    }


  }
}
