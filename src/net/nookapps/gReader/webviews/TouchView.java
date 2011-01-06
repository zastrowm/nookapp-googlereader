/*******************************************************************************
 * 	Filename:	Touch.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 29, 2010
 ********************************************************************************/
package net.nookapps.gReader.webviews;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import net.nookapps.NookHelper;
import net.nookapps.gReader.GReader;
import net.nookapps.views.AbstractExtendedWebView;
import net.nookapps.views.JavaToView;
import net.nookapps.views.WebViewConnector;
import net.nookapps.views.WebViewConnector.JavaToWebViewConnector;

/**
 * @author zastrowm
 * 
 */
public class TouchView extends AbstractExtendedWebView implements View.OnKeyListener, JavaToView {

	private GReader app;
	public WebViewConnector.JavaToWebViewConnector j2wConnector;

	/**
	 * @param it
	 */
	public TouchView(GReader theApp, WebView it, EinkFeedView feedView) {
		super(it);

		this.app = theApp;

		feedView.setOnKeyListener(this);
		web.addJavascriptInterface(app.getReader(), "reader");
		WebViewConnector.connect((AbstractExtendedWebView) this, "connectorTouch", feedView, "connectorFeed");
		j2wConnector = WebViewConnector.connect((JavaToView) this, "java", this, "webview");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.nookapps.views.AbstractExtendedWebView#load()
	 */
	@Override
	public void load() {
		web.loadUrl("file:///android_asset/touch.htm");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case NookHelper.keyDownLeft:
				j2wConnector.sendMessage("keyLeftBottom");
				break;
			case NookHelper.keyUpLeft:
				j2wConnector.sendMessage("keyLeftTop");
				break;
			case NookHelper.keyUpRight:
				j2wConnector.sendMessage("keyRightTop");
				break;
			case NookHelper.keyDownRight:
				j2wConnector.sendMessage("keyRightBottom");
				break;

			default:
				break;
			}
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.nookapps.views.JavaToView#onWebviewMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public void onWebviewMessage(String name, String message) {
		if (name.equals("quit")){
			app.stop();
			NookHelper.helper().log("Quit message from touch received");
		} else if (name.equals("delete")){
			app.deleteLoginData();
			app.stop();
			NookHelper.helper().log("Quit/delete message from touch received");
		} else if (name.equals("log")){
			NookHelper.helper().log("Touch:" + message);
		}else {
			NookHelper.helper().log("From Touch: " + name + " " + message);
		}
	}
	
	public void init(){
		j2wConnector.sendMessage("init");
	}

}
