package com.example.glow;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.glow.network.InternetConnection;
import com.example.glow.network.RetrofitClient;
import com.example.glow.network.api.Api;
import com.example.glow.request.NewPassword;
import com.example.glow.util.Constant;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PasswordReset extends AppCompatActivity {

    EditText editemail;
    EditText editpincode;
    EditText editpassword1;
    EditText editpassword2;
    Button btnSubmit;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        editemail=findViewById(R.id.editemail);
        editpincode=findViewById(R.id.editpincode);
        editpassword1=findViewById(R.id.editpassword1);
        editpassword2=findViewById(R.id.editpassword2);
        btnSubmit=findViewById(R.id.btnSubmit);
        sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);

    }

    public void onViewClicked(View v) {
        String email = editemail.getText().toString().trim();
        String pincode = editpincode.getText().toString().trim();
        String pass1 = editpassword1.getText().toString().trim();
        String pass2 = editpassword2.getText().toString().trim();
        int c_id = sharedPreferences.getInt(Constant.CUSTOMERSID, 0);
        String apiToken = sharedPreferences.getString(Constant.APITOKEN, null);
        if (Validation(email, pincode, pass1, pass2)) {
            if (InternetConnection.checkConnection(PasswordReset.this)) {

                NewPassword newPassword = new NewPassword(email,Integer.parseInt(pincode),pass1,pass2,c_id);
                createNewPassword(newPassword, apiToken);
            } else {
                Toast.makeText(this, "No Internet.....", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void createNewPassword(NewPassword newPassword, String apiToken) {
        Dialog dialog = new Dialog(PasswordReset.this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Retrofit retrofitInstance = RetrofitClient.getRetrofitInstance();
        Api api = retrofitInstance.create(Api.class);
        Call<Response> call = api.newPassword(newPassword, apiToken);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, Response<Response> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(PasswordReset.this, "success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PasswordReset.this, Login.class));
                    finish();
                } else {
                    Toast.makeText(PasswordReset.this, "Error...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(PasswordReset.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean Validation(String email, String pincode, String pass1, String pass2) {
        boolean valied = true;
        if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valied = false;
            editemail.setError("Please Enter your email");
        } else {
            editemail.setError(null);

        }
        if (pincode.isEmpty()) {
            valied = false;
            editpincode.setError("Please Enter Pin code");
        } else {
            editpincode.setError(null);
        }
        if (pass1.isEmpty()) {
            valied = false;
            editpassword1.setError("please Enter password");
        } else {
            editpassword1.setError(null);

        }
        if (pass2.isEmpty() && !pass2.equals(pass1)) {
            valied = false;
            editpassword2.setError("please Enter password");
        } else {
            editpassword2.setError(null);
        }
        return valied;
    }
}
