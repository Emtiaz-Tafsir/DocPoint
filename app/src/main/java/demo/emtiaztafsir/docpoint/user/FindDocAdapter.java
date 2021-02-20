package demo.emtiaztafsir.docpoint.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Doctor;

public class FindDocAdapter extends ArrayAdapter<Doctor> {
    public FindDocAdapter(@NonNull Context context, @NonNull ArrayList<Doctor> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_find_doc, parent, false);
        }
        final Doctor current = getItem(position);
        TextView docNameTV = listItemView.findViewById(R.id.li_fd_name);
        TextView qualiTV = listItemView.findViewById(R.id.li_fd_quali);
        TextView distanceTV = listItemView.findViewById(R.id.li_fd_distance);
        LinearLayout holder = listItemView.findViewById(R.id.li_fd_holder);
        final Button appBtn = listItemView.findViewById(R.id.li_fd_req_app);
        docNameTV.setText(current.getName());
        qualiTV.setText(current.getQualification());
        distanceTV.setText(current.getDistance());

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appBtn.getVisibility()==View.VISIBLE) appBtn.setVisibility(View.GONE);
                else appBtn.setVisibility(View.VISIBLE);
            }
        });

        appBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                HashMap<String,String> map = new HashMap<>();
                map.put("patient",cuid);
                map.put("doctor",current.getUid());
                map.put("status","pending");
                final String key = rootRef.child("Appointments").push().getKey();
                rootRef.child("Appointments").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            rootRef.child("Patients").child(cuid).child("appointments").child(key).setValue("")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                rootRef.child("Doctors").child(current.getUid()).child("appointments").child(key).setValue("")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    appBtn.setText("Request Sent");
                                                                    appBtn.setEnabled(false);
                                                                }
                                                                else{
                                                                    Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else{
                                                Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        return listItemView;
    }
}
