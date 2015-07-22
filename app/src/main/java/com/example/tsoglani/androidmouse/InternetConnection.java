package com.nikos.tsoglani.androidmouse;

import android.app.Activity;
import android.content.Intent;
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
public class InternetConnection {

    public static final int port = 2000;
    static Socket returnSocket = null;

    public InternetConnection(Activity context) {
        try {
            returnSocket = null;

            Socket socket = sendToAllIpInNetwork();
            MouseUIActivity.ps = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
            MouseUIActivity.bf= new DataInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            if (MouseUIActivity.ps == null) {
                Toast.makeText(context.getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();

                context.startActivity(new Intent(context, MainActivity.class));
            }
        }
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
}
