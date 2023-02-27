package com.example.smartycle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    ImageButton scanBtn;
    User user;
    Button points;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference smartycle;
    DatabaseReference arduino;

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
       progressDialog = new ProgressDialog(this);
        if (result.getContents() != null) {
            smartycle = firebaseDatabase.getReference(result.getContents() + "/picture");
            arduino = firebaseDatabase.getReference(result.getContents());
            smartycle.setValue(123);
            progressDialog.show();
                arduino.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isChecked = false;
                        for(int i = 0; i < 500; i++){

                            boolean found = true;

                            int differ = 0;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                String clubkey = data.getKey();
                                Log.d("club", clubkey);
                                if (clubkey.equals("check") || clubkey.equals("picture")) {
                                    isChecked = true;
                                    found = false;
                                }
                                else{
                                    if(isChecked){
                                        found = false;
                                    }
                                }
                                if (clubkey.equals("difference")) {
                                    differ = data.getValue(Integer.class);
                                }
                            }
                            if (found) {
                                user.points += differ;
                                DatabaseReference userRef = firebaseDatabase.getReference("Users/" + user.key);
                                userRef.setValue(user);
                                points.setText("הנקודות שצברת עד כה: " + String.valueOf(user.points));
                                Log.d("found!!!!!!!", String.valueOf(differ) );
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            progressDialog.dismiss();
            }

    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        points = findViewById(R.id.button4);
        retreiveUser();

        scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(v->{
            scanCode();
        });
        if(user!=null)
        points.setText("הנקודות שצברת עד כה: " + String.valueOf(user.points));
    }
    private void scanCode()
    {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }


    public void retreiveUser(){

        DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("/Users");
        databaseUsers.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                String uid = firebaseAuth.getCurrentUser().getUid().toString();
                Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();
                for(DataSnapshot data: snapshot.getChildren()){
                    String uidcheck =iter.next().child("uid").getValue(String.class);
                    Log.d("string uid", uidcheck + " ");

                    if(uid.equals(uidcheck)){
                        user = data.getValue(User.class);
                        break;
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}