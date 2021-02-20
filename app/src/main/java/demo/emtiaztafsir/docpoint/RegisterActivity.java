package demo.emtiaztafsir.docpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    Toolbar toolbar;
    EditText emailEt, passEt, confPassEt;
    Button regBtn;
    ProgressBar progressBar;
    TextView stats;
    private FirebaseAuth mAuth;
    private FirebaseUser cUser;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.register_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        initializeFields();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    private void attemptRegister() {
        String email = emailEt.getText().toString();
        String pass =  passEt.getText().toString();
        String confPass = confPassEt.getText().toString();

        if( checkEmail(email) && checkPass(pass, confPass))
            proceedRegistration(email,pass);
    }

    private void proceedRegistration(final String email, final String pass) {
        progressBar.setVisibility(View.VISIBLE);
        stats.setVisibility(View.VISIBLE);
        stats.setText("Creating Account");
        regBtn.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    stats.setText("Account Creation Successful");
                    progressBar.setVisibility(View.INVISIBLE);
                    cUser = mAuth.getCurrentUser();
                    String uID = cUser.getUid();

                    rootRef.child("Users").child(uID).setValue("");

                    Intent i = new Intent();
                    i.putExtra("email", email);
                    i.putExtra("pass", pass);
                    setResult(RESULT_OK, i);
                    finish();
                }
                else{
                    String err = task.getException().toString();
                    stats.setText("Failed: "+err);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                regBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private boolean checkPass(String pass, String confPass) {
        if(TextUtils.isEmpty(pass)){
            passEt.setError("Field Empty");
        }
        else if(TextUtils.isEmpty(confPass)){
            confPassEt.setError("Field Empty");
        }
        else if(pass.length()<6)
            passEt.setError("Must be 6 characters or more");
        else if(!pass.equals(confPass)) {
            passEt.setError("Password Mismatch");
            confPassEt.setError("Password Mismatch");
        }
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

    private void initializeFields() {
        emailEt = findViewById(R.id.registerEmail);
        passEt = findViewById(R.id.registerPass);
        confPassEt = findViewById(R.id.registerConfPass);
        regBtn = findViewById(R.id.register_btn);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.prog_bar_reg);
        stats = findViewById(R.id.prog_stat_reg);
        rootRef = FirebaseDatabase.getInstance().getReference();
    }
}
