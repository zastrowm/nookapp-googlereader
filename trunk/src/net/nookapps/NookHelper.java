/*******************************************************************************
 * 	Filename:	nook.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 25, 2010
 ********************************************************************************/
package net.nookapps;

import java.io.File;

import org.json.JSONObject;

import net.nookapps.network.WifiNotifier;
import android.app.AlertDialog;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * @author zastrowm
 * 
 */
public class NookHelper {
	public static final int keyUpRight = 98;
	public static final int keyDownRight = 97;
	public static final int keyUpLeft = 96;
	public static final int keyDownLeft = 95;
	
	public boolean isLeaving = false;
	
	private static NookHelper theHelper;
	private Context context;
	private WakeLock wakeLock;
	
	ConnectivityManager.WakeLock wifiLock;
	
	private final static int defaultSSTimeout = 300000;
	
	private long ssTimeout;
	private String name;
	private Handler handler;
	
	/**
	 * Create a new nook helper
	 * @param theContext the program context
	 * @param handler 
	 * @param theName the name of the app (used for locks)
	 */
	public NookHelper(Context theContext,Handler theHandler, String theName){
		this.context = theContext;
		this.handler = theHandler;
		this.wakeLock = ((PowerManager)context.getSystemService(Context.POWER_SERVICE))
			.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, this.name);
		wakeLock.setReferenceCounted(false);
		
		this.ssTimeout = defaultSSTimeout;
		this.name = "net.nookapps.NookHelper-" + theName;
		
		
		theHelper = this;
	}
	
	
	
	/**
	 * Set the title
	 * @param title the title
	 */
	public void setTitle(String title){
		Intent msg = new Intent("com.bravo.intent.UPDATE_TITLE");		
        msg.putExtra("apptitle", title);
        context.sendBroadcast(msg);
	}
	
	/**
	 * Lock the screen saver for at least this long
	 * @param timeout how long in milliseconds 
	 */
	public void setScreenSaverTimeout(long timeout){
		this.ssTimeout = timeout;
		resetScreenSaverTimeout();
	}
	
	/**
	 * Reset the Screen Save timeout for the amount of time designated before
	 */
	public void resetScreenSaverTimeout(){
		wakeLock.acquire(this.ssTimeout);
	}
	
	/**
	 * Lock the wifi so that it doesn't disappear
	 * @param notfiee a notifier to notify when Wifi has been locked
	 */
	public void lockWifi(WifiNotifier notfiee){	
		
		ConnectivityManager cmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiLock = cmgr.newWakeLock(1, this.name);
		wifiLock.setReferenceCounted(false);
		wifiLock.acquire();		
		
		log("starting WIFI task");
		new Thread(new WifiTask(notfiee)).start();
		//new WifiTask2(notfiee).execute();

	}
	
	/**
	 * Unlock the wifi if it's been locked
	 */
	public void unlockWifi(){
		if (wifiLock != null)
			while (wifiLock.isHeld())
				wifiLock.release();
	}
	
	/**
	 * Detect whether or not we're in the emulator
	 * @return true if we're in the emulator
	 */
	public static boolean isEmulator(){
		return false;
	}
	
	class WifiTask implements Runnable{

		
		WifiTask(WifiNotifier wifiNot2){
			wifiNotifier = wifiNot2;
		}
		
		private Handler handler;
		WifiNotifier wifiNotifier;

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			boolean isAirplaneModeEnabled = Settings.System.getInt(
				      context.getContentResolver(), 
				      Settings.System.AIRPLANE_MODE_ON, 0) == 1;
			boolean isWifiActive = false;
			
			
			if (!isAirplaneModeEnabled) {
				ConnectivityManager cmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				
				boolean connection = (info == null) ? false : info.isConnected();
				int attempts = 1;
				
				if (isEmulator()) attempts = 59;
				
				while (!connection && attempts < 60 && !isLeaving) {
					
					try {Thread.sleep(1000);} catch (Exception ex) {}
					
					
					info = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					connection = (info == null) ? false : info.isConnected();
					attempts++;
				}
				
				isWifiActive = connection;
			}
		
		
			if (isLeaving)	return;
			
			log("Wifi Lock Enabled " + isWifiActive);
			wifiNotifier.onWifiEnabled(isWifiActive);
		
		}
			
	}

	/**
	 * Unlock the screen saver if it's been locked
	 */
	public void unlockScreenSaver() {
		this.wakeLock.release();
		
	}
	
	public void log(String message){
		Log.i(this.name, message);
	}
	
	public void showKeyboard(){
		if (this.isLeaving)
			return;
		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	public void showKeyboard(final int delay){
		AsyncTask<Void, Integer, Void> showKeyboard = new AsyncTask<Void, Integer, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try{Thread.sleep(delay);}catch(Exception e){};
				showKeyboard();
				return null;
			}
			
		};
		
		showKeyboard.execute();
	}
	
	public static NookHelper helper(){
		return theHelper;
	}



	/**
	 * @param i
	 */
	public void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	/**
	 * @param string
	 * @param string2
	 * @param i
	 */
	public void messageBox(String title, String message, final int time, final Callback callback) {
		final AlertDialog messageBox = new AlertDialog.Builder(this.context)
		.setTitle(title)
		.setMessage(message)
		.create();
		
		messageBox.show();
		
		new AsyncTask<Void,Void,Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void e){
				messageBox.dismiss();
				callback.onCallback();
			}
			
			
		}.execute();
		
	}
	
	public static interface Callback{
		void onCallback();
	}
	
	
}
