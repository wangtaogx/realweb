/**
 * $RCSfile$
 * $Revision: 13558 $
 * $Date: 2013-03-18 16:40:35 +0800 (周一, 18 三月 2013) $
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
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

package com.tao.realweb.bean;


import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tao.realweb.util.StringUtil;

/**
 * Base class for XMPP packets. Every packet has a unique ID (which is automatically
 * generated, but can be overriden). Optionally, the "to" and "from" fields can be set,
 * as well as an arbitrary number of properties.
 *
 * Properties provide an easy mechanism for clients to share data. Each property has a
 * String name, and a value that is a Java primitive (int, long, float, double, boolean)
 * or any Serializable object (a Java object is Serializable when it implements the
 * Serializable interface).
 *
 * @author Matt Tucker
 */
public abstract class Packet implements Serializable{


    /**
     * Constant used as packetID to indicate that a packet has no id. To indicate that a packet
     * has no id set this constant as the packet's id. When the packet is asked for its id the
     * answer will be <tt>null</tt>.
     */
    public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";

    /**
     * A prefix helps to make sure that ID's are unique across mutliple instances.
     */
    private static String prefix = StringUtil.randomString(5) + "-";

    /**
     * Keeps track of the current increment, which is appended to the prefix to
     * forum a unique ID.
     */
    private static long id = 0;

    /**
     * Returns the next unique id. Each id made up of a short alphanumeric
     * prefix along with a unique numeric value.
     *
     * @return the next id.
     */
    public static synchronized String nextID() {
        return prefix + Long.toString(id++);
    }

    private String packetID = null;
    private String to = null;
    private String from = null;
    private String header;
    protected Map<String,String> properties = new LinkedHashMap<String, String>();
    private PacketError error;
    public Packet() {
    }

    public Packet(Packet p) {
        this(p.getHeader(),p.getPacketID(),p.getTo(),p.getFrom());
    }
    public Packet(String header,String packetID,String to,String from){
    	this.header = header;
    	this.packetID = packetID;
    	this.from = from;
    	this.to = to;
    }
    /**
     * Returns the unique ID of the packet. The returned value could be <tt>null</tt> when
     * ID_NOT_AVAILABLE was set as the packet's id.
     *
     * @return the packet's unique ID or <tt>null</tt> if the packet's id is not available.
     */
    public String getPacketID() {
        if (ID_NOT_AVAILABLE.equals(packetID)) {
            return null;
        }

        if (packetID == null) {
            packetID = nextID();
        }
        return packetID;
    }

    /**
     * Sets the unique ID of the packet. To indicate that a packet has no id
     * pass the constant ID_NOT_AVAILABLE as the packet's id value.
     *
     * @param packetID the unique ID for the packet.
     */
    public void setPacketID(String packetID) {
        this.packetID = packetID;
    }

    /**
     * Returns who the packet is being sent "to", or <tt>null</tt> if
     * the value is not set. The XMPP protocol often makes the "to"
     * attribute optional, so it does not always need to be set.<p>
     *
     * The StringUtils class provides several useful methods for dealing with
     * XMPP addresses such as parsing the
     * {@link StringUtil#parseBareAddress(String) bare address},
     * {@link StringUtil#parseName(String) user name},
     * {@link StringUtil#parseServer(String) server}, and
     * {@link StringUtil#parseResource(String) resource}.  
     *
     * @return who the packet is being sent to, or <tt>null</tt> if the
     *      value has not been set.
     */
    public String getTo() {
        return to;
    }

    /**
     * Sets who the packet is being sent "to". The XMPP protocol often makes
     * the "to" attribute optional, so it does not always need to be set.
     *
     * @param to who the packet is being sent to.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Returns who the packet is being sent "from" or <tt>null</tt> if
     * the value is not set. The XMPP protocol often makes the "from"
     * attribute optional, so it does not always need to be set.<p>
     *
     * The StringUtils class provides several useful methods for dealing with
     * XMPP addresses such as parsing the
     * {@link StringUtil#parseBareAddress(String) bare address},
     * {@link StringUtil#parseName(String) user name},
     * {@link StringUtil#parseServer(String) server}, and
     * {@link StringUtil#parseResource(String) resource}.  
     *
     * @return who the packet is being sent from, or <tt>null</tt> if the
     *      value has not been set.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets who the packet is being sent "from". The XMPP protocol often
     * makes the "from" attribute optional, so it does not always need to
     * be set.
     *
     * @param from who the packet is being sent to.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Returns the error associated with this packet, or <tt>null</tt> if there are
     * no errors.
     *
     * @return the error sub-packet or <tt>null</tt> if there isn't an error.
     */
    public PacketError getError() {
      
        return this.error;
    }

    /**
     * Sets the error for this packet.
     *
     * @param error the error to associate with this packet.
     */
    public void setError(PacketError error) {
    	this.error = error;
    }
    @JsonIgnore
    public String getProperty(String key){
    	return properties.get(key);
    }
    public void setProperty(String key,String value){
    	properties.put(key, value);
    }
	public Map<String, String> getProperties() {
		return properties;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public boolean equals(Object o) {
    	if(o instanceof Packet){
    		Packet p = (Packet)o;
    		if(p.getPacketID() == this.packetID)
    			return true;
    	}
    	return false;
    }

}
