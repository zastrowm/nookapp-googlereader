
package net.nookapps.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
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
	
	public void httpGetToFile(String url,String filename){
		try {
			HttpGet httpget = new HttpGet(url);
			
			HttpResponse response = this.httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			convertStreamToFile(entity.getContent(),filename);
			
			if (entity != null) {
	            entity.consumeContent();
	        }		 
			
			return;
			
		} catch (IOException e) {
			return;
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
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8192);
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
	
	public static void convertStreamToFile(InputStream is,String filename){
		
		try {			
			OutputStream out = new FileOutputStream(new File(filename));
			byte buffer[] = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0)
				out.write(buffer, 0, length);
			out.close();
			is.close();
		} catch (IOException e) {}
		
	}
	
	
	protected void finalize() {
		if (this.httpclient != null)
			this.httpclient.getConnectionManager().shutdown();
		
		this.httpclient = null;
	}
	

}