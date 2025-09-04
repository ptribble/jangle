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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A Tree Node suitable for snmp.
 *
 * @author Peter Tribble
 */
public final class SnmpTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    /**
     * Construct a new SnmpTreeNode.
     *
     * @param o The object to store in this SnmpTreeNode
     */
    public SnmpTreeNode(final Object o) {
	super(o);
    }

    @Override
    public String toString() {
	return (userObject == null) ? null
	    : SnmpMibManager.getInstance().prettifyOID(userObject.toString());
    }
}
