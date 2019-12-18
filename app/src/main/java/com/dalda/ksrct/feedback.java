package com.dalda.ksrct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class feedback extends AppCompatActivity {
    private EditText feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        feedback=findViewById(R.id.feedbac);
        Button fedbtn = findViewById(R.id.bunfeed);

        fedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fdb = feedback.getText().toString();

                FirebaseDatabase.getInstance().getReference("feedback")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(fdb).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(feedback.this,getString(R.string.msgsent),Toast.LENGTH_LONG).show();
                            backtomain();
                        }else {
                            Toast.makeText(feedback.this,getString(R.string.msgnotsent),Toast.LENGTH_LONG).show();
                             }

                    }
                });
            }
        });
    }

    private void backtomain() {
        Intent intent = new Intent(com.dalda.ksrct.feedback.this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
