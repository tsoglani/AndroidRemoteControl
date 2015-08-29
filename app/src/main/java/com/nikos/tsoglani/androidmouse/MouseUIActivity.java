package com.nikos.tsoglani.androidmouse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class MouseUIActivity extends ActionBarActivity implements SensorEventListener, ActionBar.TabListener {
    private SensorManager sensorManager;
    private TextView x_axis, y_axis, z_axis;
    private boolean collect = true;
    public static PrintWriter ps;
    public static DataInputStream bf;
    private ActionBar bar;


    ///<used for images>
    private Bitmap bitmapimage;
    private AsyncTask<Void, Void, Void> asTask;
    int width = -1, height = -1;
    private static Object lock = new Object();
    ///</used for images>
    private int sleepingTime = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.nikos.tsoglani.androidmouse.R.layout.activity_mouse);
        bar = getSupportActionBar();
        String type = getIntent().getStringExtra("Type");
        stopService(new Intent(this, InternetConnection.class));
        try {
            if (type.equalsIgnoreCase("bluetooth")) {
                ps.println("bluetooth");
                receivedImageX = 400;
                receivedImageY = 400;
                sleepingTime = 1300;
            } else if (type.equalsIgnoreCase("WLAN")) {
                receivedImageX = 1000;
                receivedImageY = 1000;
                sleepingTime = 600;
            } else if (type.equalsIgnoreCase("Internet")) {
                receivedImageX = 800;
                receivedImageY = 800;
                sleepingTime = 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//
//
//        else {
////            try {
////
////
////                            InternetConnection.returnSocket = new Socket(new URL("46.198.215.227").toString(), 2000);
////
////
////                if(InternetConnection.returnSocket==null){
////                    throw new Exception("Not internet connection Found");
////                }
////            } catch (Exception e) {
////                startActivity(new Intent(this, MainActivity.class));
////              //  e.printStackTrace();
////            }
//        }


        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.addTab(bar.newTab().setText("MousePad").setTabListener(this));
        bar.addTab(bar.newTab().setText("Keyboard").setTabListener(this));
        bar.addTab(bar.newTab().setText("Close PC").setTabListener(this));
        bar.addTab(bar.newTab().setText("Spy Camera").setTabListener(this));
        bar.addTab(bar.newTab().setText("Motion Sensor").setTabListener(this));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.nikos.tsoglani.androidmouse.R.menu.menu_mouse, menu);
        return true;
    }

    private Thread receiveThread;

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
//            return;
//        }

        if (collect) {
            collect = false;
            if (receiveThread != null && receiveThread.isAlive()) {
                return;
            }
            receiveThread = new Thread() {
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
            receiveThread.start();
            int X = 0, Y = 1, Z = 2;
            x_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.x_axis);
            y_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.y_axis);
            z_axis = (TextView) findViewById(com.nikos.tsoglani.androidmouse.R.id.z_axis);
            //  float[] values = event.values;
            int[] val = new int[3];
            int maxPointTheory = 10;
            //int reallyPoints = 1;

            for (int i = 0; i < val.length; i++) {
//                if (i == Z) {
//                    val[i] = Math.abs((int) (reallyPoints * event.values[i]));
//                } else {
//                    val[i] = Math.abs((int) (maxPointTheory * event.values[i]));
//                }
//                if (val[i] > reallyPoints && i != Z) {
//                    val[i] = Math.abs(val[i] - (reallyPoints));
//                }
                val[i] = ((maxPointTheory * Math.round(2 * event.values[i])));
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
    protected void onStop() {
        super.onStop();
        closeAll();
    }


    private void closeAll() {

        try {
            if (ps != null) {
                ps.println("Null");
                ps.flush();
                ps.close();
                ps = null;
            }
        } catch (Exception e) {
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
    }

    @Override
    public void onBackPressed() {
        isReceivingImages = false;
        super.onBackPressed();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        closeAll();
        startActivity(new Intent(MouseUIActivity.this, MainActivity.class));

    }

    private void sendInformation() {
        if (ps == null) {
            return;
        }

        ps.println("Motion:x:" + x_axis.getText().toString() + "@@" + "y:" + y_axis.getText().toString() + "@@" + "z:" + z_axis.getText().toString());


    }

    //private Thread thread;


    private int receivedImageX = 1000, receivedImageY = 1000;
    private boolean isRunning = false;
    private int sleepintTimeForScrenShot;

    public void startReceivingImages(final Activity activity, final boolean getComputerScreen) throws RuntimeException {
        try {
            if (bitmapimage != null) {
                bitmapimage.recycle();
            }
        } catch (Exception e) {
            Log.e("recycle on bitmapimage", "problem");
        }
        bitmapimage = null;
        if (ps == null || bf == null) {
            return;
        }
        final String sendCommand;
        if (getComputerScreen) {
            sendCommand = "SCREENSHOT";
            sleepintTimeForScrenShot = sleepingTime;
        } else {

            sendCommand = "CAM_SCREENSHOT";
            sleepintTimeForScrenShot = 700;
        }

        if (isRunning) {
            new Thread() {
                public void run() {
                    try {
                        isReceivingImages = false;
                        Thread.sleep(sleepintTimeForScrenShot + 1);
                        isReceivingImages = true;
                        startReceivingImages(activity, getComputerScreen);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        if (width == -1)
            width = getWindowManager().getDefaultDisplay().getWidth();
        if (height == -1) {
            height = getWindowManager().getDefaultDisplay().getHeight();
        }
        asTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                isRunning = true;
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {

                while (isReceivingImages) {
                    try {

                        Thread.sleep(sleepintTimeForScrenShot);
                        if (!isReceivingImages) {
                            break;
                        }
                        synchronized (lock) {
                            ps.println(sendCommand);
                            ps.flush();

                            int bytesRead = 0;

                            byte[] pic = new byte[receivedImageX * receivedImageX];
                            try {
                                bytesRead = bf.read(pic, 0, pic.length);
                                try {
                                    bitmapimage = BitmapFactory.decodeByteArray(pic, 0, bytesRead);

                                    if (getComputerScreen) {
                                        if (bitmapimage != null) {
                                            bitmapimage.prepareToDraw();
                                            // final Drawable draw = new BitmapDrawable(activity.getResources(), bitmapimage);
                                            final ImageView iv = (ImageView) activity.findViewById(R.id.mousepad_screen);
                                            activity.runOnUiThread(new Thread() {
                                                @Override
                                                public void run() {

                                                    try {
                                                        bitmapimage.prepareToDraw();
                                                        iv.setImageBitmap(bitmapimage);
                                                    } catch (final Exception e) {
                                                        e.printStackTrace();
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    } else {

                                        final ImageView iv = (ImageView) activity.findViewById(R.id.imageView);

                                        activity.runOnUiThread(new Thread() {
                                            @Override
                                            public void run() {
                                                if (bitmapimage != null && iv != null) {
                                                    bitmapimage.prepareToDraw();
                                                    iv.setImageBitmap(bitmapimage);
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception e) {


                                    throw new NullPointerException();
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                isReceivingImages = false;
                                startActivity(new Intent(MouseUIActivity.this, MainActivity.class));
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                isReceivingImages = false;
                                startActivity(new Intent(MouseUIActivity.this, MainActivity.class));
                            } catch (Exception e) {
                                e.printStackTrace();

                            } catch (OutOfMemoryError error) {
                                error.printStackTrace();
                                System.gc();

                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  System.gc();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                isRunning = false;
                if (!getComputerScreen) {
                    if (ps != null)
                        ps.println("STOP_CAM");
                }

                if (bitmapimage != null)
                    try {
                        bitmapimage.recycle();
                        bitmapimage = null;
                    } catch (Exception e) {
                        Log.e("recycle on bitmapimage", "problem");
                    }
            }
        };
        asTask.execute();
//        thread = new Thread() {
//            @Override
//            public void run() {
//
//            }
//        };
//        thread.start();
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
            try {
                isReceivingImages = true;
                FrameLayout ll = new FrameLayout(this);

                ll.setId(12345);
                getFragmentManager().beginTransaction().add(ll.getId(), new PageOneFragment(), "Mousepad").commit();
                fragContainer.addView(ll);
                startReceivingImages(this, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tab.getText().toString().equalsIgnoreCase("Keyboard")) {
            isReceivingImages = false;
            FrameLayout ll = new FrameLayout(this);
            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageThreeFragment(), "Keyboard").commit();
            fragContainer.addView(ll);
        } else if (tab.getText().toString().equalsIgnoreCase("Close PC")) {
            isReceivingImages = false;
            FrameLayout ll = new FrameLayout(this);
            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new PageFourFragment(), "Close PC").commit();
            fragContainer.addView(ll);
        } else if (tab.getText().toString().equalsIgnoreCase("Spy Camera")) {
            isReceivingImages = false;
            FrameLayout ll = new FrameLayout(this);

            ll.setId(12345);
            getFragmentManager().beginTransaction().add(ll.getId(), new WebCameraFragment(), "Spy Camera").commit();
            fragContainer.addView(ll);

            final ProgressDialog ringProgressDialog = ProgressDialog.show(MouseUIActivity.this, "Please wait ...", "Be sure that the computer has Camera ...", true);
            ringProgressDialog.setCancelable(true);
            ps.println("START_CAMERA");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(650);

                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                isReceivingImages = true;
                                startReceivingImages(MouseUIActivity.this, false);
                            }
                        });
                        // Here you should write your time consuming task...
                        // Let the progress ring for 10 seconds...
                        Thread.sleep(3000);

                    } catch (Exception e) {

                    }
                    ringProgressDialog.dismiss();
                }
            }).start();

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

        if (b.getText().toString().equalsIgnoreCase("send")) {
            TextView txtView = (TextView) findViewById(R.id.textScreen);

            ps.println("keyboard Word:" + txtView.getText().toString());
            ps.flush();
            txtView.setText("");
        } else {
            ps.println("keyboard:" + toUperCase(b.getText().toString()));
            ps.flush();
        }
    }

    private String toUperCase(String low) {
        String out = "";
        for (int i = 0; i < low.length(); i++) {
            out += Character.toUpperCase(low.charAt(i));
        }
        return out;
    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

}
