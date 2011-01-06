package net.nookapps.gReader;
/*******************************************************************************
 * 	Filename:	FeedManager.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		gReader
 * 	Date:		Sep 27, 2010
 ********************************************************************************/


import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.nookapps.NookHelper;
import net.nookapps.network.NetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zastrowm
 *
 */
public class FeedManager {
	
	public static interface FeedManagerOnItem {
		void onFeedItemComplete(String jsonText);
		void onContinuationSting(String Continuation);
	}

	private FeedManagerOnItem notifiee;
	
	public FeedManager(String imgDirectory,FeedManagerOnItem theNotifiee){
		this.imgDirectory = imgDirectory;
		this.notifiee = theNotifiee;
		this.startThreads();
	}
	
	
	public static String readFile(String filename){
		try {
			Scanner scan = new Scanner(new File(filename));  
			scan.useDelimiter("\\Z");  
			return scan.next();
		} catch (Exception e) {
			return null;
		}  
	}
	
	private static final Pattern imgRegex = Pattern.compile("<img(.*?)src=\"(.*?)\"(.*?)\\/?>");;
	
	//private static final String imgDirectory = "/tmp/data/source/pict/";
	private final String imgDirectory;
	private static final int WAIT_TIME = 1000;
	
	
	private boolean exitThreads = false;
	
	volatile BlockingQueue<String> feedQueue = new LinkedBlockingQueue<String>();
	volatile BlockingQueue<JSONObject> itemQueue = new LinkedBlockingQueue<JSONObject>();
	volatile BlockingQueue<String> cookedItemQueue = new LinkedBlockingQueue<String>();
	
	private Thread fp;
	private Thread ip;
	
	public boolean addFeed(final String feed){
		try {
			boolean success = feedQueue.offer(feed);
			return success;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void startThreads(){
		fp = new FeedParser();
		ip = new ItemParser("a/");
		fp.start();
		ip.start();
	}

	class FeedParser extends Thread{
		@Override
		public void run(){
			String feed;
			while(true){
				try {feed = feedQueue.poll(WAIT_TIME, TimeUnit.MILLISECONDS);} catch (InterruptedException e1) {feed = null;}
				
				if (exitThreads)		break;
				else if (feed != null){
					
					try {						
						JSONObject json = new JSONObject(feed);
						
						if (json.has("continuation"))
							notifiee.onContinuationSting(json.getString("continuation"));
						
						JSONArray items = json.getJSONArray("items");
						
						for (int i = 0; i < items.length(); i++){
							
							try {itemQueue.offer(items.getJSONObject(i),WAIT_TIME, TimeUnit.MILLISECONDS);} catch (InterruptedException e) {System.err.println("error");}
											
						}
					} catch (JSONException e) {System.err.println("error:" + e);}
				}
				
			}
			
		}
	}

	
	class ItemParser extends Thread{
		
		public ItemParser(String theSourcePrefix){
			this.sourcePrefix = imgDirectory + theSourcePrefix;
		}
		
		private NetManager netManager = new NetManager();
		private String sourcePrefix;
		private int bigIndex = 0;
		
		@Override
		public void run(){
			JSONObject json;
			JSONObject content = null;
			String strContent;
			new File(this.sourcePrefix).delete();
			new File(this.sourcePrefix).mkdirs();
			while (true){
				
				
				json = null;
				//get an element
				try {json = itemQueue.poll(300, TimeUnit.MILLISECONDS);} catch (InterruptedException e1) {}
				//if timed out and we're supposed to exit, then exit
				if (exitThreads) break;
				else if (json != null){
					
					strContent = null;
					 
					try {
						if (json.has("summary")) {
							content = json.getJSONObject("summary");
							strContent = content.getString("content");
						} else if (json.has("content")){
							content = json.getJSONObject("content");
							strContent = content.getString("content");
						}
					} catch (Exception e) {
						strContent = null;
					}
					
					if (strContent != null){
						LinkedList<SourceReplacement> listOfSource = new LinkedList<SourceReplacement>();
						strContent = replace(strContent,listOfSource);
						
						for (SourceReplacement src : listOfSource){
							netManager.httpGetToFile(src.source,src.newSource);
						}
						try {
							content.put("content", strContent);
							//NookHelper.helper().log(json.toString());
							notifiee.onFeedItemComplete(json.toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
											
					
					}
					
				 }

		 
				content = null;
				
				
			}
			
			new File(this.sourcePrefix).delete();
		
		}
		
		public String replace(String content,LinkedList<SourceReplacement> list){
			int smallIndex = 0;
			Matcher matcher = imgRegex.matcher(content);
			StringBuffer sb = new StringBuffer();
			String prefix = sourcePrefix + (bigIndex++)  + "/";
			new File(prefix).mkdir();
			
			SourceReplacement sr;
			
			while (matcher.find()) {
				sr = new SourceReplacement(matcher.group(2),prefix+(smallIndex++));
				matcher.appendReplacement(sb, "<img" + matcher.group(1) + "src=\""+sr.newSource+"\"" + matcher.group(3) + "/>");
				list.add(sr);
			}
			matcher.appendTail(sb);
			
			if (bigIndex > 20){
				File numDir = new File(sourcePrefix + (bigIndex - 20) + "/");
				if (numDir.exists())	numDir.delete();
				
			}
			
			return sb.toString();
		}
		
	}
	
	public void exit(){
		this.exitThreads = true;				
		try {
			fp.join();
			ip.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.cleanup();	
	}
	
	public void cleanup(){
		new File(imgDirectory).delete();
	}
	
	class SourceReplacement{
		
		SourceReplacement(String theSource, String theNewSource){
			this.source = theSource;
			this.newSource = theNewSource;
		}
		
		String source;
		String newSource;
	}
}
