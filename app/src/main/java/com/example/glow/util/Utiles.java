package com.example.glow.util;

import android.app.Dialog;
import android.content.Context;

import com.example.glow.R;

public  class Utiles {
     static Context context;

    public Utiles(Context context) {
        this.context = context;
    }
    public void ShowProgresspar(){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_bar);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    public void DismissProgresspar(){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.loading_bar);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.dismiss();
    }
}
