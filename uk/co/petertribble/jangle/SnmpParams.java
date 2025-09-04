/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package uk.co.petertribble.jangle;

/**
 * Holds details of an snmp server.
 *
 * @author Peter Tribble
 */
public class SnmpParams {

    /**
     * Represents the default host to connect to, localhost.
     */
    public static final String DEFAULT_HOST = "localhost";

    /**
     * Represents the default (snmpv1) community string, "public".
     */
    public static final String DEFAULT_COMMUNITY = "public";

    /**
     * Represents the default SNMP port, 161.
     */
    public static final int DEFAULT_PORT = 161;

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
    public SnmpParams(final String server) {
	this(server, DEFAULT_COMMUNITY);
    }

    /**
     * Create an SnmpParams object referencing the given server with the
     * given community string on the default port 161.
     *
     * @param server the name of the server
     * @param community the community string to use when accessing this server
     */
    public SnmpParams(final String server, final String community) {
	this(server, community, DEFAULT_PORT);
    }

    /**
     * Create an SnmpParams object referencing the given server with the
     * given community string on the given port.
     *
     * @param server the name of the server
     * @param community the community string to use when accessing this server
     * @param port the snmp port to connect to
     */
    public SnmpParams(final String server, final String community,
		      final int port) {
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
    public SnmpParams(final String server, final String community,
		      final int port, final int version) {
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
