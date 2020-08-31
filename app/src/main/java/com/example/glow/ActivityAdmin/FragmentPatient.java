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
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.Tasks;
import com.example.glow.R;
import com.example.glow.model.AdminPatient;
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

public class FragmentPatient extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout patientSwip;
    private RecyclerView recyclePatient;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("PatientProfile");
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    String currentUserId = null;
    List<AdminPatient> patientList;
    PatientAdapter patientAdapter;

    Map<String, Object> m;
    List<Object> arr;

    public FragmentPatient() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_patient2, container, false);
        m = new HashMap<>();
        arr = new ArrayList<>();
        patientSwip = v.findViewById(R.id.patientSwip);
        recyclePatient = v.findViewById(R.id.recyclePatient);
        patientList = new ArrayList<>();
        recyclePatient.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclePatient.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        patientSwip.setOnRefreshListener(this);
        patientAdapter = new PatientAdapter(getActivity());
        recyclePatient.setAdapter(patientAdapter);
        patientSwip.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        FetchData();


        return v;
    }

    @Override
    public void onRefresh() {
        patientAdapter.clear();
        FetchData();

    }

    public void FetchData() {
        if (InternetConnection.checkConnection(getActivity())) {


            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
//            db.collection("PatientProfile").get().
//                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    dialog.dismiss();
//                    if (patientSwip.isRefreshing()) {
//                        patientSwip.setRefreshing(false);
//
//                    }
//                    if (task.isSuccessful()) {
//
//                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
//                        for (int i = 0; i < documents.size(); i++) {
//                            int count = 0;
//                            String value = "";
//                            String endTask = "";
//                            String key = "";
//                            String studentId = "";
//                            Map<String, Object> datatask = (Map<String, Object>) documents.get(i).getData().get("tasks");
//                            if (datatask == null)
//                                datatask = new HashMap<>();
//
//                            for (Map.Entry<String, Object> o : datatask.entrySet()) {
//                                count++;
//                                key = o.getKey();
//                                value = o.getValue().toString();
//                                if (key.equals("endTaskDate"))
//                                    endTask = o.getValue().toString();
//                                if (key.equals("studentID"))
//                                    studentId = o.getValue().toString();
//                                ;
//
//                            }
//                            System.out.println("Value is :" + value);
//                            System.out.println("Key is :" + key);
//                            System.out.println("end task is :" + endTask);
//                            System.out.println("studentId is :" + studentId);
//
//
////                            for (String key : datatask.keySet()) {
////                                dateTask = datatask.get(key).toString();
////                                if (key.equals("endTaskDate"))
////                                    endTask=datatask.get(key).toString();
////                                count++;
////                            }
//                            HashMap<String, Object> m = (HashMap<String, Object>) documents.get(i).getData();
//                            if (m == null)
//                                m = new HashMap<>();
////                            AdminPatient data = new AdminPatient(count, m.get("username").toString(),
////                                    m.get("imageUrl").toString(),
////                                    m.get("phone").toString(), key, value, studentId, endTask
////
////                            );
//                                AdminPatient data=new AdminPatient( m.get("username").toString(),m.get("imageUrl").toString());
//                            patientList.add(data);
//
//                        }
//                        patientAdapter.setList(patientList);
//
//                    }
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    dialog.dismiss();
//                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });

            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    dialog.dismiss();
                    if (patientSwip.isRefreshing()) {
                        patientSwip.setRefreshing(false);

                    }

                    if (task.isSuccessful()) {
                        for (DocumentSnapshot querySnapshot : task.getResult()) {

                            String value = "";
                            String endTask = "";
                            String key = "";
                            String studentId = "";
                            Map<String, Object> data = querySnapshot.getData();
                            List<Map<String, Object>> tasks = (List<Map<String, Object>>) data.get("tasks");
                            if (tasks == null)
                                tasks = new ArrayList<>();
                            if (querySnapshot.getString("imageUrl") != null) {
                                AdminPatient adminPatient = new AdminPatient(querySnapshot.getString("username"), querySnapshot.getString("imageUrl"), tasks, querySnapshot.getString("phone"));
                                patientList.add(adminPatient);
                            }

                        }
                        patientAdapter.setList(patientList);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No Internet.....", Toast.LENGTH_SHORT).show();
        }

    }


}

class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.patientHolder> {
    private List<AdminPatient> adminPatients;
    private Context context;

    public PatientAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public patientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_admin, parent, false);

        return new patientHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull patientHolder holder, int position) {
        AdminPatient data = adminPatients.get(position);
        if (data.getImageUrl() == null)
            Picasso.get().load(R.drawable.logo).into(holder.circleImageView);
        Picasso.get().load(data.getImageUrl()).into(holder.circleImageView);
        holder.textname.setText(data.getUsername());
        holder.containerPatient.setOnClickListener(v -> {

            Intent i = new Intent(context, Tasks.class);
            i.putExtra("AllTasks", data);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return adminPatients != null ? adminPatients.size() : 0;
    }

    public void setList(List<AdminPatient> adminPatients) {
        this.adminPatients = adminPatients;
        notifyDataSetChanged();
    }

    public void clear() {
        adminPatients.clear();
        notifyDataSetChanged();
    }

    class patientHolder extends RecyclerView.ViewHolder {
        ConstraintLayout containerPatient;
        CircleImageView circleImageView;
        TextView textname;

        public patientHolder(@NonNull View itemView) {
            super(itemView);
            containerPatient = itemView.findViewById(R.id.containerPatient);
            circleImageView = itemView.findViewById(R.id.circleImageView);
            textname = itemView.findViewById(R.id.textname);
        }
    }
}
