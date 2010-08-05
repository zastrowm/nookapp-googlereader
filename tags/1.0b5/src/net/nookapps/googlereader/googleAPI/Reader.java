package net.nookapps.googlereader.googleAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import net.nookapps.googlereader.network.NetManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/*******************************************************************************
 * 	Filename:	GoogleAccessor.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		RssFeed
 * 	Date:		Jul 4, 2010
 ********************************************************************************/

/**
 * @author zastrowm
 *
 */
public class Reader extends AbstractReader{
	
	public Reader(String user,String pass,boolean ascending){
		super(user,pass,ascending);
	}
	
	public Reader(AbstractReader other){
		super(other);
	}
	
	/**
	 * Login to Google Reader
	 */
	public void login(){
		String content = network.httpGet("https://www.google.com/accounts/ServiceLoginAuth");

		String dsh = AbstractReader.getSurroundedString(content,"<input type=\"hidden\" name=\"dsh\"", "value=\"", "\"");
		String GALX = AbstractReader.getSurroundedString(content,"name=\"GALX\"", "value=\"", "\"");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("dsh", dsh));
		nvps.add(new BasicNameValuePair("GALX", GALX));
		nvps.add(new BasicNameValuePair("Email", this.username));
		nvps.add(new BasicNameValuePair("Passwd", this.password));
		nvps.add(new BasicNameValuePair("signin", "Sign+in"));
		nvps.add(new BasicNameValuePair("asts", ""));

		network.httpPost("https://www.google.com/accounts/ServiceLoginAuth",nvps);
		
		this.refreshToken();
	}

	/**
	 * Refresh the token for the Google API POST commands
	 * @return the new token
	 */
	public String refreshToken(){
		return this.currentToken = this.requestToken();
	}


	/**
	 * Get all feeds that are in the reading list
	 * @return the feeds in json format
	 */
	public String getAllFeeds(){
		return network.httpGet("http://www.google.com/reader/api/0/stream/contents/user/-/state/com.google/reading-list?output=json");
	}
	
	/**
	 * Get the unread count of all the feeds
	 * @return the unread count in json format
	 */
	public String getUnreadCount(){
		return network.httpGet("http://www.google.com/reader/api/0/unread-count?allcomments=false&output=json&ck=1255643091105&client=nookGoogleReader");
	}
	
	/**
	 * Get the subscription list
	 * @return the subscription list in josn format
	 */
	public String getSubscriptionList(){
		return network.httpGet("http://www.google.com/reader/api/0/subscription/list?output=json");
	}
	
	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.AbstractReader#rawAddRemoveLabel(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected String rawAddRemoveLabel(String feedOwner,String itemName,String label,boolean add){
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		
		if (add)
			nvps.add(new BasicNameValuePair("a", label));	//add raw tag
		else
			nvps.add(new BasicNameValuePair("r", label));	//remove raw tag
		
		nvps.add(new BasicNameValuePair("s", feedOwner));
		nvps.add(new BasicNameValuePair("i", itemName));
		nvps.add(new BasicNameValuePair("async", "true"));
		nvps.add(new BasicNameValuePair("T", this.currentToken));
		
		return network.httpPost("http://www.google.com/reader/api/0/edit-tag?client=nookGoogleReader", nvps);
	}

	
	
	/**
	 * Get a feed based on a specific label
	 * @param label the label from which to get the feed
	 * @param continuation the continuation string, if any
	 * @return the feed in json format
	 */
	public String getFeedBasedOnLabel(String label,String continuation){
		
		String url = "http://www.google.com/reader/api/0/stream/contents/user/-/label/" + label
			+ "?ck=" + new Date().getTime() + "&c=" + continuation + "&ot=0&r=o"
			+ "&n=15&client=nookGoogleReader&output=json&xt=user/-/state/com.google/read";
		
		return network.httpGet(url);
	}
	
	
	/**
	 * Get a specific feed 
	 * @param feed
	 * @param continuation
	 * @return
	 */
	public String getFeedBasedOnFeed(String feed,String continuation){
		
		String url = "http://www.google.com/reader/api/0/stream/contents/feed/"+feed
		+ "?ck=" + new Date().getTime() + "&c=" + continuation + "&ot=0&r=" + this.sortOrder
		+ "&n=15&&client=nookGoogleReader&output=json&xt=user/-/state/com.google/read";
				
		return network.httpGet(url);
	}
	
	
}
