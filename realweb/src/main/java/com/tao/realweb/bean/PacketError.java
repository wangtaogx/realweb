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
	private int code;
    private Condition condition;
    private String message;

	public static final PacketError p1 = new PacketError(500,Condition.interna_server_error,"interna-server-erro");
	public static final PacketError p2 =new PacketError(403,Condition.interna_server_error,"interna-server-erro");
	public static final PacketError p3 =new PacketError(400,Condition.bad_request,"interna-server-erro");
	public static final PacketError p4 =new PacketError(404,Condition.item_not_found,"interna-server-erro");
	public static final PacketError p5 =new PacketError(409,Condition.conflict,"interna-server-erro");
	public static final PacketError p6 =new PacketError(501,Condition.feature_not_implemented,"interna-server-erro");
	public static final PacketError p7 =new PacketError(302,Condition.gone,"interna-server-erro");
	public static final PacketError p8 =new PacketError(500,Condition.jid_malformed,"interna-server-erro");
	public static final PacketError p9 =new PacketError(405,Condition.not_allowed,"interna-server-erro");
	public static final PacketError p10 =new PacketError(500,Condition.no_acceptable,"interna-server-erro");
	public static final PacketError p11 =new PacketError(500,Condition.not_authorized,"interna-server-erro");
	public static final PacketError p12 =new PacketError(500,Condition.payment_required,"interna-server-erro");
	public static final PacketError p13 =new PacketError(500,Condition.recipient_unavailable,"interna-server-erro");
	public static final PacketError p14 =new PacketError(500,Condition.redirect,"interna-server-erro");
	public static final PacketError p16 =new PacketError(500,Condition.registration_required,"interna-server-erro");
	public static final PacketError p17 =new PacketError(500,Condition.remote_server_not_found,"interna-server-erro");
	public static final PacketError p18 =new PacketError(500,Condition.remote_server_timeout,"interna-server-erro");
	public static final PacketError p19 =new PacketError(500,Condition.remote_server_error,"interna-server-erro");
	public static final PacketError p20 =new PacketError(500,Condition.resource_constraint,"interna-server-erro");
	public static final PacketError p21 =new PacketError(500,Condition.service_unavailable,"interna-server-erro");
	public static final PacketError p22 =new PacketError(500,Condition.subscription_required,"interna-server-erro");
	public static final PacketError p23 =new PacketError(500,Condition.undefined_condition,"interna-server-erro");
	public static final PacketError p24 =new PacketError(500,Condition.unexpected_request,"interna-server-erro");
	public static final PacketError p25 =new PacketError(500,Condition.request_timeout,"interna-server-erro");

    public PacketError(){
    	
    }
    public PacketError(String messageText) {
        this(Condition.interna_server_error,messageText);
    }
    public PacketError(Condition condition) {
       this(condition,"");
    }

    public PacketError(Condition condition, String messageText) {
        this(-1,condition,messageText);
    }

    public PacketError(int code) {
        this(code,"");
    }

    public PacketError(int code, String message) {
       this(code,Condition.interna_server_error,message);
    }
    public PacketError(int code,  Condition condition, String message) {
        this.code = code;
        this.condition = condition;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    

    public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	/**
     * Returns the error as XML.
     *
     * @return the error as XML.
     */

    public String toString() {
        StringBuilder txt = new StringBuilder();
        if (condition != null) {
            txt.append(condition);
        }
        txt.append("(").append(code).append(")");
        if (message != null) {
            txt.append(" ").append(message);
        }
        return txt.toString();
    }


    public static enum Condition{

		interna_server_error("internal-server-error"),
        forbidden("forbidden"),
        bad_request("bad-request"),
        conflict("conflict"),
        feature_not_implemented("feature-not-implemented"),
        gone("gone"),
        item_not_found("item-not-found"),
        jid_malformed("jid-malformed"),
        no_acceptable("not-acceptable"),
        not_allowed("not-allowed"),
        not_authorized("not-authorized"),
        payment_required("payment-required"),
        recipient_unavailable("recipient-unavailable"),
        redirect("redirect"),
        registration_required("registration-required"),
        remote_server_error("remote-server-error"),
        remote_server_not_found("remote-server-not-found"),
        remote_server_timeout("remote-server-timeout"),
        resource_constraint("resource-constraint"),
        service_unavailable("service-unavailable"),
        subscription_required("subscription-required"),
        undefined_condition("undefined-condition"),
        unexpected_request("unexpected-request"),
        request_timeout("request-timeout");
        private String value;

         Condition(String value) {
            this.value = value;
        }
        
        public Condition fromString(String value){
        	for(Condition c : values()){
        		if(c.getValue().equals(value))
        			return c;
        	}
        	return interna_server_error;
        }

        public String getValue() {
            return value;
        }
    }

}
