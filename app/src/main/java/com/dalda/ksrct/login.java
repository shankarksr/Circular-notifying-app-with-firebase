package com.dalda.ksrct;

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

public class login extends AppCompatActivity {

    private EditText email, pass;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();
        email = findViewById(R.id.rnum);
        pass = findViewById(R.id.pass);
        Button logbtn = findViewById(R.id.log);
        Button regbtn = findViewById(R.id.sup);
        progressDialog = new ProgressDialog(this);

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String logemail =  email.getText().toString();
                String logpass = pass.getText().toString();

                if (!TextUtils.isEmpty(logemail)&& !TextUtils.isEmpty(logpass)){
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.signInWithEmailAndPassword(logemail,logpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent i = new Intent(login.this,MainActivity.class);
                                startActivity(i);

                            }else {
                                progressDialog.dismiss();
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(login.this,"Error :" + errorMessage,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!= null){

            Intent i=new Intent(login.this,MainActivity.class);
            startActivity(i);
            finish();
        }
    }
    public void sup(View view) {
        Intent intent = new Intent(login.this,register.class);
        startActivity(intent);
        finish();
    }
}
