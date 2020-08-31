package com.example.glow;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.glow.network.InternetConnection;
import com.example.glow.network.RetrofitClient;
import com.example.glow.network.api.Api;
import com.example.glow.response.reset_password.ResetPasswordResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgetPassword extends AppCompatActivity {

    Button send;
    EditText editemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        send=findViewById(R.id.send);
        editemail=findViewById(R.id.editemail);

    }

    public void onViewClicked(View view) {
        String email = editemail.getText().toString().trim();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (InternetConnection.checkConnection(ForgetPassword.this)) {
                resetPassword(email);
            }


        } else {
            editemail.setError("please enter your email");
        }
    }

    private void resetPassword(String email) {
        Dialog dialog = new Dialog(ForgetPassword.this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(ForgetPassword.this, "please open this email ...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgetPassword.this,Login.class));
                            finish();
                        }
                    }
                });
    }
}

