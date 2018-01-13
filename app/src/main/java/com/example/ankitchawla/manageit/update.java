package com.example.ankitchawla.manageit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class update extends AppCompatActivity {
    EditText id,n,add,sal,doj,dep;
    Button b1;
    DatabaseReference mref;
    ImageButton eImg;
    ProgressDialog pd;
    Uri imageuri=null;
    StorageReference mStorage;
    Employee emp;
    String uid;
    private static final int GALLERY_REQUEST=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        id= (EditText) findViewById(R.id.editText);
        n= (EditText) findViewById(R.id.editText2);
        add= (EditText) findViewById(R.id.editText3);
        sal= (EditText) findViewById(R.id.editText4);
        doj=(EditText)findViewById(R.id.editText5);
        b1= (Button) findViewById(R.id.button);
        dep= (EditText) findViewById(R.id.Dep);
        eImg= (ImageButton) findViewById(R.id.imageButton);
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        Intent i=getIntent();
        emp=(Employee)i.getSerializableExtra("employee");
        id.setText(Integer.toString(emp.geteid()));
        n.setText(emp.getname());
        add.setText(emp.getaddress());
        sal.setText(Integer.toString(emp.getsalary()));
        doj.setText(emp.getdatej());
        dep.setText(emp.getDepartment());
        Picasso.with(getApplicationContext())
                .load(emp.getEmpImgUrl())
                .into(eImg);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorage= FirebaseStorage.getInstance().getReference();
        mref= FirebaseDatabase.getInstance().getReference();
        eImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,GALLERY_REQUEST);

            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id.getText().toString().equals("") || n.getText().equals("") || add.getText().equals("") || sal.getText().toString().equals("") || doj.getText().equals("") || imageuri==null) {
                    Toast.makeText(update.this, "Enter Details correctly", Toast.LENGTH_SHORT).show();
                }
                else {

                    pd.setMessage("uploading Data");
                    pd.show();
                    final int eid = Integer.parseInt(id.getText().toString());
                    final String name = n.getText().toString();
                    final String address = add.getText().toString();
                    final int salary = Integer.parseInt(sal.getText().toString());
                    final String datej=doj.getText().toString();
                    final String department=dep.getText().toString();
                    mStorage.child(uid).child("Emp_Pic").child(Integer.toString(eid)).putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri dwldUri = taskSnapshot.getDownloadUrl();
                            Employee emp1=new Employee(eid,name,address,salary,datej,dwldUri.toString(),department);
                            mref.child(uid).child("employees").child(Integer.toString(eid)).setValue(emp);
                            pd.dismiss();
                    Toast.makeText(update.this, "Record Updated", Toast.LENGTH_SHORT).show();
                    update.this.finish();
                    Intent i = new Intent(update.this, MainActivity.class);
                    startActivity(i);
                }
            }) .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(update.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }});
        doj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                new DatePickerDialog(update.this,date,
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR)
                ).show();
            }
        });
    }
    DatePickerDialog.OnDateSetListener date=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            doj.setText(dayOfMonth+ "/"+(month+1)+"/"+year);
        }
    };
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            imageuri=data.getData();
            eImg.setImageURI(imageuri);
        }
    }
}
