package com.example.ankitchawla.manageit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class add extends AppCompatActivity {
    EditText id,n,addr,sal,doj,dep;
    private Button b1;
    ImageButton eImg;
    DatabaseReference mref;
    ProgressDialog pd;
    Uri imageuri=null;
    StorageReference mStorage;
    private static final int GALLERY_REQUEST=1;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        id= (EditText) findViewById(R.id.editText);
        n= (EditText) findViewById(R.id.editText2);
        addr= (EditText) findViewById(R.id.editText3);
        sal= (EditText) findViewById(R.id.editText4);
        doj=(EditText) findViewById(R.id.editText5);
        b1= (Button) findViewById(R.id.button);
        eImg= (ImageButton) findViewById(R.id.imageButton);
        dep= (EditText) findViewById(R.id.editDep);
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
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
                if(id.getText().toString().equals("") || n.getText().equals("") || addr.getText().equals("") || sal.getText().toString().equals("") || doj.getText().equals("") || imageuri==null || addr.getText().equals("")) {
                    Toast.makeText(add.this, "Enter Details correctly", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd.setMessage("uploading Data");
                    pd.show();
                    final int eid = Integer.parseInt(id.getText().toString());
                    final String name = n.getText().toString();
                    final String address = addr.getText().toString();
                    final int salary = Integer.parseInt(sal.getText().toString());
                    final String datej = doj.getText().toString();
                    final String department=dep.getText().toString();
                    mStorage.child(uid).child("Emp_Pic").child(Integer.toString(eid)).putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri dwldUri = taskSnapshot.getDownloadUrl();
                            Employee emp=new Employee(eid,name,address,salary,datej,dwldUri.toString(),department);
                            mref.child(uid).child("employees").child(Integer.toString(eid)).setValue(emp);
                            pd.dismiss();
                            Toast.makeText(add.this, "data saved", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(add.this, MainActivity.class);
                            startActivity(i);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(add.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        doj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                DatePickerDialog dp=new DatePickerDialog(add.this,date,
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.YEAR)
                );
                dp.updateDate(2017,0,1);
                dp.show();
            }
        });
    }
    DatePickerDialog.OnDateSetListener date=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
             doj.setText(dayOfMonth+ "/"+(month+1)+"/"+year);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            imageuri=data.getData();
            eImg.setImageURI(imageuri);
        }
    }
}
