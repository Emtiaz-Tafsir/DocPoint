package demo.emtiaztafsir.docpoint.doctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import demo.emtiaztafsir.docpoint.ChatActivity;
import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Patient;

public class AcceptedAppAdapter extends ArrayAdapter<Patient> {
    public AcceptedAppAdapter(@NonNull Context context, @NonNull ArrayList<Patient> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_current_app, parent, false);
        }
        final Patient current = getItem(position);
        TextView patNameTV = listItemView.findViewById(R.id.ac_ap_name);
        TextView ageTV = listItemView.findViewById(R.id.ac_ap_age);
        LinearLayout holder = listItemView.findViewById(R.id.ac_ap_holder);
        final Button chtBtn = listItemView.findViewById(R.id.ac_ap_chat);


        patNameTV.setText(current.getName());
        ageTV.setText(current.getAge()+" years old");

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chtBtn.getVisibility()==View.VISIBLE) chtBtn.setVisibility(View.GONE);
                else chtBtn.setVisibility(View.VISIBLE);
            }
        });

        chtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String pid = current.getUid();

                FirebaseDatabase.getInstance().getReference().child("Doctors").child(uid).child("chats").child(pid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String chtID = snapshot.getValue().toString();
                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                            chatIntent.putExtra("me", uid);
                            chatIntent.putExtra("other", pid);
                            chatIntent.putExtra("convoKey", chtID);
                            chatIntent.putExtra("type",0);
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
