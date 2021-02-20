package demo.emtiaztafsir.docpoint.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import demo.emtiaztafsir.docpoint.LoginActivity;
import demo.emtiaztafsir.docpoint.R;

public class UserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager uViewPager;
    private TabLayout uTabLayout;
    private UserTabsAdapter uTabAdapter;
    private FirebaseAuth mAuth;
    private String uid;
    private DatabaseReference userRef;
    private FusedLocationProviderClient fusedLocationProviderClient;
    String[] location=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        toolbar = findViewById(R.id.user_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DocPoint");

        uViewPager = findViewById(R.id.user_pager);
        uTabAdapter = new UserTabsAdapter(getSupportFragmentManager());
        uTabLayout = findViewById(R.id.user_tabs);
        uViewPager.setAdapter(uTabAdapter);
        uTabLayout.setupWithViewPager(uViewPager);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(uid);

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getLocation() {
        final String[] results =new String[3];
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location loc = task.getResult();
                if(loc!=null){
                    try {
                        Geocoder geo = new Geocoder(UserActivity.this, Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                        results[0] = ""+addresses.get(0).getLatitude();
                        results[1] = ""+addresses.get(0).getLongitude();
                        results[2] = ""+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName();
                        updateLocation(results);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(UserActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateLocation(String[] results) {
        location = results;
        Log.v("FindDoc", location[2]);
        userRef.child("latitude").setValue(results[0]);
        userRef.child("longtitude").setValue(results[1]);
        userRef.child("city").setValue(results[2]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_item:
                return true;
            case R.id.signout_item:
                AlertDialog.Builder popup = new AlertDialog.Builder(this);
                popup.setTitle("LOG OUT");
                popup.setMessage("Are you sure?");
                popup.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        Intent login = new Intent(UserActivity.this, LoginActivity.class);
                        startActivity(login);
                        finish();
                    }
                });
                popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = popup.create();
                dialog.show();
                return true;
            default:
                return false;
        }
    }
}
