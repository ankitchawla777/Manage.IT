package com.example.ankitchawla.manageit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class Attendance_Details extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Calendar c=Calendar.getInstance();
    int today=c.get(Calendar.DAY_OF_MONTH);
    DatabaseReference mref;
    String uid;
    Employee emp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance__details);
        Intent i=getIntent();
        emp=(Employee)i.getSerializableExtra("employee");
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref= FirebaseDatabase.getInstance().getReference();
        mRecyclerView = (RecyclerView) findViewById(R.id.att_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new FirebaseRecyclerAdapter<Attendance, Attendance_Details.attViewHolder>(
                Attendance.class,
                R.layout.attendance_card,
                Attendance_Details.attViewHolder.class,
                mref.child(uid).child("employees").child(Integer.toString(emp.geteid())).child("attendance").orderByChild("atdate").limitToLast(today)) {
            @Override
            public void populateViewHolder(Attendance_Details.attViewHolder holder, final Attendance mcard, final int position) {
                holder.setDate(mcard.getAtdate());
                holder.setAttn(mcard.getAttend());
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }
    public static class attViewHolder extends RecyclerView.ViewHolder {
        TextView date,attn;


        public attViewHolder(View itemView) {
            super(itemView);

            date= (TextView) itemView.findViewById(R.id.adate);
            attn= (TextView) itemView.findViewById(R.id.attnd);

        }
        public void setDate(String atdate) {
            date.setText(atdate);
        }
        public void setAttn(String attend) {
            attn.setText(attend);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAdapter!=null) {
            mAdapter.cleanup();
        }
    }
}
