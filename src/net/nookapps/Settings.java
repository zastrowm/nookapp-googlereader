/*******************************************************************************
 * 	Filename:	Scanner.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Sep 12, 2010
 ********************************************************************************/
package net.nookapps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author zastrowm
 *
 */
public class Settings {
	
	static final String DIRECTORY = "/system/media/sdcard/.settings/";
	private String directory;
	
	public Settings(String thePackage){
		this.directory = DIRECTORY + thePackage + "/";
		new File(this.directory).mkdirs();
	}
	
	private String resolve(String type){
		return this.directory + type + ".dat";
	}
	
	public Scanner open(String type){
		File file = new File(resolve(type));
		
		try {return new Scanner(file);}
		catch (FileNotFoundException e) {return null;}
	}
	
	public boolean save(String type,String data){
		boolean didSave = false;	
		try {
			FileWriter fw;
			fw = new FileWriter(new File(resolve(type)));
			fw.write(data);
			fw.close();
			didSave = true;
		} catch (IOException e) {}
		
		return didSave;		
	}
	
	public void delete(String type){
		new File(resolve(type)).delete();
	}
}
