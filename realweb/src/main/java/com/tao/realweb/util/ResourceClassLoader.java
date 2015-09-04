package com.tao.realweb.util;

import java.io.File;
import java.net.URL;

public class ResourceClassLoader{

	private static ClassLoader loader = ClassLoader.getSystemClassLoader();
	
	public static File getResource(String fileName){
		try{
			URL url = loader.getResource(fileName); 
			if(url != null){
				File file = new File(url.getFile());
				if(file != null && file.exists()){
					return file;
				}
			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
