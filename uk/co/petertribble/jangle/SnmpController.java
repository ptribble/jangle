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
    public SnmpController(SnmpParams params) {
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
    private SNMPVarBindList getNextMIBEntry(String s) throws SnmpException {
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
     * @param s  a String naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getNext(String s) throws SnmpException {
	return getSno(getNextMIBEntry(s));
    }

    /**
     * Return the next SnmpObject after the given entry.
     *
     * @param sno  a SnmpObject naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getNext(SnmpObject sno) throws SnmpException {
	return getNext(sno.toString());
    }

    private SNMPVarBindList getMIBEntry(String s) throws SnmpException {
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
     * @param s  a String naming the oid of interest
     *
     * @return an SnmpObject object
     *
     * @throws SnmpException if an error occurs
     */
    public SnmpObject getValue(String s) throws SnmpException {
	return getSno(getMIBEntry(s));
    }

    private SnmpObject getSno(SNMPVarBindList newVars) {
	if (newVars == null) {
	    return null;
	}
	SNMPSequence pair = (SNMPSequence) newVars.getSNMPObjectAt(0);
	SNMPObjectIdentifier snmpOID =
		(SNMPObjectIdentifier) pair.getSNMPObjectAt(0);
	return new SnmpObject(snmpOID, pair.getSNMPObjectAt(1));
    }
}
