/**
 * $RCSfile$
 * $Revision: 1583 $
 * $Date: 2005-07-03 17:55:39 -0300 (Sun, 03 Jul 2005) $
 *
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
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

package com.tao.realweb.container;

import java.io.Serializable;
import java.util.Date;

/**
 * Implements the server info for a basic server. Optimization opportunities
 * in reusing this object the data is relatively static.
 *
 * @author Iain Shigeoka
 */
public class RealWebServerInfo implements Serializable {

    private Date startDate;
    private String domain;
    private String hostname;
    private String version;
    private String ip;
    private int weight;

    /**
     * Simple constructor
     *
     * @param xmppDomain the server's XMPP domain name (e.g. example.org).
     * @param hostname the server's host name (e.g. server1.example.org).
     * @param version the server's version number.
     * @param startDate the server's last start time (can be null indicating
     *      it hasn't been started).
     * @param connectionManager the object that keeps track of the active ports.
     */
    public RealWebServerInfo(String domain,String hostname, String version, Date startDate) {
        this.domain = domain;
        this.hostname = hostname;
        this.version = version;
        this.startDate = startDate;
    }

    public String getVersion() {
        return version;
    }

    public String getHostname()
	{
		return hostname;
	}

    public Date getLastStarted() {
        return startDate;
    }

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domainName) {
		this.domain = domainName;
		
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	public String getServerUsername(){
		return this.hostname;
	}
	public String getServerPassword(){
		return this.hostname;
	}
	
}