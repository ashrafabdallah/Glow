package com.example.glow.notification;

import android.content.SharedPreferences;

import com.example.glow.util.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class FirebaseServise extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        if(user!=null){
            updateToken(tokenRefresh);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

         FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Users");
        Token token=new Token(tokenRefresh);
        Map<String,Object>d=new HashMap<>();
        d.put("userToken",token);
        collectionReference.document(currentUser.getUid()).update(d).addOnSuccessListener(aVoid -> {
         SharedPreferences sharedPreferences = getSharedPreferences(Constant.FILENAME, MODE_PRIVATE);
         sharedPreferences.edit().putString("token",tokenRefresh).commit();
        });

    }
}
