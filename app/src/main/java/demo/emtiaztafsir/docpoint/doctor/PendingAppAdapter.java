package demo.emtiaztafsir.docpoint.doctor;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Patient;

public class PendingAppAdapter extends ArrayAdapter<Patient> {

    ArrayList<Patient> currList;

    public PendingAppAdapter(@NonNull Context context,  @NonNull ArrayList<Patient> objects) {
        super(context, 0, objects);
        currList = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_pending_app, parent, false);
        }
        final Patient current = getItem(position);
        TextView patNameTV = listItemView.findViewById(R.id.pe_ap_name);
        TextView ageTV = listItemView.findViewById(R.id.pe_ap_age);
        TextView distanceTV = listItemView.findViewById(R.id.pe_ap_distance);
        LinearLayout holder = listItemView.findViewById(R.id.pe_ap_holder);
        final LinearLayout btHolder = listItemView.findViewById(R.id.pe_ap_bt_holder);
        final Button accBtn = listItemView.findViewById(R.id.pe_ap_acc_app);
        final Button refBtn = listItemView.findViewById(R.id.pe_ap_rej_app);


        patNameTV.setText(current.getName());
        ageTV.setText(current.getAge()+" years old");
        distanceTV.setText(current.getDistance());

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btHolder.getVisibility()==View.VISIBLE) btHolder.setVisibility(View.GONE);
                else btHolder.setVisibility(View.VISIBLE);
            }
        });
        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accBtn.setEnabled(false);
                refBtn.setEnabled(false);
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                rootRef.child("Appointments").child(current.getAppId()).child("status").setValue("accepted").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            final String key = rootRef.child("Chats").push().getKey();
                            rootRef.child("Doctors").child(myId).child("chats").child(current.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()){
                                        rootRef.child("Doctors").child(myId).child("chats").child(current.getUid()).setValue(key);
                                        rootRef.child("Patients").child(current.getUid()).child("chats").child(myId).setValue(key);
                                        rootRef.child("Chats").child(key).child("patient").setValue(current.getUid());
                                        rootRef.child("Chats").child(key).child("doctor").setValue(myId);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            currList.remove(position);
                            notifyDataSetChanged();
                        }
                        else{
                            accBtn.setEnabled(true);
                            refBtn.setEnabled(true);
                            Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        refBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accBtn.setEnabled(false);
                refBtn.setEnabled(false);
                FirebaseDatabase.getInstance().getReference().child("Appointments").child(current.getAppId()).child("status").setValue("rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            currList.remove(position);
                            notifyDataSetChanged();
                        }
                        else{
                            accBtn.setEnabled(true);
                            refBtn.setEnabled(true);
                            Toast.makeText(getContext(),"Error Occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return listItemView;
    }
}
