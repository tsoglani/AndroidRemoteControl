package com.example.tsoglani.androidmouse;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.PrintWriter;
import android.support.v7.app.ActionBar;

public class MouseUIActivity extends ActionBarActivity implements SensorEventListener, ActionBar.TabListener {
    private SensorManager sensorManager;
    private TextView x_axis, y_axis, z_axis;
    private boolean collect = true;
    public static PrintWriter ps;
    private ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        bar = getSupportActionBar();
        String type = getIntent().getStringExtra("Type");

        if (type.equalsIgnoreCase("internet")) {
          new InternetConnection(this);
        }

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.addTab(bar.newTab().setText("MousePad").setTabListener(this));
        bar.addTab(bar.newTab().setText("Motion Sensor").setTabListener(this));
        bar.addTab(bar.newTab().setText("Keyboard").setTabListener(this));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mouse, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
//            return;
//        }
        if (collect) {
            collect = false;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                        collect = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            int X = 0, Y = 1, Z = 2;
            x_axis = (TextView) findViewById(R.id.x_axis);
            y_axis = (TextView) findViewById(R.id.y_axis);
            z_axis = (TextView) findViewById(R.id.z_axis);
            //  float[] values = event.values;
            int[] val = new int[3];
            int maxPointTheory = 8000;
            int reallyPoints = 5000;

            for (int i = 0; i < val.length; i++) {
                if (i == Z) {
                    val[i] = Math.abs((int) (reallyPoints * event.values[i]));
                } else {
                    val[i] = Math.abs((int) (maxPointTheory * event.values[i]));
                }
                if (val[i] > reallyPoints && i != Z) {
                    val[i] = Math.abs(val[i] - (reallyPoints));
                }
            }


            x_axis.setText(Integer.toString(val[0]));
            y_axis.setText(Integer.toString(val[1]));
            z_axis.setText(Integer.toString(val[2]));
            //Log.e(Float.toString(values[0]), Float.toString(values[1]));
            sendInformation();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors

    }

    public void rightClickFunction(View v) {
        ps.println("RIGHT_CLICK");
    }

    public void leftClickFunction(View v) {
        ps.println("LEFT_CLICK");
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (ps != null) {
            ps.close();
            ps = null;
        }
        if (InternetConnection.returnSocket != null) {
            try {
                InternetConnection.returnSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InternetConnection.returnSocket = null;
        }
        startActivity(new Intent(MouseUIActivity.this, MainActivity.class));
    }

    private void sendInformation() {
        if (ps == null) {
            return;
        }
        ps.println("x:" + x_axis.getText().toString() + "@@" + "y:" + y_axis.getText().toString() + "@@" + "z:" + z_axis.getText().toString());


    }

    private FrameLayout fl;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        FrameLayout fragContainer = (FrameLayout) findViewById(R.id.app);
        fragContainer.removeAllViews();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (tab.getText().toString().equalsIgnoreCase("MousePad")) {


            FrameLayout ll = new FrameLayout(this);

            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageOneFragment(), "Mousepad").commit();
            fragContainer.addView(ll);


        } else if (tab.getText().toString().equalsIgnoreCase("Keyboard")) {
            FrameLayout ll = new FrameLayout(this);

            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageThreeFragment(), "Keyboard").commit();
            fragContainer.addView(ll);
        } else {

            FrameLayout ll = new FrameLayout(this);

            ll.setId(123456);
            getFragmentManager().beginTransaction().add(ll.getId(), new PaneTwoFragment(), "Motion sensor").commit();
            fragContainer.addView(ll);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void keyboardFunction(View v) {
        Button b = (Button) v;
        ps.println("keyboard:" + b.getText().toString());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
