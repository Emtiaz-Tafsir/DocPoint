package demo.emtiaztafsir.docpoint;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import demo.emtiaztafsir.docpoint.doctor.DoctorActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewDoctorFragment extends Fragment {

    private EditText nameEt, qualiEt, cityEt;
    private Button updateBtn;
    private DatabaseReference rootRef;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String uid;

    public NewDoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_new_doctor, container, false);

        nameEt = root.findViewById(R.id.set_doc_name);
        qualiEt = root.findViewById(R.id.set_doc_qualif);
        cityEt = root.findViewById(R.id.set_doc_city);
        updateBtn = root.findViewById(R.id.send_doc_info);
        rootRef = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpload();
            }
        });

        return root;
    }

    private void attemptUpload() {
        String name = nameEt.getText().toString();
        String quali = qualiEt.getText().toString();
        String city = cityEt.getText().toString();

        if(infoIsValid(name, quali, city)) {
            prepareUpload();
        }
    }

    private void prepareUpload() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
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
                        Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
                        results[0] = ""+addresses.get(0).getLatitude();
                        results[1] = ""+addresses.get(0).getLongitude();
                        results[2] = ""+addresses.get(0).getLocality()+", "+addresses.get(0).getCountryName();
                        updateType(results);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void updateType(final String[] loc) {

        rootRef.child("Users").child(uid).child("type").setValue("doctor").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadToDB(loc);
            }
        });
    }

    private void uploadToDB(String[] loc) {
        String name = nameEt.getText().toString();
        String quali = qualiEt.getText().toString();

        HashMap<String,String> profMap = new HashMap<>();
        profMap.put("name",name);
        profMap.put("qualification",quali);
        profMap.put("city",loc[2]);
        profMap.put("latitude",loc[0]);
        profMap.put("longtitude", loc[1]);
        cityEt.setText(loc[2]);
        Log.v("NewDoc", loc[0]+loc[1]+loc[2]);

        rootRef.child("Doctors").child(uid).setValue(profMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendToDoc();
                }
                else{
                    Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference docLoc = rootRef.child("DoctorLocations");
        GeoFire gf = new GeoFire(docLoc);
        gf.setLocation(uid,new GeoLocation(Double.parseDouble(loc[0]),Double.parseDouble(loc[1])));

    }

    private void sendToDoc() {
        Intent docIntent = new Intent(getContext(), DoctorActivity.class);
        docIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(docIntent);
        getActivity().finish();
    }

    private boolean infoIsValid(String name, String quali, String city) {

        boolean valid = true;

        if(TextUtils.isEmpty(name)) {
            nameEt.setError("Field Empty");
            valid = false;
        }
        if(TextUtils.isEmpty(quali)) {
            qualiEt.setError("Field Empty");
            valid = false;
        }
        if(TextUtils.isEmpty(city)) {
            cityEt.setError("Field Empty");
            valid = false;
        }
        return valid;
    }

}
