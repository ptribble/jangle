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

import snmp.*;

/**
 * Common snmp utility code for jangle.
 *
 * @author Peter Tribble
 */
public class SnmpUtil {

    /**
     * Return the parent of the given OID.
     *
     * @param s the OID to find the parent of
     *
     * @return the parent of the given OID
     */
    public static String getParentOID(String s) {
	int i = s.lastIndexOf('.');
	return i < 0 ? null : s.substring(0, i);
    }

    /**
     * Return the date associated with dates encoded in OctetStrings, as used
     * in hrSWInstalledDate, hrSystemDate, hrFSLastFullBackupDate, and
     * hrFSLastPartialBackupDate.
     *
     * @param s the String to parse for a date
     *
     * @return a String representation of the date
     */
    public static String getOctetDate(String s) {
	/*
	 * http://net-snmp.sourceforge.net/docs/mibs/host.html#DateAndTime
	 */
	String[] sp = s.split(" ");
	if (sp.length > 7) {
	    int y1 = Integer.parseInt(sp[0], 16);
	    int y2 = Integer.parseInt(sp[1], 16);
	    int mon = Integer.parseInt(sp[2], 16);
	    int dd = Integer.parseInt(sp[3], 16);
	    int hh = Integer.parseInt(sp[4], 16);
	    int mm = Integer.parseInt(sp[5], 16);
	    int ss = Integer.parseInt(sp[6], 16);
	    return String.format("%04d-%02d-%02d %02d:%02d:%02d", 256*y1 + y2,
			mon, dd, hh, mm, ss);
	}
	return "";
    }

    /**
     * Determine whether the OID represents a date, which has a specific
     * hex encoding.
     *
     * @param sno the SnmpObject to test
     *
     * @return true if the value represents a date
     */
    public static boolean isDate(SnmpObject sno) {
	String sp = getParentOID(sno.toString());
	// hrSWInstalledDate
	if ("1.3.6.1.2.1.25.6.3.1.5".equals(sp)) {
	    return true;
	}
	// hrSystemDate
	if ("1.3.6.1.2.1.25.1.2".equals(sp)) {
	    return true;
	}
	// hrFSLastFullBackupDate
	if ("1.3.6.1.2.1.25.3.8.1.8".equals(sp)) {
	    return true;
	}
	// hrFSLastPartialBackupDate
	if ("1.3.6.1.2.1.25.3.8.1.9".equals(sp)) {
	    return true;
	}
	return false;
    }

    /**
     * Show the value in a reasonable representation.
     *
     * @param sno the SnmpObject to display
     *
     * @return the value as a nicely formatted String
     */
    public static String niceString(SnmpObject sno) {
	if (isDate(sno)) {
	    return getOctetDate(
			((SNMPOctetString)sno.getValue()).toHexString());
	}
	return sno.valueString();
    }
}
