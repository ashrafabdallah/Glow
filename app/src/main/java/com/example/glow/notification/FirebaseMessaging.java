package com.example.glow.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.glow.EmailCode;
import com.example.glow.R;
import com.example.glow.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class FirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // get current user from shared
//        SharedPreferences sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
//        String savedCurrentUser = sharedPreferences.getString(Constant.CUSTOMERSID, null);
//        String sent = remoteMessage.getData().get("sent");
//        String user = remoteMessage.getData().get("user");
//        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//        if (fuser != null && sent.equals(fuser.getUid())) {
//            if (!savedCurrentUser.equals(user)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    senOreAndAboveNotification(remoteMessage);
//                } else {
//                    sendNormalNotification(remoteMessage);
//                }
//            }
//
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            senOreAndAboveNotification(remoteMessage);
//        } else {
//            sendNormalNotification(remoteMessage);
//        }
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        System.out.println(title);
        System.out.println(body);
        int mNotificationId = (int) System.currentTimeMillis();
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, mNotificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.logo)
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultUri)
                .setContentIntent(contentIntent);



        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, builder.build());

    }

//    private void sendNormalNotification(RemoteMessage remoteMessage) {
//        String user = remoteMessage.getData().get("user");
//        String icon = remoteMessage.getData().get("icon");
//        String title = remoteMessage.getData().get("title");
//        String body = remoteMessage.getData().get("body");
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
//        Intent intent = new Intent(this, EmailCode.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("user", user);
//        intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setSmallIcon(Integer.parseInt(icon))
//                .setContentText(body)
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                .setSound(defaultUri)
//                .setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        int j = 0;
//        if (i > 0) {
//            j = i;
//        }
//        notificationManager.notify(j, builder.build());
//
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void senOreAndAboveNotification(RemoteMessage remoteMessage) {
//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//     OreoAndAboveNotification oreoAndAboveNotification=new OreoAndAboveNotification(this);
//        Notification.Builder builder=oreoAndAboveNotification.getNotification(title,body,defaultUri, R.drawable.logo);
//        oreoAndAboveNotification.getManager().notify(j, builder.build());
//
//    }
}
