package com.nikos.tsoglani.androidmouse;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by tsoglani on 20/7/2015.
 */
public class InternetConnection extends Service {

    public static final int port = 2000;
    static Socket returnSocket = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread() {
            @Override
            public void run() {
                try {
                    returnSocket = null;

                 sendToAllIpInNetwork();
                    if(!found){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No Wifi Network, or run the jar file on WLAN-Internet mode (read description) ",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    if (MouseUIActivity.ps == null) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No connection cause: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        startActivity(new Intent(InternetConnection.this, MainActivity.class));
                    }
                }
            }
        }.start();


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static boolean found = false;

    public boolean sendToAllIpInNetwork() throws UnknownHostException, IOException {
        found=false;
        ArrayList<String> ipList = getLocal();

        for (String ip : ipList) {
            if (returnSocket != null) {
                break;
            }
            for (int i = 1; i < 255; i++) {
                final String checkIp = ip + i;
                if (returnSocket != null) {
                    break;
                }
                new Thread() {
                    public void run() {
                        try {
                            //      System.out.println(checkIp + "  :  " + InetAddress.getByName(checkIp).isReachable(2000));

                            final Socket s = new Socket(checkIp, port);
                            found = true;
                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            br.readLine();
                            if (returnSocket == null && s != null) {
                                returnSocket = s;
                                MouseUIActivity.ps = new PrintWriter(new BufferedWriter(
                                        new OutputStreamWriter(returnSocket.getOutputStream())),
                                        true);
                                MouseUIActivity.ps.println("LOCAL_IP");
                                MouseUIActivity.bf = new DataInputStream(returnSocket.getInputStream());
                                Intent intent = new Intent(InternetConnection.this, MouseUIActivity.class);
                                intent.putExtra("Type", "WLAN");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Log.e("success   ", checkIp);
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), s.getInetAddress().toString(), Toast.LENGTH_LONG).show();

                                    }
                                });

                            }
                        } catch (IOException ex) {
                            //   System.out.println(checkIp + " is not available");

                        }

                    }
                }.start();
//                new AsyncTask<Void, Void, Void>() {
//
//                    private Socket s = null;
//
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Socket s = new Socket(checkIp, port);
//                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//
//                            br.readLine();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        if (s != null && returnSocket == null) {
//                            returnSocket = s;
//                            try {
//                                MouseUIActivity.ps = new PrintWriter(new BufferedWriter(
//                                        new OutputStreamWriter(returnSocket.getOutputStream())),
//                                        true);
//
//                                MouseUIActivity.bf = new DataInputStream(returnSocket.getInputStream());
//                                Intent intent = new Intent(context.getb, MouseUIActivity.class);
//                                intent.putExtra("Type", "Internet");
//                               InternetConnection.this.context.startActivity(intent);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }.execute();

                if(found){
                    break;
                }
            }
            if(found){
                break;
            }

        }
        return found;
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


}
