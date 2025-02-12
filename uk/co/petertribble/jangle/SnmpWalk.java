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

import snmp.SNMPOctetString;

/**
 * Emulate snmpwalk. Only does SNMPv1 to localhost, public.
 *
 * @author Peter Tribble
 */
public final class SnmpWalk {

    private static SnmpMibManager smm;

    private static String community = "public";
    private static String host = "localhost";
    private static boolean debug;


    private SnmpWalk() {
    }

    /**
     * Walk the snmp tree.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
	boolean gothosts = false;
	int i = 0;
	while (i < args.length) {
	    if ("-c".equals(args[i])) {
		if (i + 1 < args.length) {
		    i++;
		    community = args[i];
		} else {
		    usage("missing argument to -c flag");
		}
	    } else if ("-d".equals(args[i])) {
		debug = true;
	    } else {
		if (gothosts) {
		    usage("can only specify a single host");
		}
		gothosts = true;
		host = args[i];
	    }
	    i++;
	}
	smm = SnmpMibManager.getInstance();
	doWalk();
    }

    private static void doWalk() {
	try {
	    SnmpParams snp = new SnmpParams(host, community);
	    SnmpController sc = new SnmpController(snp);
	    SnmpObject sno = sc.getNext("1.3.6.1.2.1");
	    while (sno != null) {
		printout(sno);
		sno = sc.getNext(sno);
	    }
	} catch (Exception e) {
	    System.err.println("Oops!");
	}
    }

    private static void printout(SnmpObject sno) {
	String type = sno.getTypeString();
	String oid = smm.prettifyOID(sno.toString());
	System.out.print(oid + " = " + type + ":");
	if ("OctetString".equals(type)) {
	    System.out.println(SnmpUtil.niceString(sno));
	    if (debug) {
		System.out.println("  (hex: "
		    + ((SNMPOctetString) sno.getValue()).toHexString() + ")");
	    }
	} else {
	    System.out.println(sno.getValue());
	}
    }

    private static void usage(String s) {
	System.err.println("Error: " + s);
	System.err.println(
	    "Usage: snmpwalk [-c community] host");
	System.exit(1);
    }
}
