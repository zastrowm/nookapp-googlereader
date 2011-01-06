/*******************************************************************************
 * 	Filename:	JavaToView.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Aug 20, 2010
 ********************************************************************************/
package net.nookapps.views;

/**
 * @author zastrowm
 *
 */
public interface JavaToView {
	
	abstract void onWebviewMessage(String name,String message);
}
