/**
 * $RCSfile$
 * $Revision: 3174 $
 * $Date: 2005-12-08 17:41:00 -0300 (Thu, 08 Dec 2005) $
 *
 * Copyright (C) 2005-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tao.realweb.modules.system.session;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Date;

import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Packet;

/**
 * The session represents a connection between the server and a client (c2s) or
 * another server (s2s) as well as a connection with a component. Authentication and
 * user accounts are associated with c2s connections while s2s has an optional authentication
 * association but no single user user.<p>
 *
 * Obtain object managers from the session in order to access server resources.
 *
 * @author Gaston Dombiak
 */
public interface Session extends Serializable {

    /**
     * Version of the XMPP spec supported as MAJOR_VERSION.MINOR_VERSION (e.g. 1.0).
     */
	public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 0;

    public static final int STATUS_CLOSED = -1;
    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_AUTHENTICATED = 3;

    /**
      * Obtain the address of the user. The address is used by services like the core
      * server packet router to determine if a packet should be sent to the handler.
      * Handlers that are working on behalf of the server should use the generic server
      * hostname address (e.g. server.com).
      *
      * @return the address of the packet handler.
      */
    public JID getAddress();

    /**
     * Obtain the current status of this session.
     *
     * @return The status code for this session
     */
    public Status getStatus();
    public void setStatus(int code);


    /**
     * Obtain the name of the server this session belongs to.
     *
     * @return the server name.
     */
    public String getServerName();
    
    /**
     * Obtain the date the session was created.
     *
     * @return the session's creation date.
     */
    public Date getCreationDate();

    /**
     * Obtain the time the session last had activity.
     *
     * @return The last time the session received activity.
     */
    public Date getLastActiveDate();
    public void setLastActiveDate(Date lastDate);

    
    /**
     * Close this session including associated socket connection. The order of
     * events for closing the session is:
     * <ul>
     *      <li>Set closing flag to prevent redundant shutdowns.
     *      <li>Call notifyEvent all listeners that the channel is shutting down.
     *      <li>Close the socket.
     * </ul>
     */
    public void close();

    /**
     * Returns true if the connection/session is closed.
     *
     * @return true if the connection is closed.
     */
    public boolean isClosed();


    /**
     * Returns the IP address string in textual presentation.
     *
     * @return  the raw IP address in a string format.
     * @throws java.net.UnknownHostException if IP address of host could not be determined.
     */
    public String getHostAddress() throws UnknownHostException;
    /**
     * Gets the host name for this IP address.
     *
     * <p>If this InetAddress was created with a host name,
     * this host name will be remembered and returned;
     * otherwise, a reverse name lookup will be performed
     * and the result will be returned based on the system
     * configured name lookup service. If a lookup of the name service
     * is required, call
     * {@link java.net.InetAddress#getCanonicalHostName() getCanonicalHostName}.
     *
     * <p>If there is a security manager, its
     * <code>checkConnect</code> method is first called
     * with the hostname and <code>-1</code>
     * as its arguments to see if the operation is allowed.
     * If the operation is not allowed, it will return
     * the textual representation of the IP address.
     *
     * @return  the host name for this IP address, or if the operation
     *    is not allowed by the security check, the textual
     *    representation of the IP address.
     * @throws java.net.UnknownHostException if IP address of host could not be determined.
     *
     * @see java.net.InetAddress#getCanonicalHostName
     * @see SecurityManager#checkConnect
     */
    public String getHostName() throws UnknownHostException;
 
    public boolean validate();
    public void setValidate(boolean isValidate);
    public void sendMessage(Packet packet);
    
    public static enum Status{
		online(1,"在线"),
		offline(2,"断线");
		private int code;
		private String desc;
		Status(int code,String desc){
			this.code = code;
			this.desc = desc;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public static Status fromCode(int code){
			for(Status s: Status.values()){
				if(s.getCode() == code){
					return s;
				}
			}
			return Status.offline;
		}
		
	}
}