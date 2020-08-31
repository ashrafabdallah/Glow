package com.example.glow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.network.InternetConnection;
import com.example.glow.network.RetrofitClient;
import com.example.glow.network.api.Api;
import com.example.glow.request.Register;
import com.example.glow.response.register.Customers;
import com.example.glow.response.register.Data;
import com.example.glow.response.register.RegisterResponse;
import com.example.glow.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");
    EditText nameFiled;
    EditText emailFailed;
    EditText PasswordFailed;
    Button Register;
    TextView Signin;
    Spinner spinnertype;
    String userId;
    String userToken;
    SharedPreferences sharedPreferences;
    private String type;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseAuth=FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
        nameFiled=findViewById(R.id.editText);
        emailFailed=findViewById(R.id.editphone);
        PasswordFailed=findViewById(R.id.editText3);
        Register=findViewById(R.id.imageButton);
        Signin=findViewById(R.id.Signin);
        spinnertype=findViewById(R.id.spinner2);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Junior");
        arrayList.add("Patient");
        arrayList.add("Student");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertype.setAdapter(arrayAdapter);
        spinnertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = parent.getItemAtPosition(position).toString();

            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        ImageView toggle=findViewById(R.id.toggle);
        toggle.setOnTouchListener((v, event) -> {
            if (!TextUtils.isEmpty(PasswordFailed.getText().toString().trim())) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        PasswordFailed.setInputType(InputType.TYPE_CLASS_TEXT);
                        toggle.setImageResource(R.drawable.ic_eye);
                        break;
                    case MotionEvent.ACTION_UP:
                        PasswordFailed.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        toggle.setImageResource(R.drawable.ic_hide);
                        break;
                }
            }
            return true;
        });
    }



    private void cureateUser(Register register) {
        Dialog dialog = new Dialog(SignUp.this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        firebaseAuth.createUserWithEmailAndPassword(register.getEmail(),register.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        currentUser.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                            userId= currentUser.getUid();
                            userToken= getTokenResult.getToken();

                        }).addOnCompleteListener(task12 -> {
                            String token = FirebaseInstanceId.getInstance().getToken();
                            Map<String, String> map = new HashMap<>();
                            map.put("userId",userId );
                            map.put("userToken",token );
                            map.put("username", register.getName());
                            map.put("type", register.getType().toLowerCase());

                            collectionReference.document(userId).set(map).addOnCompleteListener(task1 -> {
                                dialog.dismiss();
                                Toast.makeText(SignUp.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putString(Constant.CUSTOMERSID, currentUser.getUid()).apply();

                                if(register.getType().toLowerCase().equals("junior")){
                                    Intent i=new Intent(SignUp.this,JuniorProfile.class);
                                    i.putExtra("userid", currentUser.getUid());
                                    sharedPreferences.edit().putBoolean(Constant.LOGIN, true).apply();
                                    sharedPreferences.edit().putString(Constant.CUSTOMERSID, currentUser.getUid()).apply();
                                    sharedPreferences.edit().putString(Constant.TYPE, "junior").apply();
                                    startActivity(i);
                                    finish();
                                }
                                else if(register.getType().toLowerCase().equals("patient")){
                                    Intent i=new Intent(SignUp.this,PatientProfile.class);
                                    i.putExtra("userid", currentUser.getUid());
                                    sharedPreferences.edit().putString(Constant.CUSTOMERSID, currentUser.getUid()).apply();
                                    sharedPreferences.edit().putString(Constant.TYPE, "patient").apply();
                                    startActivity(i);
                                    finish();
                                }
                                else if(register.getType().toLowerCase().equals("student")){
                                    Intent i=new Intent(SignUp.this,StudentProfile.class);
                                    i.putExtra("userid", currentUser.getUid());
                                    sharedPreferences.edit().putString(Constant.CUSTOMERSID, currentUser.getUid()).apply();
                                    sharedPreferences.edit().putString(Constant.TYPE, "student").apply();
                                    startActivity(i);
                                    finish();
                                }
                            });
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
                String name = nameFiled.getText().toString().trim();
                String email = emailFailed.getText().toString().trim();
                String password = PasswordFailed.getText().toString().trim();
                if (validation(name, email, password)) {
                    if (InternetConnection.checkConnection(SignUp.this)) {
                        Register register = new Register(name, email, password, type);
                        cureateUser(register);
                    } else {
                        Toast.makeText(this, "No Internet...", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.Signin:
                startActivity(new Intent(SignUp.this, Login.class));
                break;
        }
    }


    private boolean validation(String name, String email, String pass) {
        boolean validate = true;
        if (name.isEmpty()) {
            validate = false;
            nameFiled.setError("please enter your name");
            nameFiled.requestFocus();


        } else {
            nameFiled.setError(null);
        }
        if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            validate = false;

            emailFailed.setError("please enter valid email");
            emailFailed.requestFocus();
        } else {

            emailFailed.setError(null);
        }
        if (pass.isEmpty() && pass.length() < 6) {
            validate = false;

            PasswordFailed.setError("please enter valid password");
            PasswordFailed.requestFocus();
        } else {
            PasswordFailed.setError(null);

        }


        return validate;
    }



}
