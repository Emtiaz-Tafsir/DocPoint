package demo.emtiaztafsir.docpoint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import demo.emtiaztafsir.docpoint.model.Message;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView chatbarName;
    private EditText msgField;
    private ImageButton sendBtn;
    private String myID;
    private String otherID;
    private String chatID;
    private String otherType;
    private DatabaseReference rootRef;
    private String otherName;
    private DatabaseReference chatRef;
    private ArrayList<Message> msgs;
    private LinearLayoutManager manager;
    private MessageAdapter mAdapter;
    private RecyclerView msglist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        myID = intent.getStringExtra("me");
        otherID = intent.getStringExtra("other");
        chatID = intent.getStringExtra("convoKey");
        otherType = intent.getIntExtra("type",-1)==1 ? "Doctors" : "Patients";

        Log.i("ChatActivity", "me: "+myID);
        Log.i("ChatActivity", "other: "+otherID);
        Log.i("ChatActivity", "convoKey: "+chatID);
        Log.i("ChatActivity", "type: "+otherType);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_bar,null);
        actionBar.setCustomView(actionBarView);

        msgField = findViewById(R.id.input_txt);
        sendBtn = findViewById(R.id.send_msg_btn);
        chatbarName = findViewById(R.id.chat_bar_name);
        chatbarName.setText("Chat");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMessage();
            }
        });

        msgs = new ArrayList<>();
        msglist = findViewById(R.id.chat_holder);
        manager = new LinearLayoutManager(this);
        mAdapter = new MessageAdapter(msgs,myID);
        msglist.setLayoutManager(manager);
        msglist.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        rootRef = FirebaseDatabase.getInstance().getReference();
        retrieveName();
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID);
        chatRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                msgs.add(msg);
                mAdapter.notifyDataSetChanged();
                msglist.smoothScrollToPosition(msglist.getAdapter().getItemCount());
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

    private void retrieveName() {
        rootRef.child(otherType).child(otherID).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    otherName = snapshot.getValue().toString();
                    chatbarName.setText(otherName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void postMessage(){
        String msg = msgField.getText().toString();
        if(TextUtils.isEmpty(msg)){
            Toast.makeText(this, "Message Field Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = chatRef.child("messages").push().getKey();

        HashMap<String,String> messageBody = new HashMap<>();
        messageBody.put("from", myID);
        messageBody.put("body",msg);

        chatRef.child("messages").child(key).setValue(messageBody).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        msgField.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
}
