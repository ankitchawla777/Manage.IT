package com.example.ankitchawla.manageit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    EditText em,n,ph,p,rp;
    Button btn_su;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog pd;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        em=(EditText) findViewById(R.id.semail);
        n=(EditText) findViewById(R.id.sname);
        ph=(EditText) findViewById(R.id.sphone);
        p=(EditText) findViewById(R.id.spassword);
        rp=(EditText) findViewById(R.id.rpassword);
        btn_su=(Button) findViewById(R.id.btn_su);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        pd=new ProgressDialog(Signup.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Signing Up");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    user.sendEmailVerification();
                    Intent i=new Intent(Signup.this,MainActivity.class);
                    Signup.this.finish();
                    startActivity(i);

                } else {
                    // User is signed out

                }

            }
        };
        btn_su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=em.getText().toString();
                final String name=n.getText().toString();
                final Long phone=Long.parseLong(ph.getText().toString());
                String password=p.getText().toString();
                String rpassword=rp.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phone.toString()))
                {
                    Toast.makeText(Signup.this, "Fields are Empty", Toast.LENGTH_SHORT).show();

                }
                else if(!password.equals(rpassword))
                {
                    rp.setError("Password do not match");

                }
                else
                {
                    pd.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Signup.this, "Cant Sign Up",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else if(task.isSuccessful())
                                    {
                                        uid=mAuth.getCurrentUser().getUid();
                                        myRef.child(uid).child("name").setValue(name);
                                        myRef.child(uid).child("phone").setValue(phone.toString());
                                        Toast.makeText(Signup.this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                    pd.dismiss();
                }

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
