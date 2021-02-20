package demo.emtiaztafsir.docpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import demo.emtiaztafsir.docpoint.doctor.DoctorActivity;
import demo.emtiaztafsir.docpoint.user.UserActivity;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private TextView status;
    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        ImageView logo = findViewById(R.id.splash_logo);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade);
        logo.startAnimation(animation);


        mAuth = FirebaseAuth.getInstance();
        cUser = mAuth.getCurrentUser();
        if(cUser==null){
            isLogged = false;
            changeStatus("Not Logged In");
        }
        else{
            isLogged = true;
            changeStatus("Log In Successful");
        }
        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if(isLogged){
                        sendToMain();
                    }
                    else {
                        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.start();
    }

    private void changeStatus(String msg) {
        status = findViewById(R.id.splash_status_txt);
        status.setText(msg);
    }

    private void sendToMain() {
        String uID = cUser.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("Users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("type").exists()){
                    String uType = snapshot.child("type").getValue().toString();
                    goTo(uType);
                }
                else{
                    redirectToInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void redirectToInfo() {
        Intent docIntent = new Intent(StartActivity.this, UpdateInfoActivity.class);
        startActivity(docIntent);
        finish();
    }

    private void goTo(String type) {
        if(type.equals("patient")){
            Intent userIntent = new Intent(StartActivity.this, UserActivity.class);
            startActivity(userIntent);
            finish();
        }
        else if(type.equals("doctor")){
            Intent docIntent = new Intent(StartActivity.this, DoctorActivity.class);
            startActivity(docIntent);
            finish();
        }

    }
}
