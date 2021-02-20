package demo.emtiaztafsir.docpoint.user;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Doctor;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindDoctorsFragment extends Fragment {


    private FusedLocationProviderClient fusedLocationProviderClient;
    private DatabaseReference rootRef;
    private String uid;
    private ArrayList<Doctor> doctors;
    private ListView lv;
    private FindDocAdapter fdAdapter;
    private String[] mylocation;
    private Set<String> keySet;

    public FindDoctorsFragment() {
        // Required empty public constructor
        rootRef = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_find_doctors, container, false);
        lv = root.findViewById(R.id.find_doc_list);
        doctors = new ArrayList<>();
        fdAdapter = new FindDocAdapter(getContext(),doctors);
        lv.setAdapter(fdAdapter);
        keySet = new HashSet<>();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        UserActivity parent = (UserActivity)getActivity();
        mylocation = parent.location;
        if(mylocation == null){
            checkPermission();
            Log.v("FindDoc", "Location null");
        }
        else{
            generateRadius(mylocation);
        }
    }

    private void checkPermission() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getLocation() {
        Log.v("FindDoc", "query started");
        final String[] results =new String[3];
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location loc = task.getResult();
                if(loc!=null){
                    try {
                        Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                        results[0] = ""+addresses.get(0).getLatitude();
                        results[1] = ""+addresses.get(0).getLongitude();
                        results[2] = ""+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName();
                        generateRadius(results);


                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void generateRadius(String[] loc) {
        mylocation = loc;
        GeoFire gf = new GeoFire(rootRef.child("DoctorLocations"));
        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(Double.parseDouble(loc[0]),Double.parseDouble(loc[1])),10);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getInfo(key,location);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.v("FindDoc", "All doc found");
                if(doctors.size()==0){
                    Toast.makeText(getContext(),"No doctors within 10km",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void getInfo(final String key, GeoLocation location) {
        Location me = new Location("Me");
        me.setLatitude(Double.parseDouble(mylocation[0]));
        me.setLongitude(Double.parseDouble(mylocation[1]));
        Location doc = new Location("Doc");
        doc.setLatitude(location.latitude);
        doc.setLongitude(location.longitude);
        double dist = me.distanceTo(doc);

        Log.v("FindDoc", "My lat: "+mylocation[0]+"  My lon:"+mylocation[1]);
        Log.v("FindDoc", "Doc lat: "+location.latitude+"  Doc lon:"+location.longitude);
        Log.v("FindDoc", "Distance in meter: "+dist);
        final double distance = dist / 1000;

        rootRef.child("Doctors").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();
                    String quali = snapshot.child("qualification").getValue().toString();
                    addDoctor(key, name,quali,distance);

                    Log.v("FindDoc", "Doc found "+name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Error: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDoctor(String key, String name, String quali, double distance) {
        if(keySet.contains(key)) return;
        doctors.add(new Doctor(key, name, quali,""+String.format("%.2f",distance)+" km away"));
        fdAdapter.notifyDataSetChanged();
        keySet.add(key);
    }
}
