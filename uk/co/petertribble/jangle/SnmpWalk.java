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
