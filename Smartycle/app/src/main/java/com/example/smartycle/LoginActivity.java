package com.example.smartycle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText email;
    EditText password;
    Button signInBtn;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference userRef;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);
        signInBtn = findViewById(R.id.signIn);
        signInBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        progressDialog.setMessage("Login Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            addUserDetails();
                            Toast.makeText(LoginActivity.this, "auth_success",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "auth_failed",Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();

                    }
                });
    }


    public void addUserDetails(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        User user = new User(uid,email.getText().toString(), "");
        userRef = firebaseDatabase.getReference("Users").push();
        user.key = userRef.getKey();
        userRef.setValue(user);

    }
}
