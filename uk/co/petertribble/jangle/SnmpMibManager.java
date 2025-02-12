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

import java.util.*;
import java.io.File;
import net.percederberg.mibble.*;
import net.percederberg.mibble.value.*;

/**
 * SnmpMibManager provides a mechanism to load and query MIB files.
 *
 * @author Peter Tribble
 */
public final class SnmpMibManager {

    private static SnmpMibManager smminstance;

    private Map<String, String> oidMap;
    private Map<String, String> oid2nameMap;
    private Map<String, String> name2oidMap;
    private MibLoader mibloader;

    /**
     * Return the single instance of this SnmpMibManager.
     *
     * @return the single instance of this SnmpMibManager
     */
    public static synchronized SnmpMibManager getInstance() {
	if (smminstance == null) {
	    smminstance = new SnmpMibManager();
	}
	return smminstance;
    }

    private SnmpMibManager() {
	oidMap = new HashMap<>();
	oid2nameMap = new HashMap<>();
	name2oidMap = new HashMap<>();
	mibloader = new MibLoader();
	// solaris/opensolaris
	readMIBs("/etc/sma/snmp/mibs");
	// ubuntu
	readMIBs("/usr/share/snmp/mibs");
	// my own systems
	readMIBs("/opt/Net-SNMP/share/snmp/mibs");
    }

    private void readMIBs(String location) {
	readMIBs(new File(location));
    }

    /**
     * Load any MIB files found in the given location.
     *
     * @param f1 a directory to look for MIB files in
     */
    protected void readMIBs(File f1) {
	if (f1.exists()) {
	    mibloader.addDir(f1);
	    for (File f : f1.listFiles()) {
		try {
		    mibloader.load(f);
		} catch (Exception e) { }
	    }
	    for (Mib mib : mibloader.getAllMibs()) {
		scanMib(mib);
	    }
	}
    }

    private void scanMib(Mib mib) {
	for (Object o : mib.getAllSymbols()) {
	    MibSymbol symbol = (MibSymbol) o;
	    ObjectIdentifierValue value = extractOid(symbol);
	    if (value != null) {
		oidMap.put(value.toString(), value.getName());
		name2oidMap.put(symbol.toString(), value.toString());
		oid2nameMap.put(value.toString(), symbol.toString());
	    }
	}
    }

    private ObjectIdentifierValue extractOid(MibSymbol symbol) {
	if (symbol instanceof MibValueSymbol) {
	    MibValue value = ((MibValueSymbol) symbol).getValue();
	    if (value instanceof ObjectIdentifierValue) {
		return (ObjectIdentifierValue) value;
	    }
	}
	return null;
    }

    /**
     * Converts a numeric OID into its textual representation. This will
     * attempt to convert the root of the oid into a name, and append the
     * instance. If no match can be found, simply returns the original string.
     *
     * @param sno the SnmpObject to prettify
     *
     * @return a prettified version of the given oid
     */
    public String prettifyOID(SnmpObject sno) {
	return prettifyOID(sno.toString());
    }

    /**
     * Converts a numeric OID into its textual representation. This will
     * attempt to convert the root of the oid into a name, and append the
     * instance. If no match can be found, simply returns the original string.
     *
     * @param oid the oid to prettify
     *
     * @return a prettified version of the given oid
     */
    public String prettifyOID(String oid) {
	// start with the given value
	String valname = oidMap.get(oid);
	if (valname != null) {
	    return valname;
	}
	// now walk our way back through the string
	String spar = SnmpUtil.getParentOID(oid);
	while (spar != null) {
	    valname = oidMap.get(spar);
	    if (valname != null) {
		return valname + oid.substring(spar.length());
	    }
	    spar = SnmpUtil.getParentOID(spar);
	}
	// if nothing else, return what we started with
	return oid;
    }

    private String getMibForOID(String oid) {
	return oid2nameMap.get(oid);
    }

    /**
     * Return the closest MIB entry for this OID string. If nothing
     * matches, return null.
     *
     * @param oid the oid to retrieve the MIB entry for
     *
     * @return the closest MIB entry for this OID string
     */
    public String getNearestMibForOID(String oid) {
	String namepar = getMibForOID(oid);
	if (namepar != null) {
	    return namepar;
	}
	String spar = SnmpUtil.getParentOID(oid);
	while (spar != null) {
	    namepar = getMibForOID(spar);
	    if (namepar != null) {
		return namepar;
	    }
	    spar = SnmpUtil.getParentOID(spar);
	}
	return null;
    }

    /**
     * Return the OID corresponding to this name, or null if it's not known.
     *
     * @param name the name to convert into an OID
     *
     * @return the OID corresponding to this name
     */
    public String getOIDForName(String name) {
	return name2oidMap.get(name);
    }
}
