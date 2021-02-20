package demo.emtiaztafsir.docpoint.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import demo.emtiaztafsir.docpoint.ChatActivity;
import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Doctor;

public class AppointmentAdapter extends ArrayAdapter<Doctor> {

    public AppointmentAdapter(@NonNull Context context, @NonNull ArrayList<Doctor> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(convertView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_user_app, parent, false);
        }
        final Doctor current = getItem(position);
        TextView docNameTV = listItemView.findViewById(R.id.user_ap_name);
        TextView qualiTV = listItemView.findViewById(R.id.user_ap_quali);
        TextView statTV = listItemView.findViewById(R.id.user_ap_stat);
        LinearLayout holder = listItemView.findViewById(R.id.user_ap_holder);
        final Button chatBtn = listItemView.findViewById(R.id.user_ap_chat);
        docNameTV.setText(current.getName());
        qualiTV.setText(current.getQualification());
        String stat = current.getAppStatus();
        if(stat.equals("accepted")) statTV.setTextColor(Color.GREEN);
        statTV.setText(current.getAppStatus());

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.getAppStatus().equals("accepted")) {
                    if (chatBtn.getVisibility() == View.VISIBLE) chatBtn.setVisibility(View.GONE);
                    else chatBtn.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(getContext(),"Appointment has not been approved yet", Toast.LENGTH_SHORT).show();
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String pid = current.getUid();

                FirebaseDatabase.getInstance().getReference().child("Patients").child(uid).child("chats").child(pid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String chtID = snapshot.getValue().toString();
                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                            chatIntent.putExtra("me", uid);
                            chatIntent.putExtra("other", pid);
                            chatIntent.putExtra("convoKey", chtID);
                            chatIntent.putExtra("type",1);
                            getContext().startActivity(chatIntent);
                        }
                        v.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        v.setEnabled(true);
                    }
                });
            }
        });

        return listItemView;
    }
}
