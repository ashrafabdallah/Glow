package com.example.glow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.network.InternetConnection;
import com.example.glow.response.login.Customers;
import com.example.glow.response.login.Data;
import com.example.glow.response.login.LoginResponse;
import com.example.glow.util.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    EditText editemail;
    EditText editpassword;
    TextView textforgetpassword;
    Button login;
    Guideline guideline11;
    TextView signup;
    SharedPreferences sharedPreferences;
    private String adminEmail, adminPassword;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        editemail = findViewById(R.id.editText);
        editpassword = findViewById(R.id.editText2);
        textforgetpassword = findViewById(R.id.textView48);
        login = findViewById(R.id.imageButton);
        guideline11 = findViewById(R.id.guideline11);
        signup = findViewById(R.id.signup);
        ImageView toggle = findViewById(R.id.toggle);
        textforgetpassword.setOnClickListener(v -> startActivity(new Intent(Login.this, ForgetPassword.class)));
        toggle.setOnTouchListener((v, event) -> {
            if (!TextUtils.isEmpty(editpassword.getText().toString().trim())) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        editpassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        toggle.setImageResource(R.drawable.ic_eye);
                        break;
                    case MotionEvent.ACTION_UP:
                        editpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        toggle.setImageResource(R.drawable.ic_hide);
                        break;
                }
            }
            return true;
        });

    }

    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.imageButton:
                String email = editemail.getText().toString().trim();
                String password = editpassword.getText().toString().trim();
                if (validation(email, password)) {
                    if (InternetConnection.checkConnection(Login.this)) {
                        loginUser(email, password);
                    } else {
                        Toast.makeText(this, "No Internet...", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.signup:
                finish();
                startActivity(new Intent(Login.this, SignUp.class));
                break;
        }
    }

    private void loginUser(String email, String password) {


        adminEmail = "admin@gmail.com";
        adminPassword = "admin123456789";
        if (email.equals(adminEmail) && password.equals(adminPassword)) {
            final Dialog dialog = new Dialog(Login.this);
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            firebaseAuth.signInWithEmailAndPassword(adminEmail, adminPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    dialog.dismiss();

                    sharedPreferences.edit().putString(Constant.TYPE, "admin").apply();
                    sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();
                    finish();
                    startActivity(new Intent(Login.this, AdminHome.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            final Dialog dialog = new Dialog(Login.this);
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    final String currentUserId = user.getUid();
                    collectionReference
                            //The following query returns all cities with userId = ال مبعوت
                            .whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                    assert queryDocumentSnapshots != null;
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            String userId = snapshot.getString("userId");
                                            String type = snapshot.getString("type");
                                            dialog.dismiss();
                                            Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                            sharedPreferences.edit().putString(Constant.TYPE, type).apply();
                                            sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();

                                            if (type.equals("junior")) {
                                                Intent i = new Intent(Login.this, HomeJunior.class);
                                                i.putExtra("userid", userId);
                                                sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();
                                                sharedPreferences.edit().putString(Constant.CUSTOMERSID, userId).apply();
                                                startActivity(i);
                                                finish();
                                            } else if (type.equals("patient")) {
                                                Intent i = new Intent(Login.this, HomePatient.class);
                                                i.putExtra("userid", userId);
                                                sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();
                                                sharedPreferences.edit().putString(Constant.CUSTOMERSID, userId).apply();
                                                startActivity(i);
                                                finish();
                                            } else if (type.equals("student")) {
                                                Intent i = new Intent(Login.this, HomeStudent.class);
                                                i.putExtra("userid", userId);
                                                sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();
                                                sharedPreferences.edit().putString(Constant.CUSTOMERSID, userId).apply();
                                                startActivity(i);
                                                finish();
                                            }
                                            String token = FirebaseInstanceId.getInstance().getToken();
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("userToken", token);

                                            collectionReference.document(FirebaseInstanceId.getInstance().getId()).update(map);
                                        }
                                    }

                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    public boolean validation(String email, String password) {
        boolean valid = true;
        if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            editemail.setError("Please Enter your email.");
        } else {
            editemail.setError(null);
        }
        if (password.isEmpty()) {
            valid = false;
            editpassword.setError("Please Enter your password");
        } else {
            editpassword.setError(null);
        }

        return valid;
    }
}


