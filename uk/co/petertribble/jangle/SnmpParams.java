/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2013 Peter C. Tribble
 */

package uk.co.petertribble.jangle;

/**
 * Holds details of an snmp server.
 *
 * @author Peter Tribble
 */
public class SnmpParams {

    /**
     * Represents SNMP version 1.
     */
    public static final int SNMPV1 = 0;

    /**
     * Represents SNMP version 2.
     */
    public static final int SNMPV2 = 1;

    /**
     * Represents SNMP version 3.
     */
    public static final int SNMPV3 = 2;

    private String server;
    private String community;
    private int version;
    private int port;

    /**
     * Create an SnmpParams object referencing the given server with the
     * default community string "public" on the default port 161.
     *
     * @param server the name of the server
     */
    public SnmpParams(String server) {
	this(server, "public");
    }

    /**
     * Create an SnmpParams object referencing the given server with the
     * given community string on the default port 161.
     *
     * @param server the name of the server
     * @param community the community string to use when accessing this server
     */
    public SnmpParams(String server, String community) {
	this(server, community, 161);
    }

    /**
     * Create an SnmpParams object referencing the given server with the
     * given community string on the given port.
     *
     * @param server the name of the server
     * @param community the community string to use when accessing this server
     * @param port the snmp port to connect to
     */
    public SnmpParams(String server, String community, int port) {
	this(server, community, port, SNMPV1);
    }

    /**
     * Create an SnmpParams object referencing the given server with the
     * given community string.
     *
     * @param server the name of the server
     * @param community the community string to use when accessing this server
     * @param port the snmp port to connect to
     * @param version the SNMP version to use
     */
    public SnmpParams(String server, String community, int port, int version) {
	this.server = server;
	this.community = community;
	this.port = port;
	this.version = version;
    }

    /**
     * Return the name of the server referenced by this SnmpParams.
     *
     * @return the name of the server
     */
    public String getServer() {
	return server;
    }

    /**
     * Return the community string associated with the server referenced by
     * this SnmpParams.
     *
     * @return the community string
     */
    public String getCommunity() {
	return community;
    }

    /**
     * Return the SNMP port associated with the server referenced by
     * this SnmpParams.
     *
     * @return the SNMP port to connect to
     */
    public int getPort() {
	return port;
    }

    /**
     * Return the SNMP version associated with the server referenced by
     * this SnmpParams.
     *
     * @return the SNMP version
     */
    public int getVersion() {
	return version;
    }
}
