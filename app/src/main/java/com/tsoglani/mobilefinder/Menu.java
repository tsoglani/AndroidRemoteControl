package com.tsoglani.mobilefinder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends Activity {
	private static MediaPlayer mp;
	public static final int PORT = 2429;
	private Thread fst;
	private static ServerSocket serverSocket;
	public static final String RunAtStartup = "RunAtStartup";
	private DB dbHandler = new DB(this, null, null, 1);
	private boolean isLighOn = false;
	private Camera camera;
	private static boolean startEffects = false;
	String[] data;
	private static String waitforSignalString = "Waiting for signal to ring",
			infoString = "You are using MobileFinder";
	private static boolean isRestarted = false;

	// private boolean isActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		final ImageView imView = (ImageView) findViewById(R.id.image);
		final RadioButton enable = (RadioButton) findViewById(R.id.enable);
		final RadioButton disable = (RadioButton) findViewById(R.id.disable);
		final Button activateDevises = (Button) findViewById(R.id.activate_device_button);
		final Button properties = (Button) findViewById(R.id.properties);
		Button info = (Button) findViewById(R.id.info);
		Intent intent = getIntent();

		// isActive=enable.isChecked();
		data = dbHandler.getData();
		// for (int i = 0; i < 3; i++) {
		// if (data[i] == null) {
		// if (i == 0) {
		// data[i] = "true";
		// }else{
		// data[i] = "false";
		// }
		// }
		// }
		info.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(Menu.this);
				alert.setTitle("MobileFinder");
				alert.setMessage("Information");
				// Create TextView
				final TextView input = new TextView(Menu.this);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						});

				input.setHorizontallyScrolling(true);
				input.setVerticalScrollBarEnabled(true);
				input.setText("Find your Android device from a computer device (Download in your computer \n" +
						"this --> 'https://github.com/tsoglani/FindMobileServer/tree/master/store' <--Java File ) or another "
						+ "Android device ( search for Devices button ) via wifi.\n"
						+ "Requirements: The devices must share the SAME local network."
						+ "If one device search for other devices, every device that is enabled on that network will ring.\n"
						+ "You can find your phone, or do prank to a friend.\n"
						+ "Options you have : Start with the phone, enable flashlight, enable vibration."
						+ "\n\n\n  Developed by Tsoglani ");
				input.setSingleLine(false);
				alert.show();

			}
		});
		properties.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Menu.this, Properties.class);
				startActivity(intent);
			}
		});

		// ///

		/**
		 * activate button listener , sends to all possible local network
		 * clients
		 */
		activateDevises.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					if (issendToAllIpInNetworkWorks) {
						return;
					}

					runOnUiThread(new Thread() {
						public void run() {
							issendToAllIpInNetworkWorks = true;
							imView.setBackgroundResource(R.drawable.send);
							disable.setChecked(true);
							enable.setChecked(false);
							activateDevises.setEnabled(false);
							sendToAllIpInNetwork();
						}
					});

					new Thread() {
						@Override
						public void run() {
							try {
								
								Thread.sleep(2000);
								issendToAllIpInNetworkWorks = false;
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							runOnUiThread(new Thread() {
								public void run() {
									enable.setChecked(true);
									enable("Searching for devices");
									imView.setBackgroundResource(R.drawable.res);
								}
							});
						}
					}.start();
					activateDevises.setEnabled(true);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		/**
		 * enable checkbox listener
		 */
		enable.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// disable.setChecked(false);
					enable(waitforSignalString);
					runOnUiThread(new Thread() {
						public void run() {
							enable.setChecked(true);
							isWifiOpen();
							imView.setBackgroundResource(R.drawable.res);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		/**
		 * disable checkbox listener
		 */
		disable.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// enable.setChecked(false);
				// isActive = false;
				startEffects = false;
				isNotPlay_ressetMp();
				runOnUiThread(new Thread() {
					public void run() {
						imView.setBackgroundResource(Color.TRANSPARENT);
					}
				});

			}

		});

		try {
			String str = "";
			if (intent.hasExtra("Restart")) {
				str = intent.getStringExtra("Restart");
			}

			if (str != null && str.equalsIgnoreCase("True")) {
				isRestarted = true;
				enable(infoString);
				// for (int i = 0; i < 10; i++) {
				// try {
				// Thread.sleep(300);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// runOnUiThread(new Thread() {
				// @Override
				// public void run() {
				//
				// Intent dialogIntent = new Intent(getBaseContext(),
				// Menu.class);
				// dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// getApplication().startActivity(dialogIntent);
				// }
				// });
				// }

				new Thread() {
					public void run() {
						try {
							Thread.sleep(100);
							onBackPressed();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}.start();

			} else {
				enable("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			enable("");
		}

	}

	private void isWifiOpen(){
		
		
			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			}
		
	
		
	}
	/**
	 * add a toast text in screen
	 * 
	 * @param str
	 */
	private void addToast(final String str) {

		runOnUiThread(new Thread() {
			public void run() {
				Toast.makeText(Menu.this, str, Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * enable function , play sound from ServerThread
	 */
	private void enable(String message) {
		if (message != null && !message.equalsIgnoreCase("")) {
			addToast(message);
		}
		runOnUiThread(new Thread() {
			public void run() {
				try {
					// isActive = true;
					fst = new Thread(new ServerThread());
					fst.setDaemon(true);
					fst.start();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 * play sound function , plays from asset folder
	 * 
	 * @param fileName
	 */
	public void audioPlayer(final String fileName) {
		final RadioButton enable = (RadioButton) findViewById(R.id.enable);
		if (isNotPlay_ressetMp()) {
		} else {
			if (!enable.isChecked()) {
				startEffects = false;
				return;
			}
			new Thread() {
				public void run() {
					// try {
					// Thread.sleep(1500);
					// } catch (InterruptedException e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }

					for (int i = 0; i < 2; i++) {
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runOnUiThread(new Thread() {
							@Override
							public void run() {

								Intent dialogIntent = new Intent(
										getBaseContext(), Menu.class);
								dialogIntent
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
												| Intent.FLAG_ACTIVITY_SINGLE_TOP);
								getApplication().startActivity(dialogIntent);
							}
						});
					}

					runOnUiThread(new Thread() {// play music in this thread
						public void run() {
							try {
								AlertDialog.Builder alert;
								alert = new AlertDialog.Builder(Menu.this);
								alert.setIcon(android.R.drawable.ic_dialog_alert);
								alert.setTitle("FindMobile App");
								alert.setMessage("Stop sound ?");

								alert.setNegativeButton("Yes",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												isNotPlay_ressetMp();
												stopEffects();
											}

										});
								alert.setPositiveButton("No", null);

								isNotPlay_ressetMp();
								mp = new MediaPlayer();

								AssetFileDescriptor descriptor = getAssets()
										.openFd(fileName);
								mp.setDataSource(
										descriptor.getFileDescriptor(),
										descriptor.getStartOffset(),
										descriptor.getLength());
								descriptor.close();

								mp.prepare();
								mp.setVolume(1f, 1f);
								// mp.setLooping(true);
								mp.start();
								alert.show();

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}

			}.start();

		}
	}

	/**
	 * check if media player is not playing , if is not reset mp and mp=null
	 * 
	 * @return
	 */
	private boolean isNotPlay_ressetMp() {
		boolean outBool = mp != null;

		if (outBool) {

			mp.reset();
			mp.stop();
			mp.release();

			mp = null;
			System.gc();
		}
		return outBool;
	}

	/**
	 * put max volum on mobile
	 */
	private void maxVolum() {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}

	/**
	 * Server Thread , wait for interaction to continue
	 * 
	 * @author Nikos
	 * 
	 */
	public class ServerThread implements Runnable {

		final RadioButton enable = (RadioButton) findViewById(R.id.enable);

		public ServerThread() {
			Thread.currentThread().setDaemon(true);
		}

		public void run() {

			try {
				try {
					if (serverSocket != null) {
						isNotPlay_ressetMp();

						serverSocket.close();
						serverSocket = null;
					}
					serverSocket = new ServerSocket(PORT);

				} catch (Exception e) {

				}

				// SERVERIP = getLocalIpAddress();
				boolean isRunning = true;
				if (enable != null)
					isRunning = enable.isChecked();
				while (isRunning) {
					try {
						Log.e("isRunning", "IsRunnnnnnnnnninnnggg");
						serverSocket.accept();
						if (enable != null) {
							isRunning = enable.isChecked();
						}
						if (issendToAllIpInNetworkWorks) {

							continue;
						}

						startEffects = true;
						maxVolum();
						if (isRestarted) {
							isRestarted = false;
							for (int i = 0; i < 5; i++) {
								try {
									Thread.sleep(250);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								runOnUiThread(new Thread() {
									@Override
									public void run() {

										Intent dialogIntent = new Intent(
												getBaseContext(), Menu.class);
										dialogIntent
												.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
														| Intent.FLAG_ACTIVITY_SINGLE_TOP);
										getApplication().startActivity(
												dialogIntent);
									}
								});
							}

						}
						audioPlayer("pettie.mp3");
						lightTheScreen();
					} catch (Exception e) {
						if (serverSocket==null||serverSocket.isClosed()) {
							try {
								serverSocket = new ServerSocket(PORT);
							} catch (Exception exx) {
							}
						}
					}
				}

				serverSocket.close();
				serverSocket = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

	/**
	 * create connection to all possible networks with sub mask 255.255.255.0
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private boolean issendToAllIpInNetworkWorks = false;

	public synchronized void sendToAllIpInNetwork() {

		try {

			ArrayList<String> ipList = getLocal();

			for (String ip : ipList) {
				for (int i = 1; i < 255; i++) {
					final String checkIp = ip + i;
					new Thread() {
						public void run() {
							try {
								System.out.println(checkIp
										+ "  :  "
										+ InetAddress.getByName(checkIp)
												.isReachable(2500));

								Socket s = new Socket(checkIp, PORT);
								if (s.isConnected())
									Log.e("success   ", checkIp);
							} catch (Exception ex) {
								// Log.e(checkIp +
								// " is not asvailable","not available Menu class sendToAllIpInNetwork");
							}

						}
					}.start();
				}
			}
		} catch (Exception e) {
		}

	}

	/**
	 * get all local Ip's without the last bytes
	 * 
	 * @return
	 * @throws SocketException
	 */
	private static ArrayList<String> getLocal() throws Exception {
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		ArrayList<String> list = new ArrayList<String>();
		while (e.hasMoreElements()) {

			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {

				InetAddress inet = (InetAddress) ee.nextElement();
				if (!inet.isLinkLocalAddress()) {

					String hostAdd = inet.getHostAddress();
					System.out.println(hostAdd);
					String str = "";
					String[] ars = hostAdd.split("\\.");
					System.out.println("ars.length = " + ars.length);
					for (int j = 0; j < ars.length - 1; j++) {
						System.out.println(ars[j]);
						str += ars[j] + ".";
					}
					System.out.println("str = " + str);
					list.add(str);
				}
			}
		}
		return list;
	}

	/**
	 * light to the screen
	 */
	private void lightTheScreen() {

		final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Get an instance of the PowerManager
		PowerManager mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

		// Get an instance of the WindowManager
		WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay();

		// Create a bright wake lock
		final WakeLock mWakeLock = mPowerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

		runOnUiThread(new Thread() {
			public void run() {
				mWakeLock.acquire();

			}
		});

		new Thread() {
			public void run() {

				while (startEffects) {
					final int timeToSleep = 300;
					try {

						Thread.sleep(5 * timeToSleep);

						if (data[1] != null && data[1].equalsIgnoreCase("true")) {
							turnOnFlash();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Thread() {
						public void run() {
							if (data[2] != null
									&& data[2].equalsIgnoreCase("true")) {
								v.vibrate(timeToSleep);
							}
							if (data[1] != null
									&& data[1].equalsIgnoreCase("true")) {
								turnOffFlash();
							}
						}
					});
				}
				if (data[1] != null && data[1].equalsIgnoreCase("true")) {
					runOnUiThread(new Thread() {
						public void run() {
							// if (camera != null) {
							turnOffFlash();
							camera.release();
							camera = null;
							// }
						}
					});
				}

			}
		}.start();

	}

	private Parameters params = null;

	private void turnOnFlash() {
		runOnUiThread(new Thread() {
			public void run() {
				try {
					if (camera == null) {
						camera = Camera.open();

						params = camera.getParameters();
					}
					params.setFlashMode(Parameters.FLASH_MODE_TORCH);
					camera.setParameters(params);
					camera.startPreview();
					isLighOn = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	// Turning Off flash
	private void turnOffFlash() {
		runOnUiThread(new Thread() {
			public void run() {
				if (isLighOn) {
					try {
						if (camera == null) {
							camera = Camera.open();

							params = camera.getParameters();
						}
						params.setFlashMode(Parameters.FLASH_MODE_OFF);
						camera.setParameters(params);
						camera.stopPreview();
						isLighOn = false;

						// changing button/switch image
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});

	}

	private void stopEffects() {
		startEffects = false;
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);

	}

}
