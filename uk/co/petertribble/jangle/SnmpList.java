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

import java.util.List;
import java.util.ArrayList;

/**
 * A class to hold a list of SnmpObjects retrieved from a server, and
 * utilities for retrieving subsets of that list.
 *
 * @author Peter Tribble
 */
public class SnmpList {

    private SnmpController sc;
    private List<SnmpObject> oidList;

    /**
     * Create a new SnmpList using the specified SnmpController.
     *
     * @param nsc the SnmpController to use to retrieve the data
     */
    public SnmpList(final SnmpController nsc) {
	sc = nsc;
    }

    /**
     * Retrieve the entire snmp tree.
     *
     * @param startOID the OID to start the walk from
     *
     * @return a List of all SnmpObjects starting from the given oid
     *
     * @throws SnmpException if an error occurs
     */
    public List<SnmpObject> getList(final String startOID)
	    throws SnmpException {
	oidList = new ArrayList<>();
	SnmpObject sno = sc.getNext(startOID);
	while (sno != null) {
	    oidList.add(sno);
	    sno = sc.getNext(sno);
	}
	return oidList;
    }

    /**
     * Return the siblings of the given OID from the given List. Siblings are
     * other instances that have the same parent.
     *
     * @param oid an OID to match for siblings
     *
     * @return the List of siblings of the given oid
     */
    public List<SnmpObject> getSiblings(final String oid) {
	List<SnmpObject> lso = new ArrayList<>();
	String sparent = SnmpUtil.getParentOID(oid);
	if (sparent != null) {
	    for (SnmpObject sno : oidList) {
		if (sparent.equals(SnmpUtil.getParentOID(sno.toString()))) {
		    lso.add(sno);
		}
	    }
	}
	return lso;
    }

    /**
     * Return the cousin of the given OID from the given List. Cousins are
     * identical instances that have the same grandparent.
     *
     * @param oid an OID to match for cousins
     *
     * @return the List of cousins of the given oid
     */
    public List<SnmpObject> getCousins(final String oid) {
	List<SnmpObject> lso = new ArrayList<>();
	String sinst = getOIDinstance(oid);
	String sgparent = SnmpUtil.getParentOID(SnmpUtil.getParentOID(oid));
	if (sgparent != null) {
	    for (SnmpObject sno : oidList) {
		String s = sno.toString();
		if (sgparent.equals(
			SnmpUtil.getParentOID(SnmpUtil.getParentOID(s)))
				    && sinst.equals(getOIDinstance(s))) {
		    lso.add(sno);
		}
	    }
	}
	return lso;
    }

    // just get the instance
    private String getOIDinstance(final String s) {
	int i = s.lastIndexOf('.');
	return i < 0 ? null : s.substring(i + 1);
    }
}
