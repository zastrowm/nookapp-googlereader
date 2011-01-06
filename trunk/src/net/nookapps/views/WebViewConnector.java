/*******************************************************************************
 * 	Filename:	WebViewConnector.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Aug 9, 2010
 ********************************************************************************/
package net.nookapps.views;


/**
 * @author zastrowm
 *
 */
public abstract class WebViewConnector {
	
	/**
	 * Connect two ExtendedWebViews and give them each a JS object to communicate to each other
	 * @param one the first ExtendedWebView
	 * @param name1 the name that two should use to communicate with one
	 * @param two the second ExtendedWebView
	 * @param name2 the name that one should use to communicate with two
	 */
	public static void connect(AbstractExtendedWebView one, String name1, AbstractExtendedWebView two,String name2){
		Connector a,b;
		a = new Connector(one,name2);
		b = new Connector(two,name1);
		a.connectTo(b);
		b.connectTo(a);
	}
	
	public static JavaToWebViewConnector connect(JavaToView javaProgram,String name1,AbstractExtendedWebView one,String name2){
		WebviewToJavaConnector w2jc = new WebviewToJavaConnector(one,name1,name2);
		JavaToWebViewConnector j2wc = new JavaToWebViewConnector(javaProgram);
		w2jc.connectTo(j2wc);
		j2wc.connectTo(w2jc);
		
		return j2wc;
	}
	
	private static class WebviewToJavaConnector{
		
		private AbstractExtendedWebView web;
		private String helperName;
		private JavaToWebViewConnector other;
		private boolean initialized;

		private WebviewToJavaConnector(AbstractExtendedWebView theWeb,String name, String name2){
			this.web = theWeb;
			this.helperName = "helper";
			this.web.getWebView().addJavascriptInterface(this, name);
		}
		
		private void connectTo(JavaToWebViewConnector it){
			this.other = it;
		}
		
		@SuppressWarnings("unused")
		public void sendMessage(String type,String message){
			other.onMessage(type,message);
		}
		
		@SuppressWarnings("unused")
		public void sendMessage(String type){
			sendMessage(type,null);
		}
		
		@SuppressWarnings("unused")
		public void init(String name){
			helperName = name;
			this.initialized = true;
		}
		
		void onMessage(String message){
			this.web.tryJavascript(this.helperName + "(\"" + message + "\")");
		}
	}
	
	public static class JavaToWebViewConnector{
		private JavaToView java;
		private WebviewToJavaConnector other;

		private JavaToWebViewConnector(JavaToView theJava){
			this.java = theJava;
		}
		
		private void connectTo(WebviewToJavaConnector it){
			this.other = it;
		}
		
		@SuppressWarnings("unused")
		public void sendMessage(String message){
			other.onMessage(message);
		}
		
		void onMessage(String type,String message){
			java.onWebviewMessage(type, message);
		}
	}
	
	
	
	/**
	 * Class to expose to javascript.  JS should only use sendString
	 * @author zastrowm
	 *
	 */
	private static class Connector{
		private AbstractExtendedWebView web;
		private String connectorName,helperName;
		private Connector that;

		/**
		 * Store the ExtendedWebView, and the name of the connector
		 * @param one the actual ExtendedWebView
		 * @param name the name to name it in JS
		 */
		private Connector(AbstractExtendedWebView one,String name){
			this.web = one;
			this.connectorName = name;
			
			one.getWebView().addJavascriptInterface(this, connectorName);
		}
		
		/**
		 * Connect this ExtendedWebView to another one
		 * @param it the ExtendedWebView to connect it to
		 */
		private void connectTo(Connector it){
			this.that = it;
		}
		
		/**
		 * Called from another ExtendedWebView when data is sent from that ExtendedWebView
		 * @param data
		 */
		private void onReceiveString(String data){
			this.web.tryJavascript(helperName + ".onData("+ data + ")");
			//this.web.loadText("onData("+ data + ")");
			
			//this.web.tryJavascript("onData("+ data + ");");
		}
		
		/**
		 * Called from JS when the page is ready.  Needs to be called before interacting with the 
		 * 	JS object
		 * @param name the name of the object that will used to access the advanced functions
		 * @param functionName the name of the function to call when new data is received
		 */
		@SuppressWarnings("unused")
		public void init(String name,String functionName){
			
			helperName = "window." + name;
			
			String execute = 
				helperName + " = {" 
				+"j2s: function(input){if (!input) return '\"\"';switch (input.constructor) {case String:return '\"' + input + '\"';case Number:	return input.toString();case Array:var buf = [];for (i in input) buf.push(this.j2s(input[i]));return '[' + buf.join(', ') + ']';case Object:var buf = []; for (k in input) buf.push(k + ' : ' + this.j2s(input[k]));		return '{ ' + buf.join(', ') + '} ';default:return 'null';}},"
				+"postRaw : function(value){"+connectorName+".sendString(value);},"
				+"postData : function(data){ this.postRaw(this.j2s(data));},"
				+"onData: function(data){return " + functionName + "(data);}"
				+"}"
			;
			
			web.executeJavascript(execute);
						
		}
		
		/**
		 * Called from JS (shouldn't be called by user, however).
		 * @param data the data to send to the other ExtendedWebView
		 */
		@SuppressWarnings("unused")
		public void sendString(String data){
			if (data == null)
				data = "{__ : \"null\"}";
			this.that.onReceiveString(data);
		}		
	}
}
