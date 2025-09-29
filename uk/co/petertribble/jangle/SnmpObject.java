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

import snmp.SNMPObjectIdentifier;
import snmp.SNMPObject;
import snmp.SNMPInteger;
import java.math.BigInteger;

/**
 * Represent the data held by a node in an SNMP tree.
 *
 * @author Peter Tribble
 */
public class SnmpObject {

    private SNMPObjectIdentifier oid;
    private SNMPObject value;

    /**
     * Create a new SnmpObject of the given OID and value.
     *
     * @param noid the OID
     * @param nvalue the value of this object
     */
    public SnmpObject(final SNMPObjectIdentifier noid,
		      final SNMPObject nvalue) {
	oid = noid;
	value = nvalue;
    }

    /**
     * Return the oid represented by this SnmpObject.
     *
     * @return the oid represented by this SnmpObject
     */
    public SNMPObjectIdentifier getOid() {
	return oid;
    }

    /**
     * Return the value represented by this SnmpObject.
     *
     * @return the value represented by this SnmpObject
     *
     * @see #setValue
     */
    public SNMPObject getValue() {
	return value;
    }

    /**
     * Set the value of this SnmpObject.
     *
     * @param nvalue the new value of this SnmpObject
     *
     * @see #getValue
     */
    public void setValue(final SNMPObject nvalue) {
	value = nvalue;
    }

    /**
     * Return a string representation of this SnmpObject, its oid in string
     * form.
     *
     * @return the string form of the oid represented by this SnmpObject
     */
    @Override
    public String toString() {
	return oid.toString();
    }

    /**
     * Return a long string representation of the type of this SnmpObject.
     *
     * @return a long string representation of the type of this SnmpObject
     */
    public String getType() {
	return value.getClass().getName();
    }

    /**
     * Return a short string representation of the type of this SnmpObject.
     *
     * @return a short string representation of the type of this SnmpObject
     */
    public String getTypeString() {
	String s = getType();
	return s.startsWith("snmp.SNMP") ? s.substring(9) : s;
    }

    /**
     * Return a string representation of this SnmpObject's value.
     *
     * @return the string form of the value of this SnmpObject
     */
    public String valueString() {
	if ("snmp.SNMPOctetString".equals(getType())) {
	    String snmpString = value.toString();
	    // truncate at first null character
	    int nullLocation = snmpString.indexOf('\0');
	    if (nullLocation >= 0) {
		snmpString = snmpString.substring(0, nullLocation);
	    }
	    return snmpString;
	} else {
	    return value.toString();
	}
    }

    /**
     * Return the numerical value of this object. If not a number, return
     * BigInteger.ZERO.
     *
     * @return the numerical value of this object
     */
    public BigInteger getNumber() {
	return value instanceof SNMPInteger
	    ? (BigInteger) value.getValue() : BigInteger.ZERO;
    }
}
