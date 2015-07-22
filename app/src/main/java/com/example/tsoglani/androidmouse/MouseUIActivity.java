package com.nikos.tsoglani.androidmouse;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.support.v7.app.ActionBar;
import android.widget.Toast;

public class MouseUIActivity extends ActionBarActivity implements SensorEventListener, ActionBar.TabListener {
    private SensorManager sensorManager;
    private TextView x_axis, y_axis, z_axis;
    private boolean collect = true;
    public static PrintWriter ps;
    public static DataInputStream bf;
    private ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nikos.tsoglani.androidmouse.R.layout.activity_mouse);
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
        getMenuInflater().inflate(com.nikos.tsoglani.androidmouse.R.menu.menu_mouse, menu);
        return true;
    }
  private  Thread receiveThread;
    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
//            return;
//        }
        if (collect) {
            collect = false;
            if(receiveThread.isAlive()){
                return;
            }
          receiveThread=   new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                        collect = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            thread.start();
            int X = 0, Y = 1, Z = 2;
            x_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.x_axis);
            y_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.y_axis);
            z_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.z_axis);
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
ps.flush();
    }

    public void leftClickFunction(View v) {
        ps.println("LEFT_CLICK");
        ps.flush();
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
        isReceivingImages = false;
        super.onBackPressed();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (ps != null) {
            ps.flush();
            ps.close();
            ps = null;
        }
        if (bf != null) {
            try {

                bf.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            bf = null;

        }
        if (InternetConnection.returnSocket != null) {
            try {
                InternetConnection.returnSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InternetConnection.returnSocket = null;
        }
        System.gc();
        startActivity(new Intent(MouseUIActivity.this, MainActivity.class));

    }

    private void sendInformation() {
        if (ps == null) {
            return;
        }

        ps.println("x:" + x_axis.getText().toString() + "@@" + "y:" + y_axis.getText().toString() + "@@" + "z:" + z_axis.getText().toString());


    }
private Thread thread;
    public void startReceivingImages(final Activity activity) {
        if (ps == null||bf==null||(thread!=null&&thread.isAlive())) {
            return;
        }
        final Bitmap[] bitmapimage = new Bitmap[1];

         thread = new Thread() {
            @Override
            public void run() {
                runOnUiThread(new Thread() {
                    @Override
                    public void run() {
                        Toast.makeText(MouseUIActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                    }
                });
                while (isReceivingImages) {
                    try {
                        ps.println("SCREENSHOT");
                        Thread.sleep(1000);
                        if (!isReceivingImages) {
                            break;
                        }
                        int bytesRead;
                        byte[] pic = new byte[5000 * 1024];
                        try {
                            bytesRead = bf.read(pic, 0, pic.length);

                            bitmapimage[0] = BitmapFactory.decodeByteArray(pic, 0, bytesRead);
                            final FrameLayout fr = (FrameLayout) activity.findViewById(com.nikos.tsoglani.androidmouse.R.id.mousepad);
                            final Drawable draw = new BitmapDrawable(activity.getResources(), bitmapimage[0]);
                            activity.runOnUiThread(new Thread() {
                                @Override
                                public void run() {


                                    fr.setBackgroundDrawable(draw);

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            startActivity(new Intent(MouseUIActivity.this, MainActivity.class
                            ));
                            return;
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.gc();
                }
            }
        };
        thread.start();
    }

    private FrameLayout fl;
    private static boolean isReceivingImages = true;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        FrameLayout fragContainer = (FrameLayout) findViewById(com.nikos.tsoglani.androidmouse.R.id.app);
        fragContainer.removeAllViews();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        if (tab.getText().toString().equalsIgnoreCase("MousePad")) {

            isReceivingImages = true;
            FrameLayout ll = new FrameLayout(this);

            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageOneFragment(), "Mousepad").commit();
            fragContainer.addView(ll);
            startReceivingImages(this);

        } else if (tab.getText().toString().equalsIgnoreCase("Keyboard")) {
            FrameLayout ll = new FrameLayout(this);
            isReceivingImages = false;
            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageThreeFragment(), "Keyboard").commit();
            fragContainer.addView(ll);
        } else {
            isReceivingImages = false;
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
ps.flush();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
