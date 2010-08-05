/*******************************************************************************
 * 	Filename:	ThreadedGoogleAccessor.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 25, 2010
 ********************************************************************************/
package net.nookapps.googlereader.googleAPI;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zastrowm
 *
 */
public class ThreadedReader extends AbstractReader {

	public volatile Queue<Runnable> labeled = new LinkedBlockingQueue<Runnable>(),feeds = new LinkedBlockingQueue<Runnable>();
	public final Reader reader;
	
	private final AbstractReaderMethodHelper helper;
	private boolean isLoggedIn = false;
	private boolean stopAllThreads = false;
	
	private final Thread requestThread;
	
	/**
	 * @param user
	 * @param pass
	 * @param ascending
	 */
	public ThreadedReader(AbstractReaderMethodHelper help,String user, String pass, boolean ascending) {
		super(user, pass, ascending);
		reader = new Reader(this);
		helper = help;
		
		requestThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while (true){
					while (!feeds.isEmpty() || !labeled.isEmpty()){
						while (!feeds.isEmpty())
							feeds.poll().run();
						
						if (!labeled.isEmpty())
							labeled.poll().run();
					}
					

					try {Thread.sleep(1000);} catch (InterruptedException e) {}
					
					if (stopAllThreads){
						while (!labeled.isEmpty())
							labeled.poll().run();
						break;
					}
						
				}
			}			
		});
		
		this.requestThread.start();
	}
	
	public void stopThreads(){
		this.stopAllThreads = true;
		try {
			requestThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void finalize(){
		stopThreads();
	}
	

	/* (non-Javadoc)
	 * @see net.nookapps.googlereader.googleAPI.Reader#rawAddRemoveLabel(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	protected String rawAddRemoveLabel(final String owner,final String item,final String rawLabel,final boolean add) {
		labeled.add(new Runnable(){
			@Override
			public void run() {
				helper.onLabeled(ThreadedReader.this.reader.rawAddRemoveLabel(owner, item, rawLabel, add));
			}		
		});
		
		return null;
	}
	
	/**
	 * Login to Google Reader
	 */
	public void login(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.reader.login();
				ThreadedReader.this.isLoggedIn = true;
				ThreadedReader.this.helper.onLoginComplete();
			}
			
		}).start();
	}
	
	/**
	 * Get a feed based on a specific label
	 * @param label the label from which to get the feed
	 * @param continuation the continuation string, if any
	 * @return the feed in json format
	 */
	public void getFeedBasedOnLabel(final String label,final String continuation){
		feeds.add(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.helper.onFeed(
					ThreadedReader.this.reader.getFeedBasedOnLabel(label, continuation)
				);
			}
			
		});
	}
	
	public void getFeedBasedOnFeed(final String feed, final String continuation){
		feeds.add(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.helper.onFeed(
					ThreadedReader.this.reader.getFeedBasedOnFeed(feed, continuation)
				);
				
			}
			
		});
	}
	
	public void getAllFeeds(){
		feeds.add(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.helper.onFeed(
					ThreadedReader.this.reader.getAllFeeds()
				);
				
			}
			
		});
	}
	
	public void getUnreadCount(){
		feeds.add(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.helper.onUnreadCount(
					ThreadedReader.this.reader.getUnreadCount()
				);
				
			}
			
		});
	}
	
	public void getSubscriptionList(){
		feeds.add(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.helper.onSubscriptionList(
					ThreadedReader.this.reader.getSubscriptionList()
				);
				
			}
			
		});
	}
	
	
	public void refreshToken(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				ThreadedReader.this.reader.refreshToken();			
			}
			
		}).run();
	}
	
	
}
