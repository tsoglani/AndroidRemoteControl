package com.tsoglani.mobilefinder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Properties extends Activity {
	private DB dbHandler = new DB(this, null, null, 1);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_properties);

		final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
		Button back = (Button) findViewById(R.id.back);
		Button save = (Button) findViewById(R.id.save);
		final Switch flash = (Switch) findViewById(R.id.flash);
		final Switch vibrate = (Switch) findViewById(R.id.vibrate);

		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Thread() {
					public void run() {
						goHome();
					}
				});

			}
		});

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Thread() {
					public void run() {
						runOnUiThread(new Thread() {
							public void run() {
								Toast.makeText(Properties.this, "It will activated after application restart", Toast.LENGTH_LONG).show();
							}
						});
						dbHandler.addProduct(
								Boolean.toString(toggle.isChecked()),
								Boolean.toString(flash.isChecked()),
								Boolean.toString(vibrate.isChecked()));
						goHome();

					}
				});
			}
		});

		String[] pinax = dbHandler.getData();

		String RunAtStartDBValue = pinax[0];
		String flashDBValue = pinax[1];
		String vibrateDBValue = pinax[2];

		// //////////// RunAtStart get data fron db
		if (RunAtStartDBValue == null) {
			RunAtStartDBValue = Boolean.toString(true);
			Log.e("dbtxt = ", "null");
		}

		if (RunAtStartDBValue.equalsIgnoreCase("true")) {
			toggle.setChecked(true);
		} else {
			toggle.setChecked(false);
		}

		// //////////////flash get data fron db
		if (flashDBValue == null) {
			flashDBValue = Boolean.toString(false);
			Log.e("dbtxt = ", "null");
		}

		if (flashDBValue.equalsIgnoreCase("true")) {
			flash.setChecked(true);
		} else {
			flash.setChecked(false);
		}

		// ///////////vrbrate get data fron db
		if (vibrateDBValue == null) {
			vibrateDBValue = Boolean.toString(false);

		} else {
			Log.e("vibrateDBValue !!!!= ", "null");
		}

		if (vibrateDBValue.equalsIgnoreCase("true")) {
			vibrate.setChecked(true);

		} else {
			vibrate.setChecked(false);
		}

	}

	private void goHome() {
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.properties, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}
}
