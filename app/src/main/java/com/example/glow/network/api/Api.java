package com.example.glow.network.api;

import com.example.glow.request.JuniorRequest;
import com.example.glow.request.NewPassword;

import com.example.glow.request.Register;
import com.example.glow.response.juniorprofile.JuniorProfileResponse;
import com.example.glow.response.login.LoginResponse;
import com.example.glow.response.patient_list_from_junior.PatientListFromJuniorResponse;
import com.example.glow.response.register.RegisterResponse;
import com.example.glow.response.reset_password.ResetPasswordResponse;
import com.example.glow.response.student.PatientListFromStudent;
import com.example.glow.response.student.StudentProfileResponse;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("reset_password")
    Call<ResetPasswordResponse> reset_password(@Field("email") String email);

    @POST("register")
    Call<RegisterResponse>createUser(@Body Register register);

    @POST("newpassword")
    Call<Response> newPassword(@Body NewPassword newPassword, @Query("api_token") String api_token);


    @POST("student_profile")
    Call<StudentProfileResponse> studentProfile(@Body StudentProfileResponse studentProfileResponseData, @Query("api_token") String api_token);

    @GET("patient_list_from_student")
    Call<PatientListFromStudent> patientListFromStudent( @Query("api_token") String api_token);

    @POST("junior_profile")
    Call<JuniorProfileResponse>junior_profile(@Body JuniorRequest juniorProfile, @Query("api_token")String api_token);

    @GET("patient_list_from_junior")
    Call<PatientListFromJuniorResponse>patient_list_from_junior(@Query("customer_id")int customer_id,@Query("api_token")String api_token);
}
