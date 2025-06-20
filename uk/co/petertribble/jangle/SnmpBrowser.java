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

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import uk.co.petertribble.jingle.JingleMultiFrame;
import uk.co.petertribble.jingle.JingleInfoFrame;
import uk.co.petertribble.jingle.JingleIntTextField;
import static uk.co.petertribble.jangle.SnmpParams.DEFAULT_HOST;
import static uk.co.petertribble.jangle.SnmpParams.DEFAULT_COMMUNITY;
import static uk.co.petertribble.jangle.SnmpParams.DEFAULT_PORT;

/**
 * A graphical utility to browse and display snmp statistics. It allows the
 * user to specify a host and community string, and uses SnmpTreePanel to show
 * the available data as both a list and a tree.
 *
 * @author Peter Tribble
 */
public final class SnmpBrowser extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * The main tree display.
     */
    private SnmpTreePanel stp;

    /**
     * A menu item to exit the application.
     */
    private JMenuItem exitItem;
    /**
     * A menu item to open another cloned window.
     */
    private JMenuItem cloneItem;
    /**
     * A menu item to load MIBs into the application.
     */
    private JMenuItem loadMibItem;
    /**
     * A menu item to close the current window.
     */
    private JMenuItem closeItem;
    /**
     * A menu item to show the help.
     */
    private JMenuItem helpItem;
    /**
     * A menu item to show the license.
     */
    private JMenuItem licenseItem;

    /**
     * A button to set the update interval to 5s.
     */
    private JRadioButtonMenuItem sleepItem5;
    /**
     * A button to set the update interval to 15s.
     */
    private JRadioButtonMenuItem sleepItem15;
    /**
     * A button to set the update interval to 30s.
     */
    private JRadioButtonMenuItem sleepItem30;
    /**
     * A button to set the update interval to 1 minute.
     */
    private JRadioButtonMenuItem sleepItem60;

    /**
     * A button to set the data retention to 30 minutes.
     */
    private JRadioButtonMenuItem ageItem30m;
    /**
     * A button to set the data retention to 2 hours.
     */
    private JRadioButtonMenuItem ageItem2h;
    /**
     * A button to set the data retention to 8 hours.
     */
    private JRadioButtonMenuItem ageItem8h;
    /**
     * A button to set the data retention to 24 hours.
     */
    private JRadioButtonMenuItem ageItem24h;

    /**
     * A field to enter the server name.
     */
    private JTextField jtserver;
    /**
     * A field to enter the server port.
     */
    private JingleIntTextField jtport;
    /**
     * A field to enter the server community string.
     */
    private JTextField jtcomm;
    /**
     * A button that opens the connection to a server to load data.
     */
    private JButton jbload;

    /**
     * Create an SNMP browser application.
     */
    public SnmpBrowser() {
	super(SnmpResources.getString("BROWSERUI.NAME.TEXT"));

	new SmmWorker().execute();

	addWindowListener(new WindowExit());
	JMenuBar jm = new JMenuBar();

	JMenu jme = new JMenu(SnmpResources.getString("FILE.TEXT"));
	jme.setMnemonic(KeyEvent.VK_F);
	cloneItem = new JMenuItem(
				SnmpResources.getString("FILE.NEWBROWSER.TEXT"),
				KeyEvent.VK_B);
	cloneItem.addActionListener(this);
	jme.add(cloneItem);
	loadMibItem = new JMenuItem(
				SnmpResources.getString("FILE.LOADMIB.TEXT"),
				KeyEvent.VK_M);
	loadMibItem.addActionListener(this);
	jme.add(loadMibItem);
	closeItem = new JMenuItem(
				SnmpResources.getString("FILE.CLOSEWIN.TEXT"),
				KeyEvent.VK_W);
	closeItem.addActionListener(this);
	jme.add(closeItem);
	exitItem = new JMenuItem(SnmpResources.getString("FILE.EXIT.TEXT"),
				KeyEvent.VK_X);
	exitItem.addActionListener(this);
	jme.add(exitItem);

	JingleMultiFrame.register(this, closeItem);

	JMenu jma = new JMenu(SnmpResources.getString("AGE.TEXT"));
	jma.setMnemonic(KeyEvent.VK_D);
	ageItem30m = new JRadioButtonMenuItem(
				SnmpResources.getString("AGE.30M"), true);
	ageItem30m.addActionListener(this);
	ageItem2h = new JRadioButtonMenuItem(
				SnmpResources.getString("AGE.2H"));
	ageItem2h.addActionListener(this);
	ageItem8h = new JRadioButtonMenuItem(
				SnmpResources.getString("AGE.8H"));
	ageItem8h.addActionListener(this);
	ageItem24h = new JRadioButtonMenuItem(
				SnmpResources.getString("AGE.24H"));
	ageItem24h.addActionListener(this);
	jma.add(ageItem30m);
	jma.add(ageItem2h);
	jma.add(ageItem8h);
	jma.add(ageItem24h);

	ButtonGroup ageGroup = new ButtonGroup();
	ageGroup.add(ageItem30m);
	ageGroup.add(ageItem2h);
	ageGroup.add(ageItem8h);
	ageGroup.add(ageItem24h);

	JMenu jms = new JMenu(SnmpResources.getString("SLEEP.TEXT"));
	jms.setMnemonic(KeyEvent.VK_U);
	sleepItem5 = new JRadioButtonMenuItem(
				SnmpResources.getString("SLEEP.5"));
	sleepItem5.addActionListener(this);
	sleepItem15 = new JRadioButtonMenuItem(
				SnmpResources.getString("SLEEP.15"));
	sleepItem15.addActionListener(this);
	sleepItem30 = new JRadioButtonMenuItem(
				SnmpResources.getString("SLEEP.30"), true);
	sleepItem30.addActionListener(this);
	sleepItem60 = new JRadioButtonMenuItem(
				SnmpResources.getString("SLEEP.60"));
	sleepItem60.addActionListener(this);
	jms.add(sleepItem5);
	jms.add(sleepItem15);
	jms.add(sleepItem30);
	jms.add(sleepItem60);

	ButtonGroup sleepGroup = new ButtonGroup();
	sleepGroup.add(sleepItem5);
	sleepGroup.add(sleepItem15);
	sleepGroup.add(sleepItem30);
	sleepGroup.add(sleepItem60);

	JMenu jmh = new JMenu(SnmpResources.getString("HELP.TEXT"));
	jmh.setMnemonic(KeyEvent.VK_H);
	helpItem = new JMenuItem(SnmpResources.getString("HELP.ABOUT.TEXT")
				+ " jangle", KeyEvent.VK_A);
	helpItem.addActionListener(this);
	jmh.add(helpItem);
	licenseItem = new JMenuItem(
				SnmpResources.getString("HELP.LICENSE.TEXT"),
				KeyEvent.VK_L);
	licenseItem.addActionListener(this);
	jmh.add(licenseItem);

	jm.add(jme);
	jm.add(jms);
	jm.add(jma);
	jm.add(jmh);

	setJMenuBar(jm);

	JPanel jp = new JPanel(new BorderLayout());
	setContentPane(jp);

	// a panel for the user to enter the server details.
	JPanel jpq = new JPanel(new FlowLayout());
	// FIXME snmp version?
	jpq.add(new JLabel(SnmpResources.getString("SNMP.SERVER")));
	jtserver = new JTextField(DEFAULT_HOST, 12);
	jpq.add(jtserver);
	jpq.add(new JLabel(SnmpResources.getString("SNMP.PORT")));
	jtport = new JingleIntTextField(DEFAULT_PORT, 4);
	jpq.add(jtport);
	jpq.add(new JLabel(SnmpResources.getString("SNMP.COMMUNITY")));
	jtcomm = new JTextField(DEFAULT_COMMUNITY, 12);
	jpq.add(jtcomm);
	jbload = new JButton(SnmpResources.getString("SNMP.LOAD"));
	jbload.addActionListener(this);
	jpq.add(jbload);

	jp.add(jpq, BorderLayout.NORTH);

	stp = new SnmpTreePanel();
	jp.add(stp);

	setIconImage(new ImageIcon(this.getClass().getClassLoader()
			.getResource("pixmaps/jangle.png")).getImage());

	setSize(840, 640);
	setVisible(true);
    }

    class WindowExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    JingleMultiFrame.unregister(SnmpBrowser.this);
	}
    }

    /*
     * Set the update delay.
     *
     * @param i the delay value in seconds
     */
    private void setDelay(int i) {
	stp.setDelay(i);
    }

    /*
     * Set the duration of the display.
     *
     * @param i the maximum graph age in seconds
     */
    private void setMaxAge(int i) {
	stp.setMaxAge(i);
    }

    class SmmWorker extends SwingWorker<String, Object> {
	@Override
	public String doInBackground() {
	    SnmpMibManager.getInstance();
	    return "done";
	}
    }

    private void doload() {
	stp.exploreServer(new SnmpParams(jtserver.getText(), jtcomm.getText(),
					jtport.getInt()));
    }

    private void doLoadMibs() {
	JFileChooser fc = new JFileChooser();
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	if (fc.showDialog(this, SnmpResources.getString("FILE.SCANDIR"))
	    == JFileChooser.APPROVE_OPTION) {
		SnmpMibManager.getInstance().readMIBs(fc.getSelectedFile());
	}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == cloneItem) {
	    new SnmpBrowser();
	} else if (e.getSource() == closeItem) {
	    JingleMultiFrame.unregister(this);
	} else if (e.getSource() == loadMibItem) {
	    doLoadMibs();
	} else if (e.getSource() == exitItem) {
	    System.exit(0);
	} else if (e.getSource() == helpItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/index.html", "text/html");
	} else if (e.getSource() == licenseItem) {
	    new JingleInfoFrame(this.getClass().getClassLoader(),
				"help/CDDL.txt", "text/plain");
	} else if (e.getSource() == sleepItem5) {
	    setDelay(5);
	} else if (e.getSource() == sleepItem15) {
	    setDelay(15);
	} else if (e.getSource() == sleepItem30) {
	    setDelay(30);
	} else if (e.getSource() == sleepItem60) {
	    setDelay(60);
	} else if (e.getSource() == ageItem30m) {
	    setMaxAge(1800);
	} else if (e.getSource() == ageItem2h) {
	    setMaxAge(7200);
	} else if (e.getSource() == ageItem8h) {
	    setMaxAge(28800);
	} else if (e.getSource() == ageItem24h) {
	    setMaxAge(86400);
	} else if (e.getSource() == jbload) {
	    doload();
	}
    }

    /**
     * Create a new snmp browser.
     *
     * @param args command line arguments, ignored
     */
    public static void main(String[] args) {
	new SnmpBrowser();
    }
}
