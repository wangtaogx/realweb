/**
 * $RCSfile$
 * $Revision: 10865 $
 * $Date: 2008-11-04 00:28:57 +0800 (周二, 04 十一月 2008) $
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

public class PacketError implements Serializable{

	private static final long serialVersionUID = 1L;
	private String  status;
    private String reason;

	public static final PacketError HANDLER_NOT_FIND = new PacketError("7001","handler not found");
	public static final PacketError PACKET_FORMAT_ERROR = new PacketError("7002","PACKET_FORMAT_ERROR");
	public static final PacketError SERVER_EXCEPTION = new PacketError("7003","SERVER_EXCEPTION");
	public static final PacketError TOKEN_NOT_EXIT = new PacketError("7004","TOKEN_NOT_EXIT");
    public PacketError(){
    	
    }
    public PacketError(String status) {
        this(status,"");
    }
    public PacketError(String status, String reason) {
    	this.status = status;
    	this.reason = reason;
    }
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

}
