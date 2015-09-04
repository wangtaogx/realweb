package com.tao.realweb.bean;

import com.tao.realweb.util.StringUtil;

/**
 * username@serverName/resource
 * @author Administrator
 *
 */
public class JID {
	private String username;
	private String serverName;
	private String resource;
	
	public JID(){
		
	}
	public JID(String username, String serverName, String resource) {
		this.username = username;
		this.serverName = serverName;
		this.resource = resource;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	/**
	 * 获取BareJID
	 * @param strJID
	 * @return
	 */
	public static String formatBareJID(String strJID){
		if(!StringUtil.isEmpty(strJID)){
			JID jid = formatJID(strJID);
			if(jid != null)
				return jid.toJIDString();
		}
		return null;
	}
	/**
	 * 获取用户名
	 * @param strJID
	 * @return
	 */
	public static String formatResource(String strJID){
		if(!StringUtil.isEmpty(strJID)){
			JID jid = formatJID(strJID);
			if(jid != null)
				return jid.getResource();
		}
		return null;
	}
	/**
	 * 获取用户名
	 * @param strJID
	 * @return
	 */
	public static String formatServerName(String strJID){
		if(!StringUtil.isEmpty(strJID)){
			JID jid = formatJID(strJID);
			if(jid != null)
				return jid.getServerName();
		}
		return null;
	}
	/**
	 * 获取用户名
	 * @param strJID
	 * @return
	 */
	public static String formatUsername(String strJID){
		if(!StringUtil.isEmpty(strJID)){
			JID jid = formatJID(strJID);
			if(jid != null)
				return jid.getUsername();
		}
		return null;
	}
	public static boolean isJID(String strJID){
		if(!StringUtil.isEmpty(strJID)){
			JID jid = formatJID(strJID);
			if(jid != null && !StringUtil.isEmpty(jid.getUsername()) && !StringUtil.isEmpty(jid.getServerName()) && !StringUtil.isEmpty(jid.getResource()))
				return true;
		}
		return false;
	}
	public static JID formatJID(String strJID){
		String username = null;
		String serverName = null;
		String resource = null;
		if(!StringUtil.isEmpty(strJID)){
			String r[] = strJID.split("@");
			if(r.length >= 2){
				username = r[0];
				String temp[] = r[1].split("/");
				if(temp.length == 1){
					serverName = temp[0];
					return new JID(username,serverName,"");
				}
				if(temp.length == 2){
					serverName = temp[0];
					resource = temp[1];
					return new JID(username,serverName,resource);
				}
			}
		}
		return null;
	}
	public  String toJIDString(){
		String jid ="";
		if(!StringUtil.isEmpty(username) && !StringUtil.isEmpty(serverName) )
			jid =username+"@"+serverName;
		if(!StringUtil.isEmpty(resource)){
			jid += "/"+resource;
		}
		return jid;
	}

}
