package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.glow.R;
import com.example.glow.model.Patient;
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

public class check_up_from_juinor_choosepatient extends AppCompatActivity {
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
    Dialog dialog ;
    int index;
    TextView textNoitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up);

        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId=user.getUid();
        patientData=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerView_dentalhistory);
        textNoitem=findViewById(R.id.textNoitem);

        adapter=new Adapter(patientData,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        showDialog();
        collectionReference
                .get()
                .addOnCompleteListener(task1 -> {
                    hideDialog();
                    if (task1.isSuccessful()) {
                        for (QueryDocumentSnapshot document1 : task1.getResult()) {
                            Map<String,Object> m=document1.getData();
                            if(m.get("active").equals("1")&&m.get("done").equals("1")&&m.get("check").equals("0"))
                                patientData.add(m);
                        }
                        if(patientData.size()==0)
                            textNoitem.setVisibility(View.VISIBLE);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                patientData.remove(index);
                adapter.notifyDataSetChanged();
                if(patientData.size()==0)
                    textNoitem.setVisibility(View.VISIBLE);

            }
        }
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
        if(arr.get(i).get("imageUrl")!=null)
            Picasso.get().load(arr.get(i).get("imageUrl").toString()).placeholder(R.drawable.photo).into(holder.img);
        else
            holder.img.setImageResource(R.drawable.photo);
        holder.card.setOnClickListener(v -> {
            Intent intent=new Intent(context,checkup_from_junior.class);
            intent.putExtra("patientID",arr.get(i).get("userId").toString());
            index=i;
            startActivityForResult(intent,1);
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
