package com.example.a30stm.easychat;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import NegaTiV.ChatClient.Client;

public class MainActivity extends AppCompatActivity {
    public static String myName;
    public Button btnLogin;
    public EditText edtName;
    public TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btn_login);
        edtName = findViewById(R.id.edt_name);
        txtTitle = findViewById(R.id.txt_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NovaMono.ttf");
        txtTitle.setTypeface(typeface);
        edtName.setTypeface(typeface);
        btnLogin.setTypeface(typeface);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtName.getText().length() != 0) {
                    btnLogin.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Thread th1 = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!Client.isIsConnected())
                                        Client.Connect();
                                }
                            });
                            th1.start();
                            try {
                                th1.join();

                            } catch (InterruptedException e) {
                                System.out.println(e.toString() + "--------------------------------------------");
                            }
                            final String name = edtName.getText().toString();
                            myName = name;
                            if (Client.isIsConnected())
                            {
                                Thread th2 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Client.Login(name);
                                    }
                                });
                                th2.start();
                                try {
                                    th2.join();

                                } catch (InterruptedException e) {
                                    System.out.println(e.toString() + "--------------------------------------------");
                                }
                            }
                            if (Client.isIsConnected()) {
                                if (Client.isIsLogined()) {
                                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                    startActivity(intent);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnLogin.setEnabled(true);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, getResources().getText(R.string.error_login), Toast.LENGTH_SHORT).show();
                                            btnLogin.setEnabled(true);
                                        }
                                    });
                                }
                            }
                            else
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, getResources().getText(R.string.error_cantconnect), Toast.LENGTH_SHORT).show();
                                        btnLogin.setEnabled(true);
                                    }
                                });
                            }
                        }
                    }
                    ).start();
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, getResources().getText(R.string.txt_toast), Toast.LENGTH_SHORT);
                    TextView txtToast = toast.getView().findViewById(android.R.id.message);
                    txtToast.setGravity(Gravity.CENTER);
                    toast.show();
                }
            }
        });

    }
}
