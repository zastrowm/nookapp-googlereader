package net.nookapps.googlereader.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;


public class NetManager {

	private DefaultHttpClient httpclient;

	public NetManager(){
		this.httpclient = new DefaultHttpClient();
		this.httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
	}
	
	public String httpGet(String url){
	
		try {
			HttpGet httpget = new HttpGet(url);
			
			HttpResponse response = this.httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			String content = convertStreamToString(entity.getContent());
			 if (entity != null) {
		            entity.consumeContent();
		        }
			 
			 return content;
			
		} catch (IOException e) {
			return null;
		}
		
	}
	
	public List<Cookie> getCookies(){
		return this.httpclient.getCookieStore().getCookies();
	}
	
	public String httpPost(String url,List <NameValuePair> params){

		try {
			HttpPost httpost = new HttpPost(url);

			httpost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			String content = convertStreamToString(entity.getContent());

		
			
			if (entity != null) {
				entity.consumeContent();
			}

			return content;

		} catch (Exception e) {
			return null;
		}
	}
	
	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	protected void finalize() {
		if (this.httpclient != null)
			this.httpclient.getConnectionManager().shutdown();
		
		this.httpclient = null;
	}
	

}