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



/**
 * @author zastrowm
 *
 */
public class EinkFeedView extends ExtendedWebView {

	/**
	 * @param it the WebView that represents the WinkFeed
	 */
	public EinkFeedView(WebView it) {
		super(it);
		
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
	
	

}
