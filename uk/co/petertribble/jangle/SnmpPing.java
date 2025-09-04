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
 * Grab a single value.
 *
 * @author Peter Tribble
 */
public final class SnmpPing {

    private static String community = "public";
    private static String pingoid = "1.3.6.1.2.1.1.1.0";

    private SnmpPing() {
    }

    /**
     * Grab an SNMP value.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
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
	    } else if ("-o".equals(args[i])) {
		if (i + 1 < args.length) {
		    i++;
		    pingoid = args[i];
		} else {
		    usage("missing argument to -o flag");
		}
	    } else {
		gothosts = true;
		doPing(args[i]);
	    }
	    i++;
	}
	// if no hosts specified, run against localhost
	if (!gothosts) {
	    doPing("localhost");
	}
    }

    private static void doPing(final String host) {
	try {
	    SnmpParams snp = new SnmpParams(host, community);
	    SnmpController sc = new SnmpController(snp);
	    SnmpObject sno = sc.getValue(pingoid);
	    printout(host, sno);
	} catch (Exception e) {
	    System.err.println(host + ": Oops!");
	}
    }

    private static void printout(final String host, final SnmpObject sno) {
	String type = sno.getTypeString();
	System.out.print(host + ": " + sno.toString() + " = ");
	if ("OctetString".equals(type)) {
	    System.out.println(sno.valueString());
	} else {
	    System.out.println(sno.getValue());
	}
    }

    private static void usage(final String s) {
	System.err.println("Error: " + s);
	System.err.println(
	    "Usage: snmpping [-c community] [-o oid] host [ host ...]");
	System.exit(1);
    }
}
