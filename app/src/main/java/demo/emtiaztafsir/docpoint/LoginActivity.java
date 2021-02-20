package demo.emtiaztafsir.docpoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import demo.emtiaztafsir.docpoint.doctor.DoctorActivity;
import demo.emtiaztafsir.docpoint.user.UserActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    Toolbar toolbar;
    EditText emailEt, passEt;
    Button loginBtn;
    TextView register;
    ProgressBar progressBar;
    TextView stats;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        initializeFields();

        
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(registerIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            String email = data.getStringExtra("email");
            String pass = data.getStringExtra("pass");
            emailEt.setText(email);
            passEt.setText(pass);
            proceedLogin(email,pass);
        }
        else if(requestCode==1 && resultCode==RESULT_CANCELED){

        }
    }

    private void attemptLogin() {
        String email = emailEt.getText().toString();
        String pass =  passEt.getText().toString();
        if( checkEmail(email) && checkPass(pass))
            proceedLogin(email,pass);
    }

    private boolean checkPass(String pass) {
        if(TextUtils.isEmpty(pass)){
            passEt.setError("Field Empty");
        }
        else if(pass.length()<6)
            passEt.setError("Must be 6 characters or more");
        else
            return true;
        return false;
    }

    private boolean checkEmail(String email) {
        if(TextUtils.isEmpty(email)){
            emailEt.setError("Field Empty");
        }
        else if(!email.contains("@") || !email.contains(".")){
            emailEt.setError("Invalid Email");
        }
        else
            return true;
        return false;
    }

    private void proceedLogin(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
        stats.setText("Logging In");
        loginBtn.setEnabled(false);
        register.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    stats.setText("Log in Successful");
                    progressBar.setVisibility(View.INVISIBLE);
                    sendToMain();
                }
                else{
                    String err = task.getException().getMessage();
                    stats.setText("Failed: "+err);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                loginBtn.setEnabled(true);
                register.setEnabled(true);
            }
        });
    }

    private void sendToMain() {
        FirebaseUser cUser = mAuth.getCurrentUser();
        String uID = cUser.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("Users").child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
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
        Intent docIntent = new Intent(LoginActivity.this, UpdateInfoActivity.class);
        startActivity(docIntent);
        finish();
    }

    private void goTo(String type) {
        if(type.equals("patient")){
            Intent userIntent = new Intent(LoginActivity.this, UserActivity.class);
            startActivity(userIntent);
            finish();
        }
        else if(type.equals("doctor")){
            Intent docIntent = new Intent(LoginActivity.this, DoctorActivity.class);
            startActivity(docIntent);
            finish();
        }

    }

    private void initializeFields() {
        emailEt = findViewById(R.id.loginEmail);
        passEt = findViewById(R.id.loginPass);
        loginBtn = findViewById(R.id.login_btn);
        register = findViewById(R.id.go_reg_txt);
        progressBar = findViewById(R.id.prog_bar_log);
        stats = findViewById(R.id.prog_stat_log);
        mAuth = FirebaseAuth.getInstance();
    }

}
