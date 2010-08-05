/*******************************************************************************
 * 	Filename:	Touch.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps.googlereader.googleAPI.webviews;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.nookapps.ExtendedWebView;
import net.nookapps.NookHelper;
import net.nookapps.googlereader.ManagedThreadedReader;
import net.nookapps.googlereader.NookGoogleReader;
import net.nookapps.googlereader.googleAPI.webviews.TouchView.EinkMode;

/**
 * @author zastrowm
 *
 */
public class TouchView extends ExtendedWebView implements View.OnKeyListener{

	private EinkFeedView feedView;
	private EinkMode mode = EinkMode.FeedView;
	private NookGoogleReader app;
	private ManagedThreadedReader reader;

	/**
	 * @param it
	 */
	public TouchView(NookGoogleReader theApp,WebView it,EinkFeedView feedView) {
		super(it);
		this.app = theApp;
		this.feedView = feedView;
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/touch.htm");
		feedView.setOnKeyListener(this);
		
		this.web.setWebViewClient(new TouchViewListerner());	
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		
		if (this.mode == EinkMode.FeedView)
			return feedView.onKey(v, keyCode, event);
		else
			return false;
	}

	public enum EinkMode {
		FeedView, ArticleView,NoView
	}
	
	
	private class TouchViewListerner extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			if (url.substring(0, 9).equals("reader://")){
				url = url.substring(9);
				
				if (url.equals("scrollUp"))
					feedView.scrollItem(true);
				else if (url.equals("scrollDown"))
					feedView.scrollItem(false);
				else if (url.equals("nextItem")){
					feedView.changeItem(true);
				}
					
				else if (url.equals("prevItem"))
					feedView.changeItem(false);
				else if (url.equals("quit"))
					app.stop();
				else if (url.equals("unread")){
					feedView.changeReadStatus(false);
					reader.markRead(false);
				} else if (url.equals("readLater")){
					feedView.changeReadStatus(false);
					feedView.changeSaveStatus(true);
					reader.readLater(true);					
				}
					
				return true;
			} else return false;
		}
	}


	/**
	 * @param managedReader
	 */
	public void setManagedReader(ManagedThreadedReader managedReader) {
		this.reader = managedReader;
		
	}
	
}
