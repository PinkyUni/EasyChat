package com.example.a30stm.easychat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import NegaTiV.ChatClient.ServerMessage;

import static com.example.a30stm.easychat.MainActivity.myName;
import static com.example.a30stm.easychat.MainActivity.typefaceRegular;

public class ChatArrayAdapter extends ArrayAdapter<ServerMessage> {
    private final Context context;
    private final ArrayList<ServerMessage> values;

    public ChatArrayAdapter(Context context, ArrayList<ServerMessage> values) {
        super(context, R.layout.their_message_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.their_message_layout, parent, false);
        TextView messageBody = rowView.findViewById(R.id.message_body);
        messageBody.setText(values.get(values.size() - 1 - position).getUserMessage());
        TextView messageName = rowView.findViewById(R.id.message_name);
        messageName.setText(values.get(values.size() - 1 - position).getUserName());
        if (!messageName.getText().equals(myName)) {
            rowView = inflater.inflate(R.layout.their_message_layout, parent, false);
            TextView name = rowView.findViewById(R.id.message_name);
            name.setText(values.get(values.size() - 1 - position).getUserName());
            name.setTypeface(typefaceRegular);
        } else {
            rowView = inflater.inflate(R.layout.my_message_layout, parent, false);
        }
        TextView text = rowView.findViewById(R.id.message_body);
        text.setText(values.get(values.size() - 1 - position).getUserMessage());
        text.setTypeface(typefaceRegular);
        return rowView;
    }
}