package com.nikos.tsoglani.androidmouse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

import lt.lemonlabs.android.expandablebuttonmenu.ExpandableButtonMenu;
import lt.lemonlabs.android.expandablebuttonmenu.ExpandableMenuOverlay;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nikos.tsoglani.androidmouse.R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);

        final ExpandableMenuOverlay menuOverlay = (ExpandableMenuOverlay) findViewById(R.id.button_menu);
        menuOverlay.setOnMenuButtonClickListener(new ExpandableButtonMenu.OnMenuButtonClick() {
            @Override
            public void onClick(ExpandableButtonMenu.MenuButton action) {
                switch (action) {
                    case MID:
                        // do stuff and dismiss
                        connectFunction(menuOverlay);
                        menuOverlay.getButtonMenu().toggle();
                        break;
                    case LEFT:
                        bluetoothConnectFunction(menuOverlay);
                        menuOverlay.getButtonMenu().toggle();
                        break;
                    case RIGHT:
                        internet(menuOverlay);

                        menuOverlay.getButtonMenu().toggle();
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.nikos.tsoglani.androidmouse.R.menu.menu_main, menu);


        return true;
    }


    public void connectFunction(View v) {
        Intent intent = new Intent(this, InternetConnection.class);

        startService(intent);

    }

    String ip = null;

    public void internet(View v) {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {


                        final EditText input = new EditText(MainActivity.this);
                        SharedPreferences settings = getSharedPreferences("RemoteControl", 0);
                        input.setText(settings.getString("Global Ip", "").toString());
                        ip = input.getText().toString();
                        LinearLayout ll = new LinearLayout(getApplicationContext());
                        final LinearLayout showLayout = new LinearLayout(getApplicationContext());
                        showLayout.setOrientation(LinearLayout.VERTICAL);
                        TextView txtView1 = new TextView(getApplicationContext());
                        txtView1.setText("Enter Public ip");
                        ll.addView(txtView1);
                        ll.addView(input);
                        input.setMinEms(10);

                        final EditText input2 = new EditText(MainActivity.this);


                        input2.setText(settings.getString("NickName", "").toString());
                        input2.setFocusable(true);
                        input2.setClickable(true);
                        input2.setFocusableInTouchMode(true);
                        input2.setSelectAllOnFocus(true);
                        input2.setSingleLine(true);
                        final LinearLayout ll2 = new LinearLayout(getApplicationContext());
                        TextView txtView2 = new TextView(getApplicationContext());
                        txtView2.setText("Computer's Name");
                        input2.setMinEms(10);
                        ll2.addView(txtView2);
                        ll2.addView(input2);
                        showLayout.addView(new View(getApplicationContext()), new ViewGroup.LayoutParams(100, 50));
                        showLayout.addView(ll);
                        showLayout.addView(new View(getApplicationContext()), new ViewGroup.LayoutParams(100, 200));
                        showLayout.addView(ll2);
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "Be sure you have the same username on computer", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Remote Controll with public IP")
                                                //.setMessage("Enter Public ip")

                                        .setView(showLayout)

                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                String ip2 = input.getText().toString();
                                                if (ip2 == null || ip2.replace(" ", "").equals("")) {
                                                    runOnUiThread(new Thread() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getApplicationContext(), "Enter a real IP", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    return;
                                                }
                                                if (ip != ip2) {
                                                    SharedPreferences settings = getSharedPreferences("RemoteControl", 0);
                                                    SharedPreferences.Editor editor = settings.edit();
                                                    editor.putString("Global Ip", input.getText().toString());


                                                    editor.putString("NickName", input2.getText().toString());


                                                    editor.commit();
                                                    // deal with the editable
                                                }
                                                ip = ip2;
                                                if (ip == null || ip.equals("")) {
                                                    Toast.makeText(getApplicationContext(), "Not Valid IP .. ", Toast.LENGTH_SHORT).show();

                                                    return;
                                                }

                                                Toast.makeText(getApplicationContext(), "Wait .. ", Toast.LENGTH_SHORT).show();

                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        try {

                                                            Socket s = new Socket();

                                                            // s.setReuseAddress(true);
                                                            s.connect(new InetSocketAddress(ip, InternetConnection.port));
                                                            runOnUiThread(new Thread() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "success .. ", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });


                                                            InternetConnection.returnSocket = s;
                                                            MouseUIActivity.ps = new PrintWriter(InternetConnection.returnSocket.getOutputStream(), true);
                                                            MouseUIActivity.bf = new DataInputStream(InternetConnection.returnSocket.getInputStream());
                                                            MouseUIActivity.ps.println("GLOBAL_IP:" + input2.getText().toString());
                                                            runOnUiThread(new Thread() {
                                                                @Override
                                                                public void run() {
                                                                    Log.e(Boolean.toString(InternetConnection.returnSocket.isConnected()), "InternetConnection.returnSocket.isConnected()");
                                                                    Intent intent = new Intent(MainActivity.this, MouseUIActivity.class);
                                                                    intent.putExtra("Type", "Internet");
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                                                    startActivity(intent);

                                                                }
                                                            });
                                                        } catch (final Exception e) {
                                                            runOnUiThread(new Thread() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }.start();


                                            }
                                        })
                                        .show();
                            }
                        });

//                        s.connect((new InetSocketAddress(InetAddress.getByName("78.87.53.120"), 2000)), 5000);

//                    s = new Socket(ip, 6667);


                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Not supported yet : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Not supported yet", Toast.LENGTH_SHORT).show();
        }

    }


    public void infoFunction(View v) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        TextView message = new TextView(MainActivity.this);
        Spanned myLnk = Html.fromHtml("<a href=https://raw.githubusercontent.com/tsoglani/AndroidRemoteJavaServer/master/Internet%20Image%20Example/Screenshot%202.png> Hole punching Example</a>");


        message.setText("  This application makes possible to control your computer's Device from your mobile phone, you are able to move your mouse from your android device.\n" +
                        "works via Wifi (Wlan-Hotspot) or Bluetooth, you have just to download and run this jar file ->(https://github.com/tsoglani/AndroidRemoteJavaServer/tree/master/store) on your computer, and choose the type of connection you want to use(Wlan, bluetooth)." +
                        " \n-Parameters for Wlan connection :\n" +
                        "1) The computer must share a local network.\n" +
                        "2) The android device must be connected to the computer's network.\n" +
                        "\n" +
                        "-Parameters for Bluetooth connection :\n" +
                        "1) You must have already done the connection (You must have the computer on my devices before use it).\n" +
                        "2) Much more slower than network connection and needs time to connect (so, not recommended).\n" +
                        " \n-Parameters for Internet connection :\n" +
                        "1) You have to do port forwarding.\n" +
                        "On PC GOTO : Router preferences (propubly needs to press 192.168.1.1 on your Browser)->Nat ->Virtual Server -> Lan ip Address = your pc local address->Lan port=2000, public port=2000  -->"
        );
        message.append(myLnk);
        message.append("\n2) Enter the Public/External PC ip in your android device " +
                "\n3)Your computer must run this jar file ->(https://github.com/tsoglani/AndroidRemoteJavaServer/tree/master/store) on WLAN-Internet mode.\n" +
                "\n" +
                "\n" +
                "\n\n Directed By Tsoglani");
        message.setMovementMethod(LinkMovementMethod.getInstance());

        // set title
        alertDialogBuilder.setTitle("Info");
        // Linkify.addLinks();        // set dialog message
        alertDialogBuilder
                .setView(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity

                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it\

        alertDialog.show();


    }


    public void bluetoothConnectFunction(View v) {

        Toast.makeText(getApplicationContext(), "Wait", Toast.LENGTH_LONG).show();
        new Thread() {
            @Override
            public void run() {


                try {
                    if (new MyBlueTooth(MainActivity.this).execute().get()) {
                        Intent intent = new Intent(MainActivity.this, MouseUIActivity.class);
                        intent.putExtra("Type", "bluetooth");
                        startActivity(intent);
                    } else {
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "No Bluetooth connection", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
