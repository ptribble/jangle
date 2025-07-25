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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.event.*;
import uk.co.petertribble.jingle.JingleTextPane;
import org.jfree.chart.ChartPanel;

/**
 * A panel displaying the snmp statistics that are available as a list or
 * tree in a left panel, and details and possibly a graphical chart in the
 * right panel.
 *
 * @author Peter Tribble
 */
public final class SnmpTreePanel extends JPanel implements
        TreeSelectionListener, ListSelectionListener, ActionListener {

    private static final long serialVersionUID = 1L;

    /**
     * Object description.
     */
    private JingleTextPane tp;
    /**
     * For the description of the current OID.
     */
    private JingleTextPane tpdesc;
    /**
     * Hold the main details.
     */
    private JPanel jp1;
    /**
     * Hold the main chart.
     */
    JPanel jp2;
    /**
     * A chart for the current OID.
     */
    private SnmpChart chart;

    /**
     * Hold the chart for siblings.
     */
    private JPanel jp3;
    /**
     * Hold the text for siblings.
     */
    private JPanel jp3t;
    /**
     * For the description of siblings of the current OID.
     */
    private JingleTextPane tpsiblings;
    /**
     * A chart for siblings of the current OID.
     */
    private SnmpChart schart;

    /**
     * Holds the chart for cousins.
     */
    private JPanel jp4;
    /**
     * Hold the text for cousins.
     */
    private JPanel jp4t;
    /**
     * For the description of cousins of the current OID.
     */
    private JingleTextPane tpcousins;
    /**
     * A chart for cousins of the current OID.
     */
    private SnmpChart cchart;

    /**
     * Save errors so we can give friendlier error feedback.
     */
    Exception savederror;

    /**
     * The OIDs as a list.
     */
    private JList<SnmpObject> slist;
    /**
     * The model backing th OIDs as a list.
     */
    private DefaultListModel<SnmpObject> model;
    /**
     * Shows loading progress.
     */
    JProgressBar jpb;
    /**
     * The left tabbed pane, holding the tree and list of OIDs.
     */
    private JTabbedPane jtpl;
    /**
     * The right tabbed pane, holding the output display.
     */
    private JTabbedPane jtpr;
    /**
     * The OIDs as a tree.
     */
    private JTree stree;
    private static final int TAB_D = 0;
    private static final int TAB_A = 1;
    private static final int TAB_S = 2;
    private static final int TAB_C = 3;

    /**
     * The OID to start walking from.
     */
    private String startOID;
    private transient SnmpObject currentOID;
    private transient SnmpController sc;
    private transient SnmpList snl;
    private transient SnmpMibManager smm;
    private transient List<SnmpObject> oidList;

    /**
     * A Timer to update the display in a loop.
     */
    private Timer timer;
    /**
     * The interval at which the display is updated.
     */
    private int interval = 30;
    /**
     * The time range for which data is kept.
     */
    private int age = 1800;

    /**
     * Create a new SnmpTreePanel, starting exploration at the default OID
     * of 1.3.6.1.2.1.
     */
    public SnmpTreePanel() {
	this("1.3.6.1.2.1");
    }

    /**
     * Create a new SnmpTreePanel, starting exploration at the specified OID.
     *
     * @param startOID the OID to start from
     */
    public SnmpTreePanel(String startOID) {
	this.startOID = startOID;
	setLayout(new BorderLayout());

	jtpl = new JTabbedPane();
	model = new DefaultListModel<>();
	oidList = new ArrayList<>();
	slist = new JList<>(model);
	slist.addListSelectionListener(this);
	slist.setCellRenderer(new SnmpListCellRenderer());
	jtpl.add(SnmpResources.getString("SNMP.LIST.TEXT"),
		new JScrollPane(slist));

	// we use explicit placement so the tabs can be manipulated later
	jtpr = new JTabbedPane();

	// details tab
	jp1 = new JPanel(new BorderLayout());
	tp = new JingleTextPane("text/plain");
	jp1.add(new JScrollPane(tp));
	// jp2 holds the chart
	jp2 = new JPanel(new BorderLayout());
	JSplitPane jpt = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					   jp1, jp2);
	jpt.setOneTouchExpandable(true);
	jpt.setDividerLocation(120);

	jtpr.insertTab(SnmpResources.getString("SNMP.DETAILS.TEXT"),
		(Icon) null, jpt, (String) null, TAB_D);

	tpdesc = new JingleTextPane("text/plain");
	jtpr.insertTab(SnmpResources.getString("SNMP.ABOUT.TEXT"), (Icon) null,
		new JScrollPane(tpdesc), (String) null, TAB_A);

	// siblings tab
	tpsiblings = new JingleTextPane("text/plain");
	jp3 = new JPanel(new BorderLayout());
	jp3t = new JPanel(new BorderLayout());
	JSplitPane jps = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					   jp3t, jp3);
	jps.setOneTouchExpandable(true);
	jps.setDividerLocation(200);
	jtpr.insertTab(SnmpResources.getString("SNMP.SIBLINGS.TEXT"),
		(Icon) null, jps, (String) null, TAB_S);

	// cousins tab
	tpcousins = new JingleTextPane("text/plain");
	jp4 = new JPanel(new BorderLayout());
	jp4t = new JPanel(new BorderLayout());
	JSplitPane jpc = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					   jp4t, jp4);
	jpc.setOneTouchExpandable(true);
	jpc.setDividerLocation(200);
	jtpr.insertTab(SnmpResources.getString("SNMP.COUSINS.TEXT"),
		(Icon) null, jpc, (String) null, TAB_C);

	// split pane to hold the lot
	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					   jtpl, jtpr);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(200);
	add(psplit);
    }

    /**
     * Start exploring the snmp tree.
     *
     * @param params details of the snmp server to query
     */
    public void exploreServer(SnmpParams params) {
	sc = new SnmpController(params);
	snl = new SnmpList(sc);
	jpb = new JProgressBar(0, 1);
	jpb.setValue(0);
	jpb.setIndeterminate(true);
	jp2.add(jpb, BorderLayout.SOUTH);
	jp2.validate();
	// load data in background
	new ExploreWorker().execute();
    }

    /*
     * This is where the tree is walked and the data loaded. We do the snmp
     * tree walk in the background and update the gui when we're done - that
     * means building the model for the list and finishing off the progress
     * bar.
     *
     * One possibly bad consequence of this method is that we have to expose
     * the data structures as class variables so that they can be accessed
     * from multiple contexts.
     */
    class ExploreWorker extends SwingWorker<String, Object> {
	@Override
	public String doInBackground() {
	    buildData();
	    return "done";
	}

	@Override
	protected void done() {
	    try {
		jpb.setIndeterminate(false);
		jpb.setValue(1);
		if (savederror == null) {
		    buildUI();
		    startLoop();
		} else {
		    showError();
		}
		jp2.removeAll();
		jp2.validate();
		jp2.repaint();
	    } catch (Exception e) { }
	}
    }

    /**
     * Start the timer, so the display will continually update.
     */
    public void startLoop() {
	if (timer == null) {
	    timer = new Timer(interval * 1000, this);
	}
	timer.start();
    }

    private void setPanel(SnmpTreeNode node) {
	if (node != null) {
	    if (node.getUserObject() instanceof SnmpObject) {
		showObject((SnmpObject) node.getUserObject());
	    } else if (node.getUserObject() instanceof String) {
		showObject((String) node.getUserObject());
	    }
	}
    }

    /*
     * Show the MIB details for a fake parent node.
     * We only do any work if it exists - if we can't find a MIB entry
     * then we leave the display unchanged.
     */
    private void showObject(String oid) {
	if (smm == null) {
	    smm = SnmpMibManager.getInstance();
	}
	String s = smm.getNearestMibForOID(oid);
	if (s != null) {
	    clearCharts();
	    tpdesc.setText(s);
	    jtpr.setSelectedIndex(TAB_A);
	    jtpr.setEnabledAt(TAB_D, false);
	    jtpr.setEnabledAt(TAB_S, false);
	    jtpr.setEnabledAt(TAB_C, false);
	}
    }

    /*
     * Show the object. If the new object is a sibling of the current
     * object, leave the sibling chart intact; if the new object is a
     * cousin of the current object, leave the cousin chart intact.
     */
    private void showObject(SnmpObject sno) {
	if (smm == null) {
	    smm = SnmpMibManager.getInstance();
	}
	jtpr.setEnabledAt(TAB_D, true);
	jtpr.setEnabledAt(TAB_S, true);
	jtpr.setEnabledAt(TAB_C, true);
	// stop any current charts
	clearChart();
	SnmpObject prevOID = currentOID;
	currentOID = sno;
	setText(sno);
	String oid = sno.toString();
	// show a chart if it looks like a number
	if (canChart(sno)) {
	    chart = new SnmpChart(sc, oid, smm.prettifyOID(oid), interval, age);
	    jp2.add(new ChartPanel(chart.getChart()));
	}
	jp2.validate();
	jp1.validate();
	String s = smm.getNearestMibForOID(oid);
	tpdesc.setText((s == null) ? "" : s);
	if (!isSibling(currentOID, prevOID)) {
	    clearSiblingChart();
	    showSiblings(sno);
	}
	if (!isCousin(currentOID, prevOID)) {
	    clearCousinChart();
	    showCousins(sno);
	}
    }

    /*
     * Check whether two SnmpObjects are siblings
     */
    private boolean isSibling(SnmpObject sn1, SnmpObject sn2) {
	if (sn1 == null || sn2 == null) {
	    return false;
	}
	return snl.getSiblings(sn1.toString()).contains(sn2);
    }

    /*
     * Check whether two SnmpObjects are cousins
     */
    private boolean isCousin(SnmpObject sn1, SnmpObject sn2) {
	if (sn1 == null || sn2 == null) {
	    return false;
	}
	return snl.getCousins(sn1.toString()).contains(sn2);
    }

    private boolean canChart(SnmpObject sno) {
	/*
	 * This is only called from showObject, so we can assume that smm has
	 * been initialized. We manually refuse to chart certain names that
	 * simply wouldn't make sense.
	 */
	String sp = smm.prettifyOID(sno);
	if (sp.startsWith("hrSWInstalled")
		|| sp.startsWith("hrSWRun")) {
	    return false;
	}
	String typeString = sno.getTypeString();
	if (!"OctetString".equals(typeString)
		&& !"IPAddress".equals(typeString)
		&& !"Null".equals(typeString)
		&& !"ObjectIdentifier".equals(typeString)) {
	    return true;
	}
	return false;
    }

    private void clearCharts() {
	clearChart();
	clearSiblingChart();
	clearCousinChart();
    }

    private void clearChart() {
	if (chart != null) {
	    chart.stopLoop();
	    jp2.removeAll();
	    jp2.validate();
	    jp2.repaint();
	}
    }

    private void clearSiblingChart() {
	jp3t.removeAll();
	jp3t.validate();
	jp3t.repaint();
	if (schart != null) {
	    schart.stopLoop();
	    jp3.removeAll();
	    jp3.validate();
	    jp3.repaint();
	}
    }

    private void clearCousinChart() {
	jp4t.removeAll();
	jp4t.validate();
	jp4t.repaint();
	if (cchart != null) {
	    cchart.stopLoop();
	    jp4.removeAll();
	    jp4.validate();
	    jp4.repaint();
	}
    }

    private void showSiblings(SnmpObject sno) {
	String oid = sno.toString();
	List<SnmpObject> ls = snl.getSiblings(oid);
	if (ls.size() > 1) {
	    if (canChart(sno)) {
		schart = new SnmpChart(sc, ls,
			smm.prettifyOID(SnmpUtil.getParentOID(oid)),
			interval, age);
		jp3.add(new ChartPanel(schart.getChart()));
		jp3t.add(new JScrollPane(new JTable(schart)));
	    } else {
		StringBuilder sb = new StringBuilder();
		for (SnmpObject so : ls) {
		    sb.append(smm.prettifyOID(so)).append('=')
			.append(SnmpUtil.niceString(so)).append('\n');
		}
		tpsiblings.setText(sb.toString());
		jp3t.add(new JScrollPane(tpsiblings));
	    }
	    jp3.validate();
	    jp3t.validate();
	} else {
	    jtpr.setEnabledAt(TAB_S, false);
	}
    }

    private void showCousins(SnmpObject sno) {
	String oid = sno.toString();
	List<SnmpObject> alloids = snl.getCousins(oid);
	if (alloids.size() > 1) {
	    StringBuilder sb = new StringBuilder();
	    // a list to hold those that can be charted
	    List<SnmpObject> lc = new ArrayList<>();
	    for (SnmpObject so : alloids) {
		sb.append(smm.prettifyOID(so)).append('=')
		    .append(SnmpUtil.niceString(so)).append('\n');
		if (canChart(so)) {
		    lc.add(so);
		}
	    }
	    if (lc.size() > 1) {
		cchart = new SnmpChart(sc, lc, alloids,
		    smm.prettifyOID(
			SnmpUtil.getParentOID(SnmpUtil.getParentOID(oid))),
			    interval, age);
		jp4.add(new ChartPanel(cchart.getChart()));
		jp4t.add(new JScrollPane(new JTable(cchart)));
	    } else {
		tpcousins.setText(sb.toString());
		jp4t.add(new JScrollPane(tpcousins));
	    }
	    jp4.validate();
	    jp4t.validate();
	} else {
	    jtpr.setEnabledAt(TAB_C, false);
	}
    }

    private void setText(SnmpObject sno) {
	StringBuilder sb = new StringBuilder(32);
	sb.append("OID: ").append(smm.prettifyOID(sno));
	String typeString = sno.getTypeString();
	sb.append("\nType: ").append(typeString)
	    .append("\nValue: ").append(SnmpUtil.niceString(sno));
	if ("ObjectIdentifier".equals(typeString)) {
	    sb.append(" = ").append(smm.prettifyOID(sno.getValue().toString()));
	}
	sb.append('\n');
	tp.setText(sb.toString());
    }

    /**
     * Set the update interval.
     *
     * @param interval the desired update delay in seconds
     */
    public void setDelay(int interval) {
	this.interval = interval;
	if (timer != null) {
	    timer.setDelay(interval * 1000);
	}
	if (chart != null) {
	    chart.setDelay(interval);
	}
	if (schart != null) {
	    schart.setDelay(interval);
	}
	if (cchart != null) {
	    cchart.setDelay(interval);
	}
    }

    /**
     * Set the chart display window.
     *
     * @param age The required maximum chart age in seconds.
     */
    public void setMaxAge(int age) {
	this.age = age;
	if (chart != null) {
	    chart.setMaxAge(age);
	}
	if (schart != null) {
	    schart.setMaxAge(age);
	}
	if (cchart != null) {
	    cchart.setMaxAge(age);
	}
    }

    private void updateCurrent() {
	if (currentOID != null) {
	    try {
		currentOID.setValue(sc.getValue(currentOID.toString())
				    .getValue());
	    } catch (SnmpException sne) {
		// FIXME do summat
	    }
	    setText(currentOID);
	}
    }

    // retrieve the data via an snmp walk of the tree
    void buildData() {
	try {
	    oidList = snl.getList(startOID);
	} catch (SnmpException sne) {
	    savederror = sne;
	}
    }

    void showError() {
	if (savederror != null) {
	    System.out.println(savederror.getMessage());
	    JOptionPane.showMessageDialog(this,
					savederror.getMessage(),
					"SNMP error",
					JOptionPane.ERROR_MESSAGE);
	    savederror = null;
	}
    }

    /*
     * This methods builds both the list and the tree.
     */
    void buildUI() {
	for (SnmpObject sno : oidList) {
	    model.addElement(sno);
	}
	/*
	 * We create a SnmpTreeNode for each element in the list and add it to
	 * a Hash with the name as the key. Then we walk the list - if a node
	 * has a parent, we add it to the parent, else we need to create
	 * intermediate nodes to fill in the tree.
	 */
	// root node
	SnmpTreeNode rootNode = new SnmpTreeNode("SNMP");
	// real nodes
	Map<String, SnmpTreeNode> m = new HashMap<>();
	// intermediate nodes
	Map<String, SnmpTreeNode> m2 = new HashMap<>();
	for (SnmpObject sno : oidList) {
	    m.put(sno.toString(), new SnmpTreeNode(sno));
	}
	for (SnmpObject sno : oidList) {
	    String s = sno.toString();
	    SnmpTreeNode mynode = m.get(s);
	    String parent = SnmpUtil.getParentOID(s);
	    while (parent != null) {
		if (m.containsKey(parent)) {
		    /*
		     * If there's a parent node, add this as a child.
		     * This almost never happens.
		     */
		    m.get(parent).add(mynode);
		    mynode = m.get(parent);
		} else if (m2.containsKey(parent)) {
		    /*
		     * If we've already created a parent node, add this node
		     * to that.
		     */
		    m2.get(parent).add(mynode);
		    mynode = m2.get(parent);
		} else {
		    /*
		     * Need to create a new node
		     */
		    SnmpTreeNode pnode = new SnmpTreeNode(parent);
		    pnode.add(mynode);
		    m2.put(parent, pnode);
		    mynode = pnode;
		}
		parent = SnmpUtil.getParentOID(parent);
	    }
	    rootNode.add(mynode);
	}
	if (stree == null) {
	    stree = new JTree(rootNode);
	    stree.addTreeSelectionListener(this);
	    jtpl.add(SnmpResources.getString("SNMP.TREE.TEXT"),
		new JScrollPane(stree));
	} else {
	    stree.setModel(new DefaultTreeModel(rootNode));
	}
    }

    // handle ListSelectionListener events
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && slist.getSelectedIndex() != -1) {
	    Object o = slist.getSelectedValue();
	    if (o instanceof SnmpObject) {
		showObject((SnmpObject) o);
	    }
        }
    }

    // handle TreeSelectionListener events
    @Override
    public void valueChanged(TreeSelectionEvent e) {
	TreePath tpth = e.getNewLeadSelectionPath();
	if (tpth != null) {
	    setPanel((SnmpTreeNode) tpth.getLastPathComponent());
	}
    }

    // handle timer events
    @Override
    public void actionPerformed(ActionEvent e) {
	updateCurrent();
    }
}
