package demo.emtiaztafsir.docpoint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.emtiaztafsir.docpoint.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private DatabaseReference messageRef;
    private String uid;

    public MessageAdapter(List<Message> messages, String uid) {
        this.messages = messages;
        this.uid = uid;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderTv, recieverTv;
        public CircleImageView reciverImg;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderTv = itemView.findViewById(R.id.sender_msg);
            recieverTv = itemView.findViewById(R.id.reciever_msg);
            reciverImg = itemView.findViewById(R.id.msg_profile_img);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_msg_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messages.get(position);
        String body = msg.getBody();
        String from = msg.getFrom();

        holder.reciverImg.setVisibility(View.INVISIBLE);
        holder.recieverTv.setVisibility(View.INVISIBLE);
        holder.senderTv.setVisibility(View.INVISIBLE);
        if(from.equals(uid)){
            holder.senderTv.setVisibility(View.VISIBLE);
            holder.senderTv.setBackgroundResource(R.drawable.sender_msg_container);
            holder.senderTv.setText(body);
        }
        else{
            holder.recieverTv.setBackgroundResource(R.drawable.reciever_msg_container);
            holder.reciverImg.setVisibility(View.VISIBLE);
            holder.recieverTv.setVisibility(View.VISIBLE);
            holder.recieverTv.setText(body);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}
