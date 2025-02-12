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

import javax.swing.DefaultListCellRenderer;
import java.awt.Component;
import javax.swing.JList;

/**
 * Render snmp nodes in a JList, setting the label to the human-readable form
 * rather than the numeric OID if possible.
 *
 * @author Peter Tribble
 */
public final class SnmpListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list,
							Object value,
							int index,
							boolean isSelected,
							boolean cellHasFocus) {
	setText(SnmpMibManager.getInstance().prettifyOID(value.toString()));
	return this;
    }
}
