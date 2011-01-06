package net.nookapps.gReader;

import java.util.Scanner;

import net.nookapps.CustomDialog;
import net.nookapps.CustomDialog.OnDismissedDialogListener;
import net.nookapps.NookHelper;
import net.nookapps.NookHelper.Callback;
import net.nookapps.gReader.googleAPI.AbstractReaderMethodHelper;
import net.nookapps.gReader.googleAPI.ThreadedReader;
import net.nookapps.gReader.webviews.EinkFeedView;
import net.nookapps.gReader.webviews.TouchView;
import net.nookapps.network.WifiNotifier;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.webkit.WebView;

public class GReader extends Activity implements AbstractReaderMethodHelper,
		WifiNotifier, OnDismissedDialogListener, FeedManager.FeedManagerOnItem {

	public ThreadedReader reader;
	private EinkFeedView feedView;
	private Handler handler;

	private NookHelper nookHelper;

	private int onWifiOnUserInfoCount = 0;
	public net.nookapps.Settings settings;
	private TouchView touchView;
	private FeedManager feedManager;

	volatile String currentFeed;

	WebView touch, eink;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		handler = new Handler();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		this.nookHelper = new NookHelper(this, this.handler, "gReader");
		this.settings = new net.nookapps.Settings("net.nookapps.gReader");
		
		nookHelper.lockWifi(this);
		nookHelper.resetScreenSaverTimeout();
		
		feedManager = new FeedManager("/system/media/sdcard/my downloads/source/pict/",this);
		reader = new ThreadedReader(this);

		
		this.feedView = new EinkFeedView((WebView) findViewById(R.id.einkview));
		this.touchView = new TouchView(this, (WebView) findViewById(R.id.touchview), feedView);

		nookHelper.setTitle("gReader");
		//this.feedView.getWebView().loadUrl("file:///android_asset/test2.htm");
		//this.touchView.getWebView().loadUrl("file:///android_asset/test2.htm");
		touchView.load();
		feedView.load();

		loadUserInfo();
	}
	
	/*
	@Override
	public void onDestroy(){
		nookHelper.leaving = true;
		nookHelper.unlockWifi();
		nookHelper.unlockScreenSaver();
		reader.stopThreads();
		super.onDestroy();
	}*/
	
	
	/**
	 * Stop the program
	 */
	public void stop() {
		nookHelper.isLeaving = true;
		nookHelper.unlockWifi();
		nookHelper.unlockScreenSaver();
		feedManager.exit();
		reader.stopThreads();
		this.finish();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.nookapps.CustomDialog.OnDismissedDialogListener#onDismissedDialog(boolean)
	 */
	@Override
	public void onDismissedDialog(CustomDialog theDialog, boolean byCancel) {

		LoginDialog dialog = (LoginDialog) theDialog;

		if (byCancel) {
			this.stop();
			return;
		}

		if (dialog.username != null && dialog.password != null && dialog.label != null) {

			settings.save("login", dialog.username + "\n" + dialog.password);
			reader.setLoginCredentials(dialog.username, dialog.password);
			doWifiOnUserInfo();

		} else {
			this.stop();
		}

	}

	@Override
	public void onFeed(String feed) {
		this.feedManager.addFeed(feed);
	}

	@Override
	public void onLoginComplete(boolean success) {
		if (success)
			touchView.init();
		else {

			handler.post(new Runnable() {
				@Override
				public void run() {
					nookHelper.messageBox("Login Error", "Username/password error. Exiting now", 3000, new Callback() {
						@Override
						public void onCallback() {
							stop();
						}
					});
					deleteLoginData();
					
				}
			});
		}

	}

	

	@Override
	public void onSubscriptionList(String subscription) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUnreadCount(String json) {

	}

	/**
	 * When the user interacts with the tablet, reset the screen saver
	 */
	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
		nookHelper.resetScreenSaverTimeout();
	}

	@Override
	public void onWifiEnabled(boolean success) {
		if (nookHelper.isLeaving || !success) {

			nookHelper.messageBox("Wifi Error", "Wifi could not be enabled.  Exiting now.", 3000, new Callback() {
				@Override
				public void onCallback() {
					stop();
				}
			});

			return;
		}

		doWifiOnUserInfo();
	}


	/**
	 * Called from either loadUserInfo, or onWifiEnabled
	 */
	private void doWifiOnUserInfo() {
		if (onWifiOnUserInfoCount >= 1)
			reader.login();
		else
			onWifiOnUserInfoCount++;
	}
	

	/**
	 * @return the ThreadedReader Object
	 */
	public ThreadedReader getReader() {
		return this.reader;

	}

	

	/**
	 * Load the user information from the file, or prompt the user for their data
	 */
	private void loadUserInfo() {

		boolean isDialogNeeded = true;

		try {
			nookHelper.log("opening settings file");
			Scanner scanner = settings.open("login");
			reader.setLoginCredentials(scanner.nextLine(), scanner.nextLine());
			isDialogNeeded = false;
			nookHelper.log("settings loaded");
		} catch (Exception e) {
			nookHelper.log("failure to open file settings:" + e);
		}

		if (isDialogNeeded)		showLoginDialog(); // if file loading was unsuccessful
		else					doWifiOnUserInfo(); // else indicate that we're done loading

		// user = "greader.test@nookapps.net";
		// pass = "d587sa98df";
	}

	/**
	 * Show the login dialog and keyboard
	 */
	private void showLoginDialog() {
		LoginDialog loginData = new LoginDialog(this, this);
		loginData.show();
		
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				nookHelper.resetScreenSaverTimeout();
				nookHelper.showKeyboard();				
			}
			
		},500);
			
		
	}
	
	public void deleteLoginData(){
		settings.delete("login");
	}

	/* (non-Javadoc)
	 * @see net.nookapps.gReader.FeedManager.FeedManagerOnItem#onFeedItemComplete(java.lang.String)
	 */
	@Override
	public void onFeedItemComplete(final String jsonText) {
		
		handler.post(new Runnable(){
			@Override
			public void run() {		
				feedView.onFeedItemDownloaded(jsonText);
			}
		});
		
		
	}

	/* (non-Javadoc)
	 * @see net.nookapps.gReader.FeedManager.FeedManagerOnItem#onContinuationSting(java.lang.String)
	 */
	@Override
	public void onContinuationSting(final String Continuation) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				touchView.j2wConnector.sendMessage("continuation:" + Continuation);
			}
		});

	}

}