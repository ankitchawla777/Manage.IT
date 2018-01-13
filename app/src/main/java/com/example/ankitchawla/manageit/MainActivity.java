package com.example.ankitchawla.manageit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference mref;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FloatingActionButton fab;
    ProgressDialog pd;
    AlertDialog.Builder ad;
    String uid;
    StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fab= (FloatingActionButton) findViewById(R.id.FAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,add.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                        if(!user.isEmailVerified())
                        {
                            Toast.makeText(MainActivity.this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        }
                    // User is signed in

                } else {
                    // User is signed out
                    Intent i=new Intent(MainActivity.this,Login.class);
                    MainActivity.this.finish();
                    startActivity(i);
                }
                // ...
            }
        };
        try {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mref = FirebaseDatabase.getInstance().getReference();
            mStorage = FirebaseStorage.getInstance().getReference();
        }
        catch (Exception e)
        {

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        pd=new ProgressDialog(MainActivity.this);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Loading");
        pd.show();
        mAdapter = new FirebaseRecyclerAdapter<Employee, MyViewHolder>(
                Employee.class,
                R.layout.card_main,
                MyViewHolder.class,
                mref.child(uid).child("employees")) {
            @Override
            public void populateViewHolder(MyViewHolder holder, final Employee mcard, final int position) {
                holder.setEid(Integer.toString(mcard.geteid()));
                holder.setName(mcard.getname());
                holder.setDepartment(mcard.getDepartment());
                holder.setImg(getApplicationContext(),mcard.getEmpImgUrl());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(MainActivity.this, Details.class);
                        intent.putExtra("employee",mcard);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ad=new AlertDialog.Builder(MainActivity.this);
                        ad.setTitle("Delete");
                        ad.setMessage("do you Remove this Record");
                        ad.setCancelable(false);
                        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mref.child(uid).child("employees").child(Integer.toString(mcard.geteid())).setValue(null);
                                mStorage.child(uid).child("Emp_Pic").child(Integer.toString(mcard.geteid())).delete();
                                Toast.makeText(MainActivity.this, "Record Removed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
                        return false;
                    }
                });
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        pd.dismiss();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            System.exit(0);
            this.finish();
            return true;
        }
        else if(id==R.id.action_Logout)
        {
          //logout code
            mAuth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.action_search)
        {
            startActivity(new Intent(MainActivity.this,search.class));

        }

        return super.onOptionsItemSelected(item);
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView eid,name,dep;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);

            eid= (TextView) itemView.findViewById(R.id.empid);
            name= (TextView) itemView.findViewById(R.id.empname);
            img= (ImageView) itemView.findViewById(R.id.empImage);
            dep= (TextView) itemView.findViewById(R.id.empDep);
        }
        public void setEid(String id) {
            eid.setText("Id : " + id);
        }
        public void setName(String ename) {
            name.setText("Name : " + ename);
        }
        public void setDepartment(String Department){dep.setText("DEP : " + Department);}
        public void setImg(Context ctxt,String url){
            Picasso.with(ctxt)
                    .load(url)
                    .into(img);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Click on Exit", Toast.LENGTH_SHORT).show();
    }
}
