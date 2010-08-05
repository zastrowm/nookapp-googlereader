/*******************************************************************************
 * 	Filename:	nook.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 25, 2010
 ********************************************************************************/
package net.nookapps;

import net.nookapps.googlereader.network.WifiNotifier;
import android.content.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * @author zastrowm
 * 
 */
public class NookHelper {
	public static final int keyUpRight = 98;
	public static final int keyDownRight = 97;
	public static final int keyUpLeft = 96;
	public static final int keyDownLeft = 95;
	private Context context;
	private WakeLock wakeLock;
	
	
	ConnectivityManager.WakeLock wifiLock;
	
	private final static int defaultSSTimeout = 300000;
	
	private long ssTimeout;
	private String name;
	
	public NookHelper(Context theContext){
		this(theContext,Integer.toString(theContext.hashCode()));
	}
	
	public NookHelper(Context theContext,String theName){
		this.context = theContext;
		
		this.wakeLock = ((PowerManager)context.getSystemService(Context.POWER_SERVICE))
			.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "net.nookapps");
		wakeLock.setReferenceCounted(false);
		
		this.ssTimeout = defaultSSTimeout;
		this.name = theName;
	}
	
	public void setTitle(String title){
		Intent msg = new Intent("com.bravo.intent.UPDATE_TITLE");		
        msg.putExtra("apptitle", title);
        context.sendBroadcast(msg);
	}
	
	public void setScreenSaverTimeout(long timeout){
		this.ssTimeout = timeout;
		resetTimeout();
	}
	
	public void resetTimeout(){
		wakeLock.acquire(this.ssTimeout);
	}
	
	private WifiManager getWifiManager(){
		return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE));
	}
	
	public void lockWifi(WifiNotifier notfiee){	
		
		ConnectivityManager cmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiLock = cmgr.newWakeLock(1, "net.nookapps.NookHelper-" + this.name);
		wifiLock.setReferenceCounted(false);
		wifiLock.acquire();		
		
		new WifiTask(notfiee).execute();			
	}
	
	
	public void unlockWifi(){
		if (wifiLock != null)
			while (wifiLock.isHeld())
				wifiLock.release();
	}
	
	
	
	class WifiTask extends AsyncTask<Void, Integer, Boolean> {

		WifiNotifier notifee;
		
		WifiTask(WifiNotifier it){
			this.notifee = it;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			ConnectivityManager cmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			boolean connection = (info == null) ? false : info.isConnected();
			int attempts = 1;
			while (!connection && attempts < 60) {
				try {
					Thread.sleep(1000);
				} catch (Exception ex) {

				}
				info = cmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				connection = (info == null) ? false : info.isConnected();
				attempts++;
			}
			
			
			return connection;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			notifee.onWifiEnabled();
		}
	}



	/**
	 * 
	 */
	public void unlockScreenSaver() {
		this.wakeLock.release();
		
	}
}
