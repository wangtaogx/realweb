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

import com.fasterxml.jackson.annotation.JsonIgnoreType;


/**
 * The base IQ (Info/Query) packet. IQ packets are used to get and set information
 * on the server, including authentication, roster operations, and creating
 * accounts. Each IQ packet has a specific type that indicates what type of action
 * is being taken: "get", "set", "result", or "error".<p>
 *
 * IQ packets can contain a single child element that exists in a specific XML
 * namespace. The combination of the element name and namespace determines what
 * type of IQ packet it is. Some example IQ subpacket snippets:<ul>
 *
 *  <li>&lt;query xmlns="jabber:iq:auth"&gt; -- an authentication IQ.
 *  <li>&lt;query xmlns="jabber:iq:private"&gt; -- a private storage IQ.
 *  <li>&lt;pubsub xmlns="http://jabber.org/protocol/pubsub"&gt; -- a pubsub IQ.
 * </ul>
 *
 * @author Matt Tucker
 */
public class IQ extends Packet {

	public static final String IQ_NAMESPACE = "namespace";
	private String namespace;
    private Type type = Type.GET;
    public IQ() {
        super();
    }

    public IQ(IQ iq) {
        super(iq);
        type = iq.getType();
    }
    /**
     * Returns the type of the IQ packet.
     *
     * @return the type of the IQ packet.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the IQ packet.
     *
     * @param type the type of the IQ packet.
     */
    public void setType(Type type) {
        if (type == null) {
            this.type = Type.GET;
        }
        else {
            this.type = type;
        }
    }
    public void setNamespace(String namespace){
    	this.namespace = namespace;;
    }
    public String getNamespace(){
    	return this.namespace;
    }

    /**
     * Convenience method to create a new empty {@link Type#RESULT IQ.Type.RESULT}
     * IQ based on a {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET}
     * IQ. The new packet will be initialized with:<ul>
     *      <li>The sender set to the recipient of the originating IQ.
     *      <li>The recipient set to the sender of the originating IQ.
     *      <li>The type set to {@link Type#RESULT IQ.Type.RESULT}.
     *      <li>The id set to the id of the originating IQ.
     *      <li>No child element of the IQ element.
     * </ul>
     *
     * @param iq the {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET} IQ packet.
     * @throws IllegalArgumentException if the IQ packet does not have a type of
     *      {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET}.
     * @return a new {@link Type#RESULT IQ.Type.RESULT} IQ based on the originating IQ.
     */
    public static IQ createResultIQ(final IQ request) {
        if (!(request.getType() == Type.GET || request.getType() == Type.SET)) {
            throw new IllegalArgumentException(
                    "IQ must be of type 'set' or 'get'. Original IQ: " + request.toString());
        }
        final IQ result = new IQ() ;
        result.setType(Type.RESULT);
        result.setPacketID(request.getPacketID());
        result.setFrom(request.getTo());
        result.setTo(request.getFrom());
        return result;
    }

    /**
     * Convenience method to create a new {@link Type#ERROR IQ.Type.ERROR} IQ
     * based on a {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET}
     * IQ. The new packet will be initialized with:<ul>
     *      <li>The sender set to the recipient of the originating IQ.
     *      <li>The recipient set to the sender of the originating IQ.
     *      <li>The type set to {@link Type#ERROR IQ.Type.ERROR}.
     *      <li>The id set to the id of the originating IQ.
     *      <li>The child element contained in the associated originating IQ.
     *      <li>The provided {@link PacketError XMPPError}.
     * </ul>
     *
     * @param iq the {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET} IQ packet.
     * @param error the error to associate with the created IQ packet.
     * @throws IllegalArgumentException if the IQ packet does not have a type of
     *      {@link Type#GET IQ.Type.GET} or {@link Type#SET IQ.Type.SET}.
     * @return a new {@link Type#ERROR IQ.Type.ERROR} IQ based on the originating IQ.
     */
    public static IQ createErrorResponse(final IQ request, final PacketError error) {
        if (!(request.getType() == Type.GET || request.getType() == Type.SET)) {
            throw new IllegalArgumentException(
                    "IQ must be of type 'set' or 'get'. Original IQ: " + request.toString());
        }
        final IQ result = new IQ();
        result.setType(Type.ERROR);
        result.setPacketID(request.getPacketID());
        result.setFrom(request.getTo());
        result.setTo(request.getFrom());
        result.setError(error);
        return result;
    }

    /**
     * A class to represent the type of the IQ packet. The types are:
     *
     * <ul>
     *      <li>IQ.Type.GET
     *      <li>IQ.Type.SET
     *      <li>IQ.Type.RESULT
     *      <li>IQ.Type.ERROR
     * </ul>
     */
    @JsonIgnoreType
    public static enum Type{

        GET("get"),
        SET("set"),
        RESULT("result"),
        ERROR("error");

        /**
         * Converts a String into the corresponding types. Valid String values
         * that can be converted to types are: "get", "set", "result", and "error".
         *
         * @param type the String value to covert.
         * @return the corresponding Type.
         */
        public static Type fromString(String typeStr) {
           for(Type t : values()){
        	   if(t.toString().equals(typeStr)){
        		   return t;
        	   }
           }
           return ERROR;
        }

        private String value;

        private Type(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }
}
