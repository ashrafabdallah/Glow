package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.glow.HomeStudent;
import com.example.glow.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DentailHistory extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    String currentUserId=null;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    List<Map<String,Object>> patientData;
    Adapter adapter;
    RecyclerView recyclerView;
    String type;
    Dialog dialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentail_history);
        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId=user.getUid();
        patientData=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerView_dentalhistory);
        adapter=new Adapter(patientData,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        type=getIntent().getStringExtra("type");
        showDialog();
        collectionReference
                .get()
                .addOnCompleteListener(task1 -> {
                    hideDialog();
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                            Map<String,Object> m=document1.getData();
                            if(m.get("active").equals("1")&&m.get(type).equals("1"))
                                if(type.equals("done")) {
                                    if (!m.get("check").equals("1"))
                                        patientData.add(m);
                                }else
                                    if (m.get("check").equals("1"))
                                        patientData.add(m);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
    void showDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_bar);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    void hideDialog(){
        dialog.dismiss();
    }
    class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {
        List<Map<String,Object>> arr;
        Context context;
        public Adapter(List<Map<String,Object>> arr, Context context) {
            this.arr = arr;
            this.context = context;
        }
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_patient_admin,parent,false));
        }
        @Override
        public void onBindViewHolder(@NonNull final MyHolder holder, final int i) {
            holder.textView.setText(arr.get(i).get("username").toString());
            if(patientData.get(i).get("imageUrl")!=null)
                Picasso.get().load(patientData.get(i).get("imageUrl").toString()).placeholder(R.drawable.photo).into(holder.img);
            else
                holder.img.setImageResource(R.drawable.photo);
            holder.card.setOnClickListener(v -> {
                Intent intent=new Intent(context,DentalPatientDetail.class);
                intent.putExtra("patientID",arr.get(i).get("userId").toString());
                if(type.equals("done"))
                    intent.putExtra("junior","junior");
                startActivity(intent);
            });
        }
        @Override
        public int getItemCount() {
            return arr.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            CircleImageView img;
            TextView textView;
            CardView card;
            public MyHolder(@NonNull View itemView) {
                super(itemView);

                img=itemView.findViewById(R.id.circleImageView);
                textView=itemView.findViewById(R.id.textname);
                card=itemView.findViewById(R.id.patientCard);
            }
        }
    }
}
