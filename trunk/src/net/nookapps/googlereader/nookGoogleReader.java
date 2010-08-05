package net.nookapps.googlereader;

import java.io.File;
import java.util.Scanner;
import net.nookapps.NookHelper;
import net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper;
import net.nookapps.googlereader.googleAPI.ThreadedReader;
import net.nookapps.googlereader.googleAPI.webviews.EinkFeedView;
import net.nookapps.googlereader.googleAPI.webviews.TouchView;
import net.nookapps.googlereader.network.WifiNotifier;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class NookGoogleReader extends Activity
implements AbstractReaderMethodHelper, WifiNotifier {


	volatile String currentFeed;
	WebView touch,eink;
	public ThreadedReader reader;
	
	private NookHelper nookHelper;
	String user, pass;
	private boolean shouldLeave;
	
	
	
	private EinkFeedView feedView;
	private TouchView touchView;
	private ManagedThreadedReader managedReader;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.main);
			
			this.feedView = new EinkFeedView(this,(WebView) findViewById(R.id.einkview));
			this.touchView = new TouchView(this,(WebView) findViewById(R.id.touchview),feedView);
			this.nookHelper = new NookHelper(this,"gReader");
			
			nookHelper.setTitle("gReader");        
			nookHelper.resetTimeout();
			nookHelper.lockWifi(this);
			
			feedView.getWebView().addJavascriptInterface(new Reader(), "reader");
		
			try {
				Scanner input;

				input = new Scanner(new File("/system/media/sdcard/.settings/reader.settings"));
				user = input.nextLine();
				pass = input.nextLine();			

			} catch (Exception e) {
			
				feedView.loadText("An Exception has occured.  Make sure that the file<br> '.settings/reader.settings'"
						+ "contains your Google Reader<br> username on the first line, and the Google Reader <br>password on the second");
				
				shouldLeave = true;
				
				return;
			}			
			
		} catch (Exception e){
			feedView.loadText(e.toString());
		}
		
		
	}	
	
	@Override
	public void onUserInteraction(){
		super.onUserInteraction();
		nookHelper.resetTimeout();
	}
	
	
	private class Reader {
		@SuppressWarnings("unused")
		public String retrieveItems(){
			
			return currentFeed;
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
				feedView.changeReadStatus(true);
				reader.markAsRead(theOwner, theItemId);
			}		
		}

		@SuppressWarnings("unused")
		public void requestMoreItems(String continueString){
			reader.getFeedBasedOnLabel("nook", continueString);
			feedView.tryJavascript("log('c:"+ continueString + "')");
		}
	}
	
	public void stop(){
		nookHelper.unlockWifi();
		nookHelper.unlockScreenSaver();
		reader.stopThreads();
		this.finish();
	}

	@Override
	public void onLoginComplete() {
		feedView.tryJavascript("readee.onEvent('init');");
		
	}

	@Override
	public void onFeed(String feed) {
		this.feedView.onFeedDownloaded(feed,false);

	}

	@Override
	public void onTokenError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnreadCount(String json) {
			
	}

	@Override
	public void onSubscriptionList(String subscription) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onLabeled(String result) {
		feedView.tryJavascript("log('l:" + result + "');");
		
	}

	@Override
	public void onWifiEnabled() {
		if (shouldLeave){
			this.stop();
			return;
		}
		
		reader = new ThreadedReader(this,user,pass,false);
		managedReader = new ManagedThreadedReader(reader);
		this.updateReaderUsers();
		reader.login();		
	}

	/**
	 * 
	 */
	private void updateReaderUsers() {
		this.touchView.setManagedReader(this.managedReader);
		
	}
	
}