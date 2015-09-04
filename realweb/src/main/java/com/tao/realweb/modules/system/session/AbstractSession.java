package com.tao.realweb.modules.system.session;

import java.util.Date;

import com.tao.realweb.bean.JID;

public abstract class AbstractSession implements Session {

	private JID jid;
	private Date createDate = new Date();
	private Date lastActiveDate = new Date();
	private boolean isValidate = false;
	private Status status = Status.offline;
	public AbstractSession(String jidStr){
		this.jid = JID.formatJID(jidStr);
	}
	public JID getAddress() {
		return jid;
	}
	public Date getCreationDate() {
		return this.createDate;
	}
	public Status getStatus() {
		return status;
	}
	public Date getLastActiveDate() {
		return lastActiveDate;
	}
	public boolean validate() {
		return this.isValidate;
	}
	public void setStatus(int code) {
		this.status = Status.fromCode(code);
		
	}
	public void setLastActiveDate(Date lastDate) {
		this.lastActiveDate = lastDate;
	}
	 public void setValidate(boolean isValidate){
		 this.isValidate =isValidate;
	 }
	
}
