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

import javax.swing.DefaultListCellRenderer;
import java.awt.Component;
import javax.swing.JList;

/**
 * Render snmp nodes in a JList, setting the label to the human-readable form
 * rather than the numeric OID if possible.
 *
 * @author Peter Tribble
 */
public class SnmpListCellRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
	setText(SnmpMibManager.getInstance().prettifyOID(value.toString()));
	return this;
    }
}
