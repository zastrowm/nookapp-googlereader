/*******************************************************************************
 * 	Filename:	ExtendedWebView.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
/**
 * @author zastrowm
 *
 */
public abstract class ExtendedWebView implements View.OnKeyListener{
	
	protected WebView web;

	public ExtendedWebView(WebView it){
		this.web = it;
	}
	
	public void executeJavascript(String js){
		web.loadUrl("javascript:" + js);
	}
	
	public void tryJavascript(String js){
		this.executeJavascript("try{"+js+"}catch(e){document.body.innerHTML = e + \" while trying to " +js+ "\"}");
	}
	
	public void loadText(String text){
		web.loadData("<html><body><p>" + text + "</p></body></html>", "text/html", "UTF-8");
	}
	
	public WebView getWebView(){
		return this.web;
	}
	
	public abstract boolean onKey(View v, int keyCode, KeyEvent event); 

	
	public void setOnKeyListener(View.OnKeyListener listener){
		this.web.setOnKeyListener(listener);
	}
}
