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
    private List <SnmpObject> oidList;

    /**
     * Create a new SnmpList using the specified SnmpController.
     *
     * @param sc the SnmpController to use to retrieve the data
     */
    public SnmpList(SnmpController sc) {
	this.sc = sc;
    }

    /**
     * Retrieve the entire snmp tree.
     *
     * @param startOID the OID to start the walk from
     *
     * @return a List of all SnmpObjects starting from the given oid
     *
     * @throws SnmpException
     */
    public List <SnmpObject> getList(String startOID) throws SnmpException {
	oidList = new ArrayList <SnmpObject> ();
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
    public List <SnmpObject> getSiblings(String oid) {
	List <SnmpObject> lso = new ArrayList <SnmpObject> ();
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
    public List <SnmpObject> getCousins(String oid) {
	List <SnmpObject> lso = new ArrayList <SnmpObject> ();
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
    private String getOIDinstance(String s) {
	int i = s.lastIndexOf('.');
	return (i < 0) ? null : s.substring(i+1);
    }
}
