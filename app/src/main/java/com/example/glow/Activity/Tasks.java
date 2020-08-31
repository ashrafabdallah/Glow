package com.example.glow.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glow.R;
import com.example.glow.model.AdminPatient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tasks extends AppCompatActivity {
    String imageUrl;
    String patientName;
    String phone;
    List<Map<String, Object>> arr;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        TextView textShowTask = findViewById(R.id.textShowTask);
        RecyclerView resyclerviewtask = findViewById(R.id.resyclerviewtask);
        resyclerviewtask.setLayoutManager(new LinearLayoutManager(this));
        resyclerviewtask.setHasFixedSize(true);//every item of the RecyclerView has a fix size
        Intent intent = getIntent();
        AdminPatient data = (AdminPatient) intent.getSerializableExtra("AllTasks");
        imageUrl = data.getImageUrl();
        patientName = data.getUsername();
        phone = data.getPhone();
        arr = new ArrayList<>();
        if (arr.size() == 0)
            textShowTask.setVisibility(View.VISIBLE);
        else
            textShowTask.setVisibility(View.GONE);
        if (data.getTasks() != null)
            for (Map<String, Object> map : data.getTasks()) {
                if (map != null)
                    if (map.get("endTaskDate") != null) {
                        textShowTask.setVisibility(View.GONE);
                        arr.add(map);
                    }
            }
        resyclerviewtask.setAdapter(new TasksAdapter(arr, this, imageUrl, patientName, phone));
        // Toast.makeText(this, data.getTasks().size()+"", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}

class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.Holder> {
    List<Map<String, Object>> list;
    Context context;

    String imageUrl;
    String patientName;
    String phone;

    public TasksAdapter(List<Map<String, Object>> list, Context context, String imageUrl, String patientName, String phone) {
        this.list = list;
        this.context = context;
        this.imageUrl = imageUrl;
        this.patientName = patientName;
        this.phone = phone;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {

        holder.text_num_Task.setText((i + 1) + "");
        holder.text_task_date.setText(list.get(i).get("endTaskDate").toString());


        holder.continar.setOnClickListener(v -> {
            Intent intent = new Intent(context, PatientDetailss.class);
            intent.putExtra("AllData", (Serializable) list.get(i));
            intent.putExtra("image", imageUrl);
            intent.putExtra("username", patientName);
            intent.putExtra("phone", phone);
            context.startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView text_num_Task, text_task_date;
        CardView continar;

        public Holder(@NonNull View itemView) {
            super(itemView);
            text_task_date = itemView.findViewById(R.id.text_task_date);
            text_num_Task = itemView.findViewById(R.id.text_num_Task);
            continar = itemView.findViewById(R.id.continar);
        }
    }

}
