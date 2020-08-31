package com.example.glow.ActivityAdmin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.Activity.StudentDetailss;
import com.example.glow.HomeJunior;
import com.example.glow.HomePatient;
import com.example.glow.R;
import com.example.glow.model.Student;
import com.example.glow.network.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentStudent extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView recycleStudent;
    private SwipeRefreshLayout studentSwip;
    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("StudentProfile");
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    String currentUserId = null;
    List<Student> list;
    StudentAdapter studentAdapter;

    List<Map<String, Object>> arr;
    double v = 0;
    Student student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_student2, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();
        list = new ArrayList<>();
        studentSwip = v.findViewById(R.id.studentSwip);
        recycleStudent = v.findViewById(R.id.recycleStudent);
        recycleStudent.setHasFixedSize(true);//every item of the RecyclerView has a fix size

        studentAdapter = new StudentAdapter(getActivity());
        recycleStudent.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleStudent.setAdapter(studentAdapter);
        studentSwip.setOnRefreshListener(this);
        studentSwip.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        FetchData();


        return v;
    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        studentAdapter.clear();
        FetchData();


    }

    public void FetchData() {
        if (InternetConnection.checkConnection(getActivity())) {


            final Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.loading_bar);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();
            db.collection("StudentProfile").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (studentSwip.isRefreshing()) {
                            studentSwip.setRefreshing(false);

                        }
                        dialog.dismiss();
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();

                        for (int i = 0; i < documents.size(); i++) {

                            float sum = 0;
                            Map<String, Object> data2 = (Map<String, Object>) documents.get(i).getData().get("rate");
                            Map<String, Object> patient = (Map<String, Object>) documents.get(i).getData().get("patient");
                            if (patient == null)
                                patient = new HashMap<>();

                            if (data2 == null)
                                data2 = new HashMap<>();

                            for (String key : data2.keySet()) {
                                String value = data2.get(key).toString();
                                sum += Float.parseFloat(value);
                            }
                            HashMap<String, Object> m = (HashMap<String, Object>) documents.get(i).getData();
                            if (m == null)
                                m = new HashMap<>();
                           if(m.get("imageUrl")!=null){
                               student = new Student(m.get("imageUrl").toString(),
                                       m.get("level").toString(), m.get("phone").toString(),
                                       sum / data2.size(), m.get("section").toString(),
                                       m.get("university").toString(), m.get("username").toString(),
                                       patient.size());
                               list.add(student);
                           }



//                        if (data2 != null) {
//                            for (Map.Entry<String, Object> o : data2.entrySet()) {
//                                String key = o.getKey();
//                                Object value = o.getValue();
//                                System.out.println("Value is :" + value);
//                                System.out.println("Key is :" + key);
//                            }
//                        }


                        }
                        studentAdapter.SetList(list);
//                    Object rate1 = documents.get(0).get("rate");
//                    System.out.println(rate1+"");


                    }


//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot querySnapshot : task.getResult()) {
//
//                            String imageUrl = querySnapshot.getString("imageUrl");
//                            String level = querySnapshot.getString("level");
//                            String phone = querySnapshot.getString("phone");
//                            Map<String, Object> data = querySnapshot.getData();
//
////                            Map<String, Object> rate = (Map<String, Object>) data.get("rate");
////                            if (rate != null) {
////                                for (Map.Entry<String, Object> o : rate.entrySet()) {
////                                    key = o.getKey();
////                                    value = o.getValue();
////                                    v = Double.parseDouble((String) value);
////                                }
////                            }
//                            System.out.println("sum is " + sum);
//                            String section = querySnapshot.getString("section");
//                            String university = querySnapshot.getString("university");
//                            String username = querySnapshot.getString("username");
//
//                        }
//
//
//                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            Toast.makeText(getActivity(), "No Internet.....", Toast.LENGTH_SHORT).show();
        }
    }


    class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
        List<Student> lists;
        Context context;


        public StudentAdapter(Context context) {
            this.context = context;

        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_admin, parent, false);
            return new StudentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            Student student = lists.get(position);
            holder.textname.setText(student.getUsername());
            if (student.getImageUrl() != null) {
                Picasso.get().load(student.getImageUrl()).into(holder.circleImageView);
            } else {
                Picasso.get().load(R.drawable.logo).into(holder.circleImageView);
            }
            holder.ratingBar3.setRating(student.getRate());
            holder.continerStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StudentDetailss.class);
                    intent.putExtra("studentdetailes", student);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists != null ? lists.size() : 0;
        }

        public void clear() {
            list.clear();
            notifyDataSetChanged();
        }

        public void SetList(List<Student> lists) {
            this.lists = lists;
            notifyDataSetChanged();
        }

        class StudentViewHolder extends RecyclerView.ViewHolder {
            CircleImageView circleImageView;
            TextView textname;
            RatingBar ratingBar3;
            ConstraintLayout continerStudent;

            public StudentViewHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.circleImageView);
                textname = itemView.findViewById(R.id.textname);
                ratingBar3 = itemView.findViewById(R.id.ratingBar3);
                continerStudent = itemView.findViewById(R.id.continerStudent);
            }
        }
    }
}
