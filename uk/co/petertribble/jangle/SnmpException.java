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

/**
 * Handle custom snmp exceptions for jangle.
 *
 * @author Peter Tribble
 */
public class SnmpException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new SnmpException, with no message text.
     */
    public SnmpException() {
	super();
    }

    /**
     * Create a new SnmpException, with the given message text.
     *
     * @param s the text of the exception
     */
    public SnmpException(String s) {
	super(s);
    }
}
