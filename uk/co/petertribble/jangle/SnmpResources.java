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

import java.util.ResourceBundle;

/**
 * Manage resource bundles for Snmp classes.
 *
 * @author Peter Tribble
 */
public class SnmpResources {

    private static final ResourceBundle snmpres =
				ResourceBundle.getBundle("properties/jangle");

    private SnmpResources() {
    }

    /**
     * Returns the string from the resource bundle that corresponds to the
     * given key. If there is no matching key, returns null.
     *
     * @param key The key to be looked up
     *
     * @return The matching String from the resource bundle.
     */
    public static String getString(String key) {
	return snmpres.getString(key);
    }
}
