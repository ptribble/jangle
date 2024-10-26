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
 * Copyright 2013-2024 Peter C. Tribble
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
    public static void main(String[] args) {
	boolean gothosts = false;
	int i = 0;
	while (i < args.length) {
	    if ("-c".equals(args[i])) {
		if (i+1 < args.length) {
		    i++;
		    community = args[i];
		} else {
		    usage("missing argument to -c flag");
		}
	    } else if ("-o".equals(args[i])) {
		if (i+1 < args.length) {
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

    private static void doPing(String host) {
	try {
	    SnmpParams snp = new SnmpParams(host, community);
	    SnmpController sc = new SnmpController(snp);
	    SnmpObject sno = sc.getValue(pingoid);
	    printout(host, sno);
	} catch (Exception e) {
	    System.err.println(host + ": Oops!");
	}
    }

    private static void printout(String host, SnmpObject sno) {
	String type = sno.getTypeString();
	System.out.print(host + ": " + sno.toString() + " = ");
	if ("OctetString".equals(type)) {
	    System.out.println(sno.valueString());
	} else {
	    System.out.println(sno.getValue());
	}
    }

    private static void usage(String s) {
	System.err.println("Error: " + s);
	System.err.println(
	    "Usage: snmpping [-c community] [-o oid] host [ host ...]");
	System.exit(1);
    }
}
