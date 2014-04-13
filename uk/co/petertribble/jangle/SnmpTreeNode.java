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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A Tree Node suitable for snmp.
 *
 * @author Peter Tribble
 */
public class SnmpTreeNode extends DefaultMutableTreeNode {

    /**
     * Construct a new SnmpTreeNode.
     *
     * @param o The object to store in this SnmpTreeNode
     */
    public SnmpTreeNode(Object o) {
	super(o);
    }

    public String toString() {
	return (userObject == null) ? null :
	    SnmpMibManager.getInstance().prettifyOID(userObject.toString());
    }
}
