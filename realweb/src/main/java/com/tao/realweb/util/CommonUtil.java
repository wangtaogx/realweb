package com.tao.realweb.util;

import java.util.ArrayList;
import java.util.List;


public class CommonUtil<T extends Object> {

	
	public static boolean collectionIsEmpty(List<Object> list)
	{
		if(list != null && list.size() > 0)
			return true;
		return false;
	}
	public static boolean collectionIsNotEmpty(List<Object> list)
	{
		if(list == null || list.size() == 0)
			return true;
		return false;
	}
	public static<T> List<T> arrayToList(T[] objects)
	{
		ArrayList<T> list =new ArrayList<T>(); 
		if(objects!=null)
		{
			for(T o : objects)
			{
				list.add(o);
			}
		}
		return list;
	}
}
