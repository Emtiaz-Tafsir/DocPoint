package demo.emtiaztafsir.docpoint.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import demo.emtiaztafsir.docpoint.LoginActivity;
import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Patient;

public class DoctorActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager dViewPager;
    private TabLayout dTabLayout;
    private DoctorTabsAdapter dTabAdapter;
    private FirebaseAuth mAuth;
    private String uid;
    private DatabaseReference userRef;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference rootref;
    private PendingAppAdapter paAdapter;
    private AcceptedAppAdapter acAdapter;
    private ArrayList<Patient> penPat;
    private ArrayList<Patient> accPat;
    private Set<String> allApp;
    private String[] location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        toolbar = findViewById(R.id.doc_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DocPoint");

        dViewPager = findViewById(R.id.doc_pager);
        dTabAdapter = new DoctorTabsAdapter(getSupportFragmentManager());
        dTabLayout = findViewById(R.id.doc_tabs);
        dViewPager.setAdapter(dTabAdapter);
        dTabLayout.setupWithViewPager(dViewPager);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();
        userRef = rootref.child("Doctors").child(uid);
        allApp = new HashSet<>();
        accPat = new ArrayList<>();
        penPat = new ArrayList<>();
        paAdapter = new PendingAppAdapter(this,penPat);
        acAdapter = new AcceptedAppAdapter(this,accPat);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
        retrieveAppointments();

    }

    private void retrieveAppointments() {
        rootref.child("Doctors").child(uid).child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    allApp.add(((DataSnapshot)iterator.next()).getKey());
                }
                filterAppointments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterAppointments() {
        ArrayList<String> allKeys = new ArrayList<>();
        allKeys.addAll(allApp);
        final ArrayList<Patient> tempPenPat = new ArrayList<>();
        final ArrayList<Patient> tempAccpat = new ArrayList<>();
        final int[] notifier = {allKeys.size()};
        while (allKeys.size()!=0){
            String key = allKeys.remove(0);
            rootref.child("Appointments").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notifier[0]--;
                    if(snapshot.exists()) {
                        final String appKey = snapshot.getKey();
                        String pUid = snapshot.child("patient").getValue().toString();
                        String stat = snapshot.child("status").getValue().toString();
                        if(stat.equals("pending")){
                            rootref.child("Patients").child(pUid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String key = snapshot.getKey();
                                        String name = snapshot.child("name").getValue().toString();
                                        String age = snapshot.child("age").getValue().toString();
                                        String lat = snapshot.child("latitude").getValue().toString();
                                        String lon = snapshot.child("longtitude").getValue().toString();
                                        String dist = "Unknown";
                                        if(location!=null){
                                            Location me = new Location("Me");
                                            me.setLatitude(Double.parseDouble(location[0]));
                                            me.setLongitude(Double.parseDouble(location[1]));
                                            Location doc = new Location("Doc");
                                            doc.setLatitude(Double.parseDouble(lat));
                                            doc.setLongitude(Double.parseDouble(lon));
                                            dist = String.format("%.2f",me.distanceTo(doc)/1000);
                                        }
                                        tempPenPat.add(new Patient(key,name,age,dist+" km away", appKey));
                                        if(notifier[0]==0){
                                            penPat.clear();
                                            penPat.addAll(tempPenPat);
                                            paAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        else if(stat.equals("accepted")){
                            rootref.child("Patients").child(pUid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String key = snapshot.getKey();
                                        String name = snapshot.child("name").getValue().toString();
                                        String age = snapshot.child("age").getValue().toString();
                                        tempAccpat.add(new Patient(key,name,age, appKey));
                                        if(notifier[0]==0){
                                            accPat.clear();
                                            accPat.addAll(tempAccpat);
                                            acAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
                        Geocoder geo = new Geocoder(DoctorActivity.this, Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                        results[0] = ""+addresses.get(0).getLatitude();
                        results[1] = ""+addresses.get(0).getLongitude();
                        results[2] = ""+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName();
                        updateLocation(results);
                        DatabaseReference docLoc = rootref.child("DoctorLocations");
                        GeoFire gf = new GeoFire(docLoc);
                        gf.setLocation(uid,new GeoLocation(Double.parseDouble(results[0]),Double.parseDouble(results[1])));

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(DoctorActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateLocation(String[] results) {
        location = results;
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
                        Intent login = new Intent(DoctorActivity.this, LoginActivity.class);
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

    public PendingAppAdapter getPaAdapter() {
        return paAdapter;
    }

    public AcceptedAppAdapter getAcAdapter() {
        return acAdapter;
    }
}
