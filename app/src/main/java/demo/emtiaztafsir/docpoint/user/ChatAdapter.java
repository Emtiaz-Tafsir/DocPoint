package demo.emtiaztafsir.docpoint.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import demo.emtiaztafsir.docpoint.R;
import demo.emtiaztafsir.docpoint.model.Chat;

public class ChatAdapter extends ArrayAdapter<Chat> {
    public ChatAdapter(@NonNull Context context, @NonNull List<Chat> objects) {
        super(context, 0, objects);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_chat_module, parent, false);
        }

        Chat current = getItem(position);
        TextView nameTV = listItemView.findViewById(R.id.cht_name);
        nameTV.setText(current.getpName());
        return listItemView;
    }
}
