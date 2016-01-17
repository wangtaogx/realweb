package com.tao.realweb.modules.system.session;

import java.util.Date;

import com.tao.realweb.bean.JID;

public abstract class AbstractSession implements Session {

	private JID jid;
	private Date createDate = new Date();
	private Date lastActiveDate = new Date();
	private String clientName;
	public AbstractSession(String jidStr){
		this.jid = JID.formatJID(jidStr);
	}
	public JID getAddress() {
		return jid;
	}
	public Date getCreationDate() {
		return this.createDate;
	}
	public Date getLastActiveDate() {
		return lastActiveDate;
	}
	public void setLastActiveDate(Date lastDate) {
		this.lastActiveDate = lastDate;
	}
	@Override
	public String getServerName() {
		return getAddress().getServerName();
	}
	@Override
	public String getClientName() {
		return this.clientName;
	}
	@Override
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
}
