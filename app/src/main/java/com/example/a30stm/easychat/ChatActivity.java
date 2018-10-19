package com.example.a30stm.easychat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import NegaTiV.ChatClient.Client;
import NegaTiV.ChatClient.Message;
import NegaTiV.ChatClient.ServerMessage;
import NegaTiV.ChatClient.UpdaterAction;

public class ChatActivity extends AppCompatActivity {

    public ImageButton btnSend;
    public EditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NovaMono.ttf");
        setContentView(R.layout.chat_layout);
        btnSend = findViewById(R.id.btn_send);
        edtMessage = findViewById(R.id.edt_message);
        ListView listView = findViewById(R.id.chat_area);
        final ArrayList<ServerMessage> messages = new ArrayList<>();
        // Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView
        final ChatArrayAdapter adapter = new ChatArrayAdapter(this, messages);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOk;
                if (edtMessage.getText().length() > 0) {
                    isOk = Client.SendMessage(edtMessage.getText().toString());
                    edtMessage.getText().clear();
                    if (!isOk)
                    {
                        Toast.makeText(ChatActivity.this, getResources().getText(R.string.error_lostconection), Toast.LENGTH_LONG).show();
                        Client.reset();
                        finish();
                    }
                }
            }
        });
        Client.setUpdaterAction(new UpdaterAction() {
            @Override
            public void Update(final ServerMessage msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messages.add(0, msg);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        edtMessage.setTypeface(typeface);
        edtMessage.requestFocus();
        TextWatcher txt = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtMessage.getText().length() != 0) {
                    btnSend.setImageDrawable(getResources().getDrawable(R.drawable.send));
                } else {
                    btnSend.setImageDrawable(getResources().getDrawable(R.drawable.nth_send));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edtMessage.addTextChangedListener(txt);

    }


    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(ChatActivity.this);
        quitDialog.setTitle("All messages will be lost!");
        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Client.Send(new Message(Message.MsgType.QUIT, ""));
                        Client.reset();
                    }
                }).start();
                finish();
            }
        });

        quitDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }


}
