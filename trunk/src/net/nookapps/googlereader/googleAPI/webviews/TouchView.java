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
import net.nookapps.ExtendedWebView;
import net.nookapps.NookHelper;
import net.nookapps.googlereader.googleAPI.webviews.TouchView.EinkMode;

/**
 * @author zastrowm
 *
 */
public class TouchView extends ExtendedWebView implements View.OnKeyListener{

	private EinkFeedView feedView;
	private EinkMode mode = EinkMode.FeedView;

	/**
	 * @param it
	 */
	public TouchView(WebView it,EinkFeedView feedView) {
		super(it);
		this.feedView = feedView;
		web.getSettings().setJavaScriptEnabled(true);
		web.loadUrl("file:///android_asset/touch.htm");
		feedView.setOnKeyListener(this);	
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
	
}
