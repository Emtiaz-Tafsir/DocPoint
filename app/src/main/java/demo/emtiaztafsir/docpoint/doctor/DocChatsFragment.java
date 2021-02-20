package demo.emtiaztafsir.docpoint.doctor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import demo.emtiaztafsir.docpoint.ChatActivity;
import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Chat;
import demo.emtiaztafsir.docpoint.user.ChatAdapter;

public class DocChatsFragment extends Fragment {

    private ListView lv;
    private DatabaseReference rootRef;
    private String uid;
    private HashMap<String, Chat> map;
    private ArrayList<Chat> chatList;
    private ChatAdapter chatAdapter;

    public DocChatsFragment() {
        rootRef = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View chatRoot =  inflater.inflate(R.layout.fragment_doc_chats, container, false);
        lv = chatRoot.findViewById(R.id.chats_doc_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("me", uid);
                chatIntent.putExtra("other", chatList.get(position).getpID());
                chatIntent.putExtra("convoKey", chatList.get(position).getChatID());
                chatIntent.putExtra("type",0);
                startActivity(chatIntent);
            }
        });
        return chatRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        map = new HashMap<>();
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(),chatList);
        lv.setAdapter(chatAdapter);
        retrieveChatList();
    }

    private void retrieveChatList() {

        rootRef.child("Doctors").child(uid).child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                addToList(snapshot.getKey(), snapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void addToList(String docID, String chatID) {
        Chat c = new Chat(docID, chatID);
        map.put(docID, c);
        chatList.add(c);
        rootRef.child("Patients").child(docID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("name").exists()){
                    map.get(snapshot.getKey()).setpName(snapshot.child("name").getValue().toString());
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
