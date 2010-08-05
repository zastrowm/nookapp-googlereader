/*******************************************************************************
 * 	Filename:	EinkFeed.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps.googlereader.googleAPI.webviews;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import net.nookapps.ExtendedWebView;
import net.nookapps.NookHelper;
import net.nookapps.googlereader.ManagedThreadedReader;
import net.nookapps.googlereader.NookGoogleReader;



/**
 * @author zastrowm
 *
 */
public class EinkFeedView extends ExtendedWebView {

	private NookGoogleReader app;
	private String currentFeed;
	private ManagedThreadedReader reader;

	/**
	 * @param it the WebView that represents the WinkFeed
	 */
	public EinkFeedView(NookGoogleReader theApp,WebView it) {
		super(it);
		
		this.app = theApp;		
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/feed.htm");

	}
	
	public void scrollItem(boolean up){
		this.executeJavascript("readee.onEvent('itemScrolled'," + up + ");");
	}
	
	public void changeItem(boolean next){
		this.executeJavascript("readee.onEvent('itemSwitched'," + next + ");");
	}
	
	public void changeReadStatus(boolean status){
		this.executeJavascript("readee.onEvent('itemRead'," + status + ");");
	}
	
	public void changeSaveStatus(boolean status){
		this.executeJavascript("readee.onEvent('itemSave'," + status + ");");
	}

	/* (non-Javadoc)
	 * @see net.nookapps.ExtendedWebView#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
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
	
	public void onFeedDownloaded(String feed, boolean changed){
		this.currentFeed = feed;
		this.tryJavascript("readee.onEvent('feedDownloaded'" + changed +");");
	}
	
	private class JavascriptReaderObject{
		@SuppressWarnings("unused")
		public String retrieveItems(){
			
			return currentFeed;
		}
		
		@SuppressWarnings("unused")
		public void setInfo(String itemOwner,String itemId,boolean markedUnread,boolean isSaved){
			reader.setInfo(itemOwner, itemId, markedUnread, isSaved, "");
		}

		@SuppressWarnings("unused")
		public void requestMoreItems(String continueString){
			reader.getFeedBasedOnLabel("nook", continueString);
			feedView.tryJavascript("log('c:"+ continueString + "')");
		}
	}
	
	/**
	 * @param managedReader
	 */
	public void setManagedReader(ManagedThreadedReader managedReader) {
		this.reader = managedReader;
		
	}
	
	

}
