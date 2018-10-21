package com.example.a30stm.easychat;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import NegaTiV.ChatClient.Client;
import NegaTiV.ChatClient.Message;

public class MainActivity extends AppCompatActivity {
    public static String myName;
    public static Typeface typefaceBold, typefaceRegular;
    public Button btnLogin;
    public EditText edtName;
    public TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        typefaceBold = Typeface.createFromAsset(getAssets(), "fonts/Posterizer_KG_Sketch.otf");
        typefaceRegular = Typeface.createFromAsset(getAssets(), "fonts/CabinSketchCyr.ttf");

        btnLogin = findViewById(R.id.btn_login);
        edtName = findViewById(R.id.edt_name);
        txtTitle = findViewById(R.id.txt_title);

        txtTitle.setTypeface(typefaceBold);
        edtName.setTypeface(typefaceRegular);
        btnLogin.setTypeface(typefaceBold);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString(menu.getItem(i).toString());
            s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, s.length(), 0);
            item.setTitle(s);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int selectedItem = item.getItemId();

        // Операции для выбранного пункта меню
        switch (selectedItem) {
            case R.id.action_about:
                onClickAboutAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onClickAboutAction() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle(getResources().getString(R.string.about_title));
        quitDialog.setMessage(getResources().getString(R.string.about_text));

        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });

        quitDialog.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    protected void onStart(){
        super.onStart();

//       TODO при изменении текста должен меняться цвет с красного обратно и убираться картинка
        TextWatcher txt = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtName.getText().length() != 0) {
                    edtName.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    edtName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edtName.addTextChangedListener(txt);
//

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
                                            edtName.setTextColor(getResources().getColor(R.color.colorWarning));
                                            edtName.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.warning), null);
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
