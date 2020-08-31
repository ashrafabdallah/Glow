package com.example.glow.notification;

import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServies {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAACHX2dPs:APA91bEmWDF6N-3xaVfVu-hirms4jfhMnhALZDOhoUCs3gwygWUHz_Y3Ef76BD0mqSCvcMDbDCB69bKE631mDfs_i_Xk63oCkUizquQQ7it6k7uB252xKRfPPxQ84BVRu6of2xfxaS5o"
    })

    @POST("fcm/send")
    Call<Response>senNotification(@Body Sender body);
}
