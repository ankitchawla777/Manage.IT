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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText uemail,upass;
    Button loginbtn;
    TextView sgup,fpass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uemail= (EditText) findViewById(R.id.editText3);
        upass= (EditText) findViewById(R.id.editText4);
        loginbtn= (Button) findViewById(R.id.btn_lg);
        sgup= (TextView) findViewById(R.id.signup);
        fpass= (TextView) findViewById(R.id.fp);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent i=new Intent(Login.this,MainActivity.class);
                    startActivity(i);

                } else {
                    // User is signed out
                    Toast.makeText(Login.this, "Please Login", Toast.LENGTH_SHORT).show();

                }

            }
        };
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uemail.getText().toString();
                String password = upass.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    pd=new ProgressDialog(Login.this);
                    pd.setCancelable(false);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setMessage("logging In");
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle then
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    }
                                    pd.dismiss();

                                }


                            });
                }
            }
        });
        sgup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Signup.class));
            }
        });
        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = uemail.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(Login.this, "Enter email address", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(Login.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "Cant send mail",
                                        Toast.LENGTH_SHORT).show();
                            } else if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Reset link sent to email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
