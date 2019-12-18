package com.dalda.ksrct;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity implements View.OnClickListener {
    private EditText namex,regno,email,pass,pass2;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        namex =  findViewById(R.id.nme);
        regno =  findViewById(R.id.rno);
        email =  findViewById(R.id.emil);
        pass =  findViewById(R.id.pwd);
        pass2 = findViewById(R.id.pwd2);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.sip).setOnClickListener(this);
    }

    private void registerUser() {
        final String name = namex.getText().toString().trim();
        final String regnum = regno.getText().toString().trim();
        final String memail = email.getText().toString().trim();
        String pwrd1 = pass.getText().toString().trim();
        final String pwrd2 = pass2.getText().toString().trim();

        if (name.isEmpty()) {
            namex.setError("Name required");
            namex.requestFocus();
            return;
        }
        if (regnum.isEmpty() || regnum.length() !=7) {
            regno.setError("Register number required");
            regno.requestFocus();
            return;
        }
        if (memail.isEmpty()) {
            email.setError("Email required");
            email.requestFocus();
            return;
        }
        if (!pwrd1.equals(pwrd2)) {
            pass.setError("Verify password");
            pass2.setError("Verify password");
            pass.requestFocus();
            pass2.requestFocus();
        }
        progressDialog.setMessage("Creating account....  please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(memail , pwrd1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            user user = new user(
                                    name,
                                    memail,
                                    regnum,
                                    pwrd2
                            );
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(register.this,getString(R.string.reg_sus),Toast.LENGTH_LONG).show();
                                        sendtoMain();
                                    }else {
                                        Toast.makeText(register.this,getString(R.string.reg_fail),Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void sendtoMain(){
        Intent i=new Intent(register.this,MainActivity.class);
        startActivity(i);
        finish();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sip:
                registerUser();
                break;
        }

    }


    public void havacc(View view) {
        Intent i= new Intent(register.this,login.class);
        startActivity(i);
        finish();

    }

    public void sup(View view) {
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(register.this,login.class);
        startActivity(intent);
    }
}
