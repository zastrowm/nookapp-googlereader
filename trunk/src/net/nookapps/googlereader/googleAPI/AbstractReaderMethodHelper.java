/*******************************************************************************
 * 	Filename:	ReaderMethodHelperInterface.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 25, 2010
 ********************************************************************************/
package net.nookapps.googlereader.googleAPI;

/**
 * @author zastrowm
 *
 */
public interface AbstractReaderMethodHelper {
	
	abstract void onLoginComplete();
	abstract void onFeed(String feed);
	abstract void onTokenError();
	abstract void onUnreadCount(String json);
	abstract void onSubscriptionList(String subscription);
	abstract void onLabeled(String result);
}
