package demo.emtiaztafsir.docpoint.user;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Doctor;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentsFragment extends Fragment {

    private DatabaseReference rootref;
    private String uid;
    private ArrayList<Doctor> accAppDet;
    private Set<String> allAppId;
    private AppointmentAdapter apAdapter;
    private ListView lv;

    public AppointmentsFragment() {
        rootref = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_appointments, container, false);
        lv = root.findViewById(R.id.usr_app_list);
        allAppId = new HashSet<>();
        accAppDet = new ArrayList<>();
        apAdapter = new AppointmentAdapter(getContext(),accAppDet);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        lv.setAdapter(apAdapter);
        retrieveAppointments();

    }

    private void retrieveAppointments() {
        rootref.child("Patients").child(uid).child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator iterator = snapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    allAppId.add(((DataSnapshot)iterator.next()).getKey());
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
        allKeys.addAll(allAppId);
        final ArrayList<Doctor> tempPenPat = new ArrayList<>();
        final int[] notifier = {allKeys.size()};
        while (allKeys.size()!=0){
            String key = allKeys.remove(0);
            rootref.child("Appointments").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notifier[0]--;
                    if(snapshot.exists()) {
                        final String appKey = snapshot.getKey();
                        final String dUid = snapshot.child("doctor").getValue().toString();
                        final String stat = snapshot.child("status").getValue().toString();
                        rootref.child("Doctors").child(dUid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String key = snapshot.getKey();
                                    String name = snapshot.child("name").getValue().toString();
                                    String quali = snapshot.child("qualification").getValue().toString();
                                    tempPenPat.add(new Doctor(key,name,quali,appKey, stat));
                                    if(notifier[0]==0){
                                        accAppDet.clear();
                                        accAppDet.addAll(tempPenPat);
                                        apAdapter.notifyDataSetChanged();
                                    }
                                }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
