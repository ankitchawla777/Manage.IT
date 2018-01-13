package com.example.ankitchawla.manageit;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class Details extends AppCompatActivity {
    ImageView eimg;
    TextView eid,name,add,doj,sal,dep;
    Button upd,mattd,gattd,gsal;
    Dialog d;
    String att="Absent";
    DatabaseReference mref;
    String uid;
    int p = 0;
    int l = 0;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent i=getIntent();
        final Employee emp=(Employee)i.getSerializableExtra("employee");
        eid= (TextView) findViewById(R.id.empid);
        name= (TextView) findViewById(R.id.empname);
        add= (TextView) findViewById(R.id.eadd);
        doj= (TextView) findViewById(R.id.edoj);
        sal= (TextView) findViewById(R.id.esal);
        dep= (TextView) findViewById(R.id.empDepart);
        eimg= (ImageView) findViewById(R.id.empImage);
        upd= (Button) findViewById(R.id.button2);
        mattd= (Button) findViewById(R.id.btnattd);
        gattd= (Button) findViewById(R.id.btngetattnd);
        gsal= (Button) findViewById(R.id.btnsal);
        final DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        final Calendar c=Calendar.getInstance();
        final int noOfDays=c.getMaximum(Calendar.DAY_OF_MONTH);
        final int dayOfweek=c.get(Calendar.DAY_OF_WEEK);
        int today=c.get(Calendar.DAY_OF_MONTH);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mref= FirebaseDatabase.getInstance().getReference().child(uid).child("employees");
        mref.child(String.valueOf(emp.geteid())).child("attendance").limitToLast(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(df.format(c.getTime())) || (dayOfweek==0))
                {
                    mattd.setEnabled(false);
                }
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    String attend=item.child("attend").getValue(String.class);
                    if(attend.equals("Present"))
                        p+=1;
                    else if (attend.equals("Late"))
                        l+=1;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Details.this,update.class);
                intent.putExtra("employee",emp);
                startActivity(intent);
            }
        });
        mattd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                d=new Dialog(Details.this);
                d.setContentView(R.layout.attendance_dialog);
                TextView tvdate= (TextView) d.findViewById(R.id.viewdate);
                tvdate.setText(df.format(c.getTime()));
                final RadioGroup rg= (RadioGroup) d.findViewById(R.id.attgrp);
                Button sub= (Button) d.findViewById(R.id.btnsub);
                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(rg.getCheckedRadioButtonId()==R.id.rp)
                        {
                           att ="Present";
                        }
                        else if(rg.getCheckedRadioButtonId()==R.id.ra)
                        {
                            att="Absent";
                        }
                        else if(rg.getCheckedRadioButtonId()==R.id.rl)
                        {
                            att="Late";
                        }
                        else
                        {
                            Toast.makeText(Details.this, "Please select an Input", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Details.this, att , Toast.LENGTH_SHORT).show();
                        mref.child(String.valueOf(emp.geteid())).child("attendance").child(df.format(c.getTime())).child("atdate").setValue(df.format(c.getTime()));
                        mref.child(String.valueOf(emp.geteid())).child("attendance").child(df.format(c.getTime())).child("attend").setValue(att);
                        d.hide();
                        startActivity(new Intent(Details.this,MainActivity.class));
                    }
                });
                d.show();

            }
        });
        gattd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Details.this, Attendance_Details.class);
                intent.putExtra("employee",emp);
                startActivity(intent);
            }
        });
        gsal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d=new Dialog(Details.this);
                d.setContentView(R.layout.wdays_dialog);
                final EditText wd= (EditText) d.findViewById(R.id.woda);
                Button sbt= (Button) d.findViewById(R.id.wsub);
                sbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int w_days=Integer.parseInt(wd.getText().toString());
                        if(w_days>15 && w_days<(noOfDays-4)){
                            float sal;
                            float dsal=emp.getsalary()/w_days;
                            sal= (dsal* p) + ((dsal/2)* l);
//                            Toast.makeText(Details.this, String.valueOf(sal) , Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(android.R.id.content), "Salry Till Now: " + String.valueOf(sal) , Snackbar.LENGTH_LONG).show();
                            d.hide();

                        }
                        else
                        {
                            wd.setError("Value Should be between 15 and 26");
                        }
                    }
                });
                d.show();
            }
        });
        eid.setText(String.valueOf(emp.geteid()));
        name.setText(emp.getname());
        add.setText(emp.getaddress());
        doj.setText(emp.getdatej());
        dep.setText(emp.getDepartment());
        sal.setText(String.valueOf(emp.getsalary()));
        Picasso.with(getApplicationContext())
                .load(emp.getEmpImgUrl())
                .into(eimg);
    }
}
