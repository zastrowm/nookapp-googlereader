package net.nookapps.googlereader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import net.nookapps.NookHelper;
import net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper;
import net.nookapps.googlereader.googleAPI.ThreadedReader;
import net.nookapps.googlereader.network.WifiNotifier;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.view.View.OnKeyListener;

public class nookGoogleReader extends Activity
implements View.OnKeyListener, AbstractReaderMethodHelper, WifiNotifier, DialogInterface.OnDismissListener {
	/** Called when the activity is first created. */

	volatile String currentFeed;
	WebView touch,eink;
	ThreadedReader reader;
	
	private String theOwner, theItemId;
	private boolean itemIsRead, itemIsSaved;
	private NookHelper nookHelper;
	String user, pass, label;
	private boolean shouldLeave;
	
	private boolean loggedIn = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			
			setContentView(R.layout.main);
			this.setUpWebViews();		
			
			this.nookHelper = new NookHelper(this,"gReader");	
			nookHelper.setTitle("gReader");        
			nookHelper.resetTimeout();
			nookHelper.lockWifi(this);
			
			/*
			new Runnable(){

				@Override
				public void run() {
					onWifiEnabled();
					
				}
				
			}*/
			
	}
	
	
	@Override
	public void onUserInteraction(){
		super.onUserInteraction();
		nookHelper.resetTimeout();
	}
	
	
	
	/**
	 * Helper method, belongs in onCreate()
	 */
	private final void setUpWebViews(){
		touch = (WebView) findViewById(R.id.touchview);
		eink = (WebView) findViewById(R.id.einkview);		

		eink.getSettings().setJavaScriptEnabled(true);  
		touch.getSettings().setJavaScriptEnabled(true);
		
		eink.addJavascriptInterface(new Reader(), "reader");
		eink.loadUrl("file:///android_asset/feed.htm");
		touch.loadUrl("file:///android_asset/touch.htm");

		touch.setWebViewClient(new TouchViewListerner());
		eink.setOnKeyListener(this);	
	}

	/**
	 * Execute javascript in the Eink WebView
	 * @param javascript the javascript to execute
	 */
	private void executeEinkJS(String javascript){
		this.eink.loadUrl("javascript:" + javascript);
	}
	
	private void loadEinkText(String text){
		eink.loadData("<html><body><div style='white-space:pre'>" + text + "</div></body></html>", "text/html", "UTF-8");
	}
	
	/**
	 * Execute javascript and replace the view with the error if one occurs
	 * @param js
	 */
	private void tryJS(String js){
		executeEinkJS("try{"+js+"}catch(e){document.body.innerHTML = e + \" while trying to " +js+ "\"}");
	}

	
	private void scrollItem(boolean up){
		executeEinkJS("readee.onEvent('itemScrolled'," + up + ");");
	}
	
	private void changeItem(boolean next){
		executeEinkJS("readee.onEvent('itemSwitched'," + next + ");");
	}
	
	private void changeReadStatus(boolean status){
		executeEinkJS("readee.onEvent('itemRead'," + status + ");");
	}
	
	private void changeSaveStatus(boolean status){
		executeEinkJS("readee.onEvent('itemSave'," + status + ");");
	}
	
	private class Reader {
		@SuppressWarnings("unused")
		public String retrieveItems(){
			return nookGoogleReader.this.currentFeed;
		}
		
		@SuppressWarnings("unused")
		public void setInfo(String itemOwner,String itemId,String isRead,String isSaved){
			
			//tryJS("document.body.innerHTML='" +reader.reader.getToken() + "'");
			
			theOwner = itemOwner;
			theItemId = itemId;
			
			if (itemOwner == null || itemId == null)
				return;
			
			if (isRead != null)	itemIsRead = isRead.equals("true");			
			if (isSaved != null)itemIsSaved = isSaved.equals("true");	
			
			if (itemIsSaved){
				//changeSaveStatus(true);				
			} else {
				changeReadStatus(true);
				reader.markAsRead(theOwner, theItemId);
			}		
		}

		@SuppressWarnings("unused")
		public void requestMoreItems(String continueString){
			
			if (label == null)
				label = "nook";
			
			reader.getFeedBasedOnLabel(label, continueString);
			tryJS("log('c:"+ continueString + "')");
		}
	}
	
	public void stop(){
		nookHelper.unlockWifi();
		nookHelper.unlockScreenSaver();
		reader.stopThreads();
		this.finish();
	}
	
	private class TouchViewListerner extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			
			if (url.equals("reader://quit")){
				nookGoogleReader.this.stop();
			} else 	if (!loggedIn)
				return true;
			
			if (url.substring(0, 9).equals("reader://")){
				url = url.substring(9);
				
				if (url.equals("scrollUp"))
					scrollItem(true);
				else if (url.equals("scrollDown"))
					scrollItem(false);
				else if (url.equals("nextItem")){
					changeItem(true);
					if (theOwner == null) theOwner = "null";
				}
					
				else if (url.equals("prevItem"))
					changeItem(false);
				else if (url.equals("quit"))
					nookGoogleReader.this.stop();
				else if (url.equals("delete")){
					File file = new File("/system/media/sdcard/.settings/reader.settings");
					
					if (file.exists())
						file.delete();
					
					stop();
				}
				else if (url.equals("unread") && theOwner != null && theItemId != null){
					changeReadStatus(false);
					reader.markAsUnread(theOwner,theItemId);
				} else if (url.equals("readLater") && theOwner != null && theItemId != null){
					changeReadStatus(false);
					changeSaveStatus(true);
					reader.removeLabel(theOwner, theItemId, "nook");
					reader.addLabel(theOwner, theItemId, "nookRead");					
				}
					
				return true;
			} else return false;
		}
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		if (!loggedIn)
			return true;
		
		 if (event.getAction() == KeyEvent.ACTION_DOWN) {
	            switch (keyCode) {
	            case NookHelper.keyDownLeft:
	            	changeItem(true);
	            	
	            	break;
	            case NookHelper.keyUpRight: 
	            	scrollItem(true);
	            	break;
	            case NookHelper.keyUpLeft:
	            	changeItem(false);
	            	break;
	            case NookHelper.keyDownRight:
	            	scrollItem(false);
	                break;
	            default:
	                break;
	            }
	        }
	        return false;
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onLoginComplete()
	 */
	@Override
	public void onLoginComplete(boolean success) {
		
		if (success){
			try {
				
				new File("/system/media/sdcard/.settings").mkdir();
				
				PrintStream writer
					= new PrintStream(new FileOutputStream("/system/media/sdcard/.settings/reader.settings"));
				
				writer.println(this.user);
				writer.println(this.pass);
				writer.println(this.label);
				
				writer.close();
			} catch (FileNotFoundException e) {
				//if (!NookHelper.isEmulator())
				//	loadEinkText("<p>" + e + "</p>");
			}
			
			loggedIn = true;
			tryJS("readee.onEvent('init');");
		}else{
			loadEinkText("<p>I'm sorry, login was unsuccessful</p>");
			
			File file = new File("/system/media/sdcard/.settings/reader.settings");
			
			if (file.exists())
				file.delete();
		}
			//loadEinkText(this.user + "<br>" + this.pass + "<br>" + this.label);
		
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onFeed(java.lang.String)
	 */
	@Override
	public void onFeed(String feed) {
		this.currentFeed = feed;
		tryJS("readee.onEvent('feedDownloaded');");
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onTokenError()
	 */
	@Override
	public void onTokenError() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onUnreadCount(java.lang.String)
	 */
	@Override
	public void onUnreadCount(String json) {
			
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onSubscriptionList(java.lang.String)
	 */
	@Override
	public void onSubscriptionList(String subscription) {
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper#onLabeled(java.lang.String)
	 */
	@Override
	public void onLabeled(String result) {
		tryJS("log('" + result + "');");
		
	}


	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.network.WifiNotifier#onWifiEnabled()
	 */
	@Override
	public void onWifiEnabled() {
		if (shouldLeave) {
			this.stop();
			return;
		}
			
		if (NookHelper.isEmulator()){
			this.user = "greader.test@nookapps.net";
			this.pass = "d587sa98df";
			this.label ="nook";
			reader = new ThreadedReader(this,user,pass,false);
			reader.login();
			
			return;
		}		
		
		
		if (new File("/system/media/sdcard/.settings/reader.settings").exists() && readFile()){
			reader = new ThreadedReader(this,user,pass,false);
			reader.login();	
		} else {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doDialog();
		}	
	}
	
	public boolean readFile(){
		try {		
			Scanner input;

			input = new Scanner(new File("/system/media/sdcard/.settings/reader.settings"));
			user = input.nextLine();
			pass = input.nextLine();
			
			if (input.hasNextLine()){
				label = input.nextLine();
				
				if (label.trim().length() == 0)
					label = "nook";
			}
			
			return true;

		} catch (Exception e) {
			return false;
		}			
	}
	
	public void doDialog(){				
		LoginDialog loginData = new LoginDialog(this);
		loginData.setOnDismissListener(this);
		loginData.show();		
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnDismissListener#onDismiss(android.content.DialogInterface)
	 */
	@Override
	public void onDismiss(DialogInterface dialog2) {
		LoginDialog dialog = (LoginDialog)dialog2;
		
		if (dialog.username != null && dialog.password != null){
			
			this.user = dialog.username;
			this.pass = dialog.password;
			this.label = dialog.label;
			
			reader = new ThreadedReader(this,dialog.username,dialog.password,false);
			reader.login();
			
		
		} else {
			this.shouldLeave = true;
			this.stop();
		}
		
		
	}
	
}