package com.example.ankitchawla.manageit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class search extends AppCompatActivity {
    ImageButton srch;
    EditText id;
    ProgressDialog mprogress;
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference mref;
    String uid="null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref= FirebaseDatabase.getInstance().getReference();
        id= (EditText) findViewById(R.id.editText);
        srch= (ImageButton) findViewById(R.id.btn_srch);
        mRecyclerView = (RecyclerView) findViewById(R.id.srecycler);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        srch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogress=new ProgressDialog(search.this);
                mprogress.setTitle("Searching Employee");
                mprogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mprogress.setCancelable(false);
                mprogress.show();
                if (id.getText().toString().equals("")) {
                    Toast.makeText(search.this, "Enter id", Toast.LENGTH_SHORT).show();
                }
                else {
                    int eid=Integer.parseInt(id.getText().toString());
                    mAdapter = new FirebaseRecyclerAdapter<Employee, search.MyViewHolder>(
                            Employee.class,
                            R.layout.card_main,
                            search.MyViewHolder.class,
                            mref.child(uid).child("employees").orderByChild("eid").equalTo(eid)) {
                        @Override
                        public void populateViewHolder(search.MyViewHolder holder, final Employee mcard, final int position) {
                            holder.setEid(Integer.toString(mcard.geteid()));
                            holder.setName(mcard.getname());
                            holder.setImg(getApplicationContext(),mcard.getEmpImgUrl());
                        }
                    };
                    mRecyclerView.setAdapter(mAdapter);

                }
                mprogress.dismiss();
            }
        });
        }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eid, name;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            eid = (TextView) itemView.findViewById(R.id.empid);
            name = (TextView) itemView.findViewById(R.id.empname);
            img = (ImageView) itemView.findViewById(R.id.empImage);
        }

        public void setEid(String id) {
            eid.setText("Id : " + id);
        }

        public void setName(String ename) {
            name.setText("Name : " + ename);
        }

        public void setImg(Context ctxt, String url) {
            Picasso.with(ctxt)
                    .load(url)
                    .into(img);
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


