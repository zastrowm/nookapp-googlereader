/*******************************************************************************
 * 	Filename:	ExtendedWebView.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps.views;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author zastrowm
 * Class used to facilitate interacting with the WebView
 */
public abstract class AbstractExtendedWebView{
	
	
	protected WebView web;

	/**
	 * Load the url for this webview
	 */
	public abstract void load();
	
	/**
	 * Hold a reference to the actual Webview
	 * @param it
	 */
	public AbstractExtendedWebView(WebView it){
		this.web = it;
		this.web.getSettings().setJavaScriptEnabled(true);
	}
	
	/**
	 * Execute Javascript
	 * @param js the javascript
	 */
	public void executeJavascript(String js){
		web.loadUrl("javascript:" + js);
	}
	
	/**
	 * Execute javascript that, if an exception occurs, replaces the body with error text
	 * @param js
	 */
	public void tryJavascript(String js){
		this.executeJavascript("try{"+js+";}catch(e){document.body.innerHTML = e}");
	}
	
	/**
	 * Load Text into the webview
	 * @param text
	 */
	public void loadText(String text){
		web.loadData("<html><body><p>" + text + "</p></body></html>", "text/html", "UTF-8");
	}
	
	/**
	 * Return the raw WebView
	 * @return the raw WebView
	 */
	public WebView getWebView(){
		return this.web;
	}
	
	
	/**
	 * If this is a webview that needs to capture key input, do that here.
	 * @param listener
	 */
	public void setOnKeyListener(View.OnKeyListener listener){
		this.web.setOnKeyListener(listener);
	}
	
	private static class EmptyViewListener extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			return true;
		}
	}
	
	static public EmptyViewListener emptyWebViewClient = new EmptyViewListener(); 
}
