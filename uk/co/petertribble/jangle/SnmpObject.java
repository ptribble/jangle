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
     * @param oid the OID
     * @param value the value of this object
     */
    public SnmpObject(SNMPObjectIdentifier oid, SNMPObject value) {
	this.oid = oid;
	this.value = value;
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
     * @param value the new value of this SnmpObject
     *
     * @see #getValue
     */
    public void setValue(SNMPObject value) {
	this.value = value;
    }

    /**
     * Return a string representation of this SnmpObject, its oid in string
     * form.
     *
     * @return the string form of the oid represented by this SnmpObject
     */
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
	return (s.startsWith("snmp.SNMP")) ? s.substring(9) : s;
    }

    /**
     * Return a string representation of this SnmpObject's value.
     *
     * @return the string form of the value of this SnmpObject
     */
    public String valueString() {
	if (getType().equals("snmp.SNMPOctetString")) {
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
	return (value instanceof SNMPInteger)
	    ? (BigInteger) value.getValue() : BigInteger.ZERO;
    }
}
