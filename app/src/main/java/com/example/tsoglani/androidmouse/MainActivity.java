package com.nikos.tsoglani.androidmouse;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nikos.tsoglani.androidmouse.R.layout.activity_main);
        getWindow().setBackgroundDrawable(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.nikos.tsoglani.androidmouse.R.menu.menu_main, menu);


        return true;
    }



    public void connectFunction(View v) {
        Intent intent = new Intent(this, MouseUIActivity.class);
        intent.putExtra("Type", "Internet");
        startActivity(intent);
    }

    public void bluetoothConnectFunction(View v) {

            Toast.makeText(getApplicationContext(),"Wait",Toast.LENGTH_LONG).show();
            new Thread(){
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
                }}.start();

    }
}
