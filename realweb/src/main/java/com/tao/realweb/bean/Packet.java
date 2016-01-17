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

import com.alibaba.fastjson.JSONObject;
import com.tao.realweb.util.StringUtil;

public class Packet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ACTION_WRITE = "ACTION_WRITE";
	public static final String ACTION_READ = "ACTION_READ";
	private static String prefix = StringUtil.randomString(5) + "-";
	private static long id = 0;
	private String action;
	private String namespace;
	private String status = "0";
	private boolean isEncrpt = true;
	private JSONObject body = new JSONObject();
	private JSONObject header = new JSONObject();
	private String packetID = null;
	private String to = null;
	private String from = null;

	public Packet() {
	}
	public Packet(Packet p) {
        this(p.getAction(),p.getPacketID(),p.getTo(),p.getFrom(),p.getNamespace());
    }
    public Packet(String action,String packetID,String to,String from,String namespace){
    	this.action = action;
    	this.packetID = packetID;
    	this.from = from;
    	this.to = to;
    	this.namespace = namespace;
    }
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	public boolean isEncrpt() {
		return isEncrpt;
	}

	public void setEncrpt(boolean isEncrpt) {
		this.isEncrpt = isEncrpt;
	}

	public JSONObject getHeader() {
		return header;
	}
	public void setHeader(JSONObject header) {
		this.header = header;
	}
	public JSONObject getBody() {
		return body;
	}
	public void setBody(JSONObject body) {
		this.body = body;
	}
	public String getPacketID() {
		return packetID;
	}

	public void setPacketID(String packetID) {
		this.packetID = packetID;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
		;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public void putHeader(String key, String value) {
		this.header.put(key, value);
	}

	public String getHeader(String key) {
		return this.header.getString(key);
	}

	public static synchronized String nextID() {
		id = id++;
		if (id >= Long.MAX_VALUE)
			id = 0;
		return prefix + id;
	}

	public static Packet createResultPacket(final Packet request) {
		if (StringUtil.isEmpty(request.getAction())) {
			throw new IllegalArgumentException(
					"IQ must be of type 'set' or 'get'. Original IQ: "
							+ request.toString());
		}
		final Packet result = new Packet();
		result.setAction(Packet.ACTION_WRITE);
		result.setPacketID(request.getPacketID());
		result.setFrom(request.getTo());
		result.setTo(request.getFrom());
		result.setHeader(request.getHeader());
		return result;
	}

	public static Packet createErrorResponse(final Packet request,PacketError error) {
		final Packet result = new Packet();
		result.setAction(Packet.ACTION_WRITE);
		result.setPacketID(request.getPacketID());
		result.setFrom(request.getTo());
		result.setTo(request.getFrom());
		result.setStatus(error.getStatus());
		result.setHeader(request.getHeader());
		JSONObject body = new JSONObject();
		body.put("reason", error.getReason());
		result.setBody(body);
		return result;
	}
}
