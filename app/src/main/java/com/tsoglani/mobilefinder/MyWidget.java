package com.tsoglani.mobilefinder;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {
	@Override
	   public void onUpdate(Context context, AppWidgetManager appWidgetManager,
	   int[] appWidgetIds) {
		Log.e("runnnnsss ","runnnnnnssss");
	      for(int i=0; i<appWidgetIds.length; i++){

	          RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
	              R.layout.my_widget);

	          // Set the text
	   //       remoteViews.setTextViewText(R.id.update, String.valueOf(number));

	          // Register an onClickListener
	          Intent intent = new Intent(context, Menu.class);

	       //   intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
	        //  intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
	          PendingIntent pendingIntent = PendingIntent.getActivity(context,1, intent, 0);
	          remoteViews.setOnClickPendingIntent(R.id.button1, pendingIntent);
	          
	          
	          
	          appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
	      }
	   }	
}