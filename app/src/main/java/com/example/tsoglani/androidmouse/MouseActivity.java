package com.example.tsoglani.androidmouse;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.support.v7.app.ActionBar;
import android.widget.Toast;

public class MouseActivity extends ActionBarActivity implements SensorEventListener, ActionBar.TabListener {
    private SensorManager sensorManager;
    private TextView x_axis, y_axis, z_axis;
    private boolean collect = true;
    public static PrintWriter ps;
    public static final int port = 2000;
    private static Socket returnSocket = null;
    private ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mouse);
        bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.addTab(bar.newTab().setText("MousePad").setTabListener(this));
        bar.addTab(bar.newTab().setText("Motion Sensor").setTabListener(this));
        bar.addTab(bar.newTab().setText("Keyboard").setTabListener(this));

        try {
            returnSocket = null;
            Socket socket = sendToAllIpInNetwork();
            //  Socket socket = receiveSocket();;
            //  Log.e("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee","ooooooooooooooooooooooooooooooooooooooooooooooooo");
            ps = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            ;

        } catch (Exception e) {
            e.printStackTrace();
            if (ps == null) {
                Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MouseActivity.this, MainActivity.class));
            }
        }
        //   getWindow().setBackgroundDrawable(null);

    }


    public static Socket sendToAllIpInNetwork() throws UnknownHostException, IOException {

        ArrayList<String> ipList = getLocal();

        for (String ip : ipList) {
            for (int i = 1; i < 255; i++) {
                final String checkIp = ip + i;
                new Thread() {
                    public void run() {
                        try {
                            //      System.out.println(checkIp + "  :  " + InetAddress.getByName(checkIp).isReachable(2000));

                            Socket s = new Socket(checkIp, port);

                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            br.readLine();
                            returnSocket = s;

                            Log.e("success   ", checkIp);
                        } catch (IOException ex) {
                            //   System.out.println(checkIp + " is not available");
                        }

                    }
                }.start();
                if (returnSocket != null) {
                    break;
                }
            }
            if (returnSocket != null) {
                break;
            }

        }

        return returnSocket;
    }

    private static ArrayList<String> getLocal() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        ArrayList<String> list = new ArrayList<String>();
        while (e.hasMoreElements()) {

            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {

                InetAddress inet = (InetAddress) ee.nextElement();
                if (!inet.isLinkLocalAddress()) {

                    String hostAdd = inet.getHostAddress();
                    // System.out.println(hostAdd);
                    String str = "";
                    String[] ars = hostAdd.split("\\.");
                    //    System.out.println("ars.length = " + ars.length);
                    for (int j = 0; j < ars.length - 1; j++) {
                        //    System.out.println(ars[j]);
                        str += ars[j] + ".";
                    }
                    //  System.out.println("str = " + str);
                    list.add(str);
                }
            }
        }
        return list;
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
        if (returnSocket != null) {
            try {
                returnSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            returnSocket=null;
        }
        startActivity(new Intent(MouseActivity.this, MainActivity.class));
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
