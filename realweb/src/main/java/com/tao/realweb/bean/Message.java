/**
 * $RCSfile$
 * $Revision: 11825 $
 * $Date: 2010-08-15 23:45:57 +0800 (周日, 15 八月 2010) $
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents XMPP message packets. A message can be one of several types:
 *
 * <ul>
 *      <li>Message.Type.NORMAL -- (Default) a normal text message used in email like interface.
 *      <li>Message.Type.CHAT -- a typically short text message used in line-by-line chat interfaces.
 *      <li>Message.Type.GROUP_CHAT -- a chat message sent to a groupchat server for group chats.
 *      <li>Message.Type.HEADLINE -- a text message to be displayed in scrolling marquee displays.
 *      <li>Message.Type.ERROR -- indicates a messaging error.
 * </ul>
 *
 * For each message type, different message fields are typically used as follows:
 * <p>
 * <table border="1">
 * <tr><td>&nbsp;</td><td colspan="5"><b>Message type</b></td></tr>
 * <tr><td><i>Field</i></td><td><b>Normal</b></td><td><b>Chat</b></td><td><b>Group Chat</b></td><td><b>Headline</b></td><td><b>XMPPError</b></td></tr>
 * <tr><td><i>subject</i></td> <td>SHOULD</td><td>SHOULD NOT</td><td>SHOULD NOT</td><td>SHOULD NOT</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>thread</i></td>  <td>OPTIONAL</td><td>SHOULD</td><td>OPTIONAL</td><td>OPTIONAL</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>body</i></td>    <td>SHOULD</td><td>SHOULD</td><td>SHOULD</td><td>SHOULD</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>error</i></td>   <td>MUST NOT</td><td>MUST NOT</td><td>MUST NOT</td><td>MUST NOT</td><td>MUST</td></tr>
 * </table>
 *
 * @author Matt Tucker
 */
public class Message extends Packet {
	public static final String MESSAGE_BODY = "MESSAGE_BODY";
	public static final String MESSAGE_DATE = "MESSAGE_DATE";
	public static final String IMAGE_FILE_NAME = "IMAGE_FILE_NAME";
	public static final String VOICE_FILE_NAME = "VOICE_FILE_NAME";
	public static final String VIDEO_FILE_NAME = "VIDEO_FILE_NAME";

    private Type type = Type.normal;
    private String thread = null;

    /**
     * Creates a new, "normal" message.
     */
    public Message() {
    }

    /**
     * Creates a new "normal" message to the specified recipient.
     *
     * @param to the recipient of the message.
     */
    public Message(String to) {
        setTo(to);
    }

    /**
     * Creates a new message of the specified type to a recipient.
     *
     * @param to the user to send the message to.
     * @param type the message type.
     */
    public Message(String to, Type type) {
        setTo(to);
        this.type = type;
    }

    /**
     * Returns the type of the message. If no type has been set this method will return {@link
     * org.jivesoftware.smack.packet.Message.Type#normal}.
     *
     * @return the type of the message.
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type the type of the message.
     * @throws IllegalArgumentException if null is passed in as the type
     */
    public void setType(String type) {
        this.type = Type.fromString(type);
    }

    /**
     * Returns the body corresponding to the language. If the language is null, the method result
     * will be the same as {@link #getBody()}. Null will be returned if the language does not have
     * a corresponding body.
     *
     * @param language the language of the body to return.
     * @return the body related to the passed in language.
     * @since 3.0.2
     */
    @JsonIgnore
    public String getBody() {
    	String result = null;
        Object body = properties.get(Message.MESSAGE_BODY);
        if(body != null)
        	result = body.toString();
        return result;
    }
    


    /**
     * Sets the body of the message. The body is the main message contents.
     *
     * @param body the body of the message.
     */
    public void setBody(String body) {
    	if(body == null)
    		body = "";
        properties.put(Message.MESSAGE_BODY, body);
    }



    /**
     * Removes the body from the message and returns true if the body was removed.
     *
     * @param body the body being removed from the message.
     * @return true if the body was successfully removed and false if it was not.
     * @since 3.0.2
     */
    public void removeBody() {
        	properties.remove(Message.MESSAGE_BODY);
    }

    /**
     * Returns the thread id of the message, which is a unique identifier for a sequence
     * of "chat" messages. If no thread id is set, <tt>null</tt> will be returned.
     *
     * @return the thread id of the message, or <tt>null</tt> if it doesn't exist.
     */
    public String getThread() {
        return thread;
    }

    /**
     * Sets the thread id of the message, which is a unique identifier for a sequence
     * of "chat" messages.
     *
     * @param thread the thread id of the message.
     */
    public void setThread(String thread) {
        this.thread = thread;
    }



    public boolean equals(Object o) {
    	return super.equals(o);

    }
	 public String toString(){
	    	
	    	return "From:"+super.getFrom()+",to:"+super.getTo()+",body:"+getBody();
	    }


    /**
     * Represents the type of a message.
     */
    public enum Type {

        /**
         * (Default) a normal text message used in email like interface.
         */
        normal,

        /**
         * Typically short text message used in line-by-line chat interfaces.
         */
        chat,

        /**
         * Chat message sent to a groupchat server for group chats.
         */
        groupchat,

        /**
         * Text message to be displayed in scrolling marquee displays.
         */
        headline,

        /**
         * indicates a messaging error.
         */
        error;

        public static Type fromString(String name) {
            try {
                return Type.valueOf(name);
            }
            catch (Exception e) {
                return normal;
            }
        }

    }
}