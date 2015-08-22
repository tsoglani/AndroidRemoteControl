package com.tsoglani.mobilefinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	
    	DB dbHandler = new DB(context, null, null, 1);
    	 
    	String s=dbHandler.getData()[0];
    	 if(s==null||s.equals("")){
    		 Log.e("dbHandler.getProduct()","is Null and now = true");
    		 s="true";
    	 }
    	 if(s.equalsIgnoreCase("true")){
    	if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){


            Intent i = new Intent(context, Menu.class);
            
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Restart","True");
            context.startActivity(i);
        }
    }}
}