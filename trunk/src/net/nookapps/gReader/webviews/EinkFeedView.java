/*******************************************************************************
 * 	Filename:	EinkFeed.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps.gReader.webviews;

import android.webkit.WebView;
import net.nookapps.NookHelper;
import net.nookapps.views.AbstractExtendedWebView;


/**
 * @author zastrowm
 *
 */
public class EinkFeedView extends AbstractExtendedWebView {

	/**
	 * @param it the WebView that represents the WinkFeed
	 */
	public EinkFeedView(WebView it) {
		super(it);
		
		web.setWebViewClient(AbstractExtendedWebView.emptyWebViewClient);
		
	}
	
	public void load(){
		web.loadUrl("file:///android_asset/feed.htm");
	}

	public void onFeedItemDownloaded(String item){
		this.tryJavascript("readee.onItemDownloaded("+item+");");
	}


}
