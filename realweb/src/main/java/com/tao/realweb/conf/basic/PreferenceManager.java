package com.tao.realweb.conf.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferenceManager {

	private Map<String,Preference> preferencesMap = new ConcurrentHashMap<String, Preference>();
	private static Object lock = new Object();
	private static PreferenceManager instance = null;
	private PreferenceManager(){
		
	}
	
	public static PreferenceManager getInstance(){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new PreferenceManager();
				}
			}
		}
		return instance;
	}
	public void addPreference(Preference preference){
		this.preferencesMap.put(preference.getNamespace(), preference);
	}
	public void removePreference(Preference preference) {
		preferencesMap.remove(preference.getNamespace());
	}

	public Preference getPreference(String namespace) {
	    return preferencesMap.get(namespace);
	}
	
	public Iterator<Preference> getPreferences() {
        final List<Preference> returnList = new ArrayList<Preference>();
        for (String namespace : preferencesMap.keySet()) {
            returnList.add(preferencesMap.get(namespace));
        }
        return returnList.iterator();
    }
	
}
