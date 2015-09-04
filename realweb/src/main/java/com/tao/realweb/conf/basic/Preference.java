package com.tao.realweb.conf.basic;

import java.net.URL;

public interface Preference {
	/**
	 * 命名空间，唯一性
	 * @return
	 */
	public String getNamespace();
	/**
	 * 获取参数值
	 * @return
	 */
	public String getString(String key);
	public String getStringDefault(String key,String defaultValue);
	public int getIntDefault(String key,int defaultValue);
	public double getDoubleDefault(String key,double defaultValue);
	public boolean getBooleanDefault(String key,boolean defaultValue);
	
	/**
	 * 标题
	 * @return
	 */
	public String getTitle();

    /**
     * Return the icon to use inside the Preferences list. The standard icon size
     * for preferences is 24x24.
     *
     * @return the icon to use inside the Preferences list.
     */
    public URL getIcon();

    /**
     * Return the tooltip to use for this preference. The tooltip is displayed
     * whenever a user places their mouse cursor over the icon.
     *
     * @return the tooltip to display.
     */
    public String getTooltip();

    /**
     * Return the title to use inside the Preferences list. The title is displayed below
     * and centered of the icon.
     *
     * @return the title to use inside the preferences list.
     */
    public String getListLinkName();
    
    /**
     * 设置系统参数
     * @param key
     * @param value
     */
    public void putString(String key,String value);
    public void putObject(String key,Object value);
}
