package com.example.glow.ActivityAdmin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.JuniorDetailss;
import com.example.glow.R;
import com.example.glow.model.Junior;
import com.example.glow.network.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDentist extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("juniorProfile");
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    String currentUserId = null;
    AdapterDentail adapterDentail;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Junior>juniorList;
    public FragmentDentist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dentist2, container, false);
        juniorList=new ArrayList<>();
        RecyclerView recyclerView = v.findViewById(R.id.recyclePatient);
        swipeRefreshLayout = v.findViewById(R.id.patientSwip);
        recyclerView.setHasFixedSize(true);//every item of the RecyclerView has a fix size

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterDentail=new AdapterDentail(getContext());
        recyclerView.setAdapter(adapterDentail);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        FeatchData();
        return v;
    }

    private void FeatchData() {
        if (InternetConnection.checkConnection(getActivity())) {


            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    dialog.dismiss();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (int i=0;i<documents.size();i++){
                            Map<String, Object> patient= (Map<String, Object>) documents.get(i).getData().get("patient");
                            if (patient==null)
                                patient=new HashMap<>();
                            HashMap<String, Object> m = (HashMap<String, Object>) documents.get(i).getData();
                            if(m.get("imageUrl")!=null){
                                Junior junior=new Junior(m.get("imageUrl").toString(),m.get("level").toString()
                                        ,m.get("phone").toString(),m.get("section").toString()
                                        ,m.get("university").toString(),m.get("username").toString()
                                        ,patient.size());
                                juniorList.add(junior);
                            }


                        }
                        adapterDentail.setList(juniorList);

                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            Toast.makeText(getActivity(), "No Internet.....", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        adapterDentail.clear();
        FeatchData();

    }
}

class AdapterDentail extends RecyclerView.Adapter<AdapterDentail.Dentailholder> {
    private Context context;
    private List<Junior>juniorList;

    public AdapterDentail(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dentailholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_junior, parent, false);
        return new Dentailholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Dentailholder holder, int position) {
        Junior junior = juniorList.get(position);
        holder.textname.setText(junior.getUsername());
        if(junior.getImageUrl()==null)
            Picasso.get().load(R.drawable.logo).into(holder.circleImageView);
        Picasso.get().load(junior.getImageUrl()).into(holder.circleImageView);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, JuniorDetailss.class);
                intent.putExtra("juniorData",junior);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return juniorList!=null?juniorList.size():0;
    }
    public void setList(List<Junior>juniorList){
        this.juniorList=juniorList;
        notifyDataSetChanged();
    }
    public void clear(){
        juniorList.clear();
        notifyDataSetChanged();
    }

    class Dentailholder extends RecyclerView.ViewHolder {
        ConstraintLayout container;
        CircleImageView circleImageView;
        TextView textname;
        Button btnDone;

        public Dentailholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            container = itemView.findViewById(R.id.container);
            textname = itemView.findViewById(R.id.textname);

        }
    }
}
