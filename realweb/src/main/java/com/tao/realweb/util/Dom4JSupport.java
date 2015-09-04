package com.tao.realweb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Dom4JSupport {

	/**
	 * @Parmeter XML文件路径
	 * @Return 将文档持久化到内存
	 */
	public static Document parse(String xmlPath)  {
		try{
			InputStream in=new FileInputStream(xmlPath);
	        SAXReader reader = new SAXReader();
	        Document document = reader.read(in);
	        return document;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
    }
	/**
	 * 文件写入
	 * @param document
	 * @param path
	 * @throws Exception
	 */
	public static void writeXML(Document document,String path)
	{
		try{
			OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
			xmlFormat.setEncoding("UTF-8");
	        XMLWriter writer = new XMLWriter( new FileOutputStream( path ),xmlFormat);
	        writer.write( document );
	        writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
