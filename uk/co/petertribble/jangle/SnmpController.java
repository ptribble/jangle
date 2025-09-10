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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import snmp.*;
import java.io.IOException;

/**
 * A control class to manage Snmp queries.
 *
 * @author Peter Tribble
 */
public class SnmpController {

    private SnmpParams params;
    private SNMPv1CommunicationInterface ifv1;
    private SnmpException savedException;

    /**
     * Create a new SnmpController with parameters detailing how to
     * communicate with a server.
     *
     * @param params an SnmpParams with connection details
     */
    public SnmpController(final SnmpParams params) {
	this.params = params;
	initialize();
    }

    private void initialize() {
	try {
	    InetAddress hostAddress = InetAddress.getByName(params.getServer());
	    ifv1 =
		new SNMPv1CommunicationInterface(params.getVersion(),
						hostAddress,
						params.getCommunity(),
						params.getPort());
	    // FIXME be specific
	} catch (UnknownHostException e) {
	    savedException = new SnmpException("Unknown host.");
	} catch (SocketException e) {
	    savedException = new SnmpException("Socket error.");
	}
    }

    /*
     * Return the SNMPv1CommunicationInterface associated with this
     * SnmpController.
     *
     * @return the SNMPv1CommunicationInterface associated with this
     * SnmpController.
     */
    private SNMPv1CommunicationInterface getInterfaceV1() throws SnmpException {
	if (savedException != null) {
	    throw savedException;
	}
	return ifv1;
    }

    // FIXME throw the saved SnmpException if we're uninitialized
    private SNMPVarBindList getNextMIBEntry(final String s)
	throws SnmpException {
	try {
	    return getInterfaceV1().getNextMIBEntry(s);
	} catch (IOException ioe) {
	    // throw new SnmpException("IOException getNextMIBEntry " + s);
	    System.err.println("IOException getNextMIBEntry " + s);
	} catch (SNMPBadValueException sbve) {
	    // throw new SnmpException("SNMPBadValueException " + s);
	    System.err.println("SNMPBadValueException getNextMIBEntry " + s);
	} catch (SNMPGetException sge) {
	    // throw new SnmpException("SNMPGetException getNextMIBEntry " + s);
	    System.err.println("SNMPGetException getNextMIBEntry " + s);
	}
	return null;
    }

    /**
     * Return the next SnmpObject after the given entry.
     *
     * @param s a String naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getNext(final String s) throws SnmpException {
	return getSno(getNextMIBEntry(s));
    }

    /**
     * Return the next SnmpObject after the given entry.
     *
     * @param sno an SnmpObject naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getNext(final SnmpObject sno) throws SnmpException {
	return getNext(sno.toString());
    }

    private SNMPVarBindList getMIBEntry(final String s) throws SnmpException {
	try {
	    return getInterfaceV1().getMIBEntry(s);
	} catch (IOException ioe) {
	    throw new SnmpException("IOException getMIBEntry Retrieving " + s);
	} catch (SNMPBadValueException sbve) {
	    throw new SnmpException("SNMPBadValueException Retrieving " + s);
	} catch (SNMPGetException sbve) {
	    throw new SnmpException("SNMPGetException Retrieving " + s);
	}
    }

    /**
     * Return the SnmpObject corresponding to the given entry.
     *
     * @param s a String naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getValue(final String s) throws SnmpException {
	return getSno(getMIBEntry(s));
    }

    private SnmpObject getSno(final SNMPVarBindList newVars) {
	if (newVars == null) {
	    return null;
	}
	SNMPSequence pair = (SNMPSequence) newVars.getSNMPObjectAt(0);
	SNMPObjectIdentifier snmpOID =
		(SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
	return new SnmpObject(snmpOID, pair.getSNMPObjectAt(1));
    }
}
