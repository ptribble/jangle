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

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.*;
import java.math.BigInteger;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.*;
import org.jfree.data.time.*;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;

/**
 * Display snmp data in a graphical chart.
 *
 * @author Peter Tribble
 */
public class SnmpChart extends AbstractTableModel implements ActionListener {

    static private final SnmpMibManager smm = SnmpMibManager.getInstance();

    private SnmpController sc;
    private JFreeChart chart;
    private Timer timer;
    private int interval;
    private int maxage;
    private TimeSeriesCollection dataset;
    private boolean showdelta;
    // times in milliseonds
    private long lastsnap;
    private String charttitle;

    private Map <String, TimeSeries> tsmap;
    // save previous values for rates, and used as the backing store for
    // the TableModel
    private Map <String, BigInteger> valueMap;
    private List <String> allnames;

    /**
     * Create a new SnmpChart, showing the rate of change of the given oid.
     *
     * @param sc an SnmpController
     * @param oid the oid to show
     * @param charttitle the title to show on the chart
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, String oid, String charttitle,
		int interval, int age) {
	this(sc, oid, charttitle, true, interval, age);
    }

    /**
     * Create a new SnmpChart, showing the given oid.
     *
     * @param sc an SnmpController
     * @param oid the oid to show
     * @param charttitle the title to show on the chart
     * @param showdelta if true, show rates instead of values
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, String oid, String charttitle,
		boolean showdelta, int interval, int age) {
	this.sc = sc;
	this.showdelta = showdelta;
	this.charttitle = charttitle;
	this.interval = interval;
	this.maxage = 1000*age;
	List <String> oids = new ArrayList <String> ();
	oids.add(oid);
	initialize(oids, oids);
    }

    /**
     * Create a new SnmpChart, showing the rate of change of the given OIDs.
     *
     * @param sc an SnmpController
     * @param oids a List of oids to show
     * @param charttitle the title to show on the chart
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, List <SnmpObject> oids,
		String charttitle, int interval, int age) {
	this(sc, oids, charttitle, true, interval, age);
    }

    /**
     * Create a new SnmpChart, showing the rate of change of the given OIDs.
     *
     * @param sc an SnmpController
     * @param oids a List of oids to chart
     * @param alloids a List of all oids, including those not charted
     * @param charttitle the title to show on the chart
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, List <SnmpObject> oids,
		List <SnmpObject> alloids,
		String charttitle, int interval, int age) {
	this(sc, oids, alloids, charttitle, true, interval, age);
    }

    /**
     * Create a new SnmpChart, showing the rate of change of the given OIDs.
     *
     * @param sc an SnmpController
     * @param snos a List of oids to show
     * @param charttitle the title to show on the chart
     * @param showdelta if true, show rates instead of values
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, List <SnmpObject> snos,
		String charttitle, boolean showdelta, int interval, int age) {
	this(sc, snos, snos, charttitle, showdelta, interval, age);
    }

    /**
     * Create a new SnmpChart, showing the rate of change of the given OIDs.
     *
     * @param sc an SnmpController
     * @param snos a List of oids to chart
     * @param tsnos a List of all oids, including those not charted
     * @param charttitle the title to show on the chart
     * @param showdelta if true, show rates instead of values
     * @param interval the update interval in seconds
     * @param age the maximum age of the chart in seconds
     */
    public SnmpChart(SnmpController sc, List <SnmpObject> snos,
		List <SnmpObject> tsnos,
		String charttitle, boolean showdelta, int interval, int age) {
	this.sc = sc;
	this.showdelta = showdelta;
	this.charttitle = charttitle;
	this.interval = interval;
	this.maxage = 1000*age;
	/*
	 * The oids here are the numeric form. So, what we do is to put them
	 * into a TreeMap with the pretty version as the key, so that they are
	 * sorted by name rather than by oid.
	 */
	Map <String, String> oids = new TreeMap <String, String> ();
	Map <String, String> alloids = new TreeMap <String, String> ();
	for (SnmpObject sno : snos) {
	    oids.put(smm.prettifyOID(sno.toString()), sno.toString());
	}
	for (SnmpObject sno : tsnos) {
	    alloids.put(smm.prettifyOID(sno.toString()), sno.toString());
	}
	initialize(new ArrayList <String> (oids.values()),
		new ArrayList <String> (alloids.values()));
    }

    private void initialize(List <String> oids, List <String> alloids) {
	allnames = alloids;
	tsmap = new HashMap <String, TimeSeries> ();
	valueMap = new HashMap <String, BigInteger> ();
	dataset = new TimeSeriesCollection();
	lastsnap = 0;

	for (String oid : oids) {
	    TimeSeries ts = new TimeSeries(smm.prettifyOID(oid));
	    ts.setMaximumItemAge(maxage);
	    dataset.addSeries(ts);
	    tsmap.put(oid, ts);
	    valueMap.put(oid, BigInteger.ZERO);
	}

	updateAccessory();

	String ylabel = showdelta ? SnmpResources.getString("CHART.RATE")
	    : SnmpResources.getString("CHART.VALUE");

	chart = ChartFactory.createTimeSeriesChart(
		charttitle,
		SnmpResources.getString("CHART.TIME"),
		ylabel,
		dataset,
		true,
		true,
		false);

	setAxes();

	startLoop();
    }

    /*
     * Set up the X and Y axes.
     */
    private void setAxes() {
	XYPlot xyplot = chart.getXYPlot();

	String ylabel = showdelta ? SnmpResources.getString("CHART.RATE")
	    : SnmpResources.getString("CHART.VALUE");
	NumberAxis loadaxis = new NumberAxis(ylabel);
	loadaxis.setAutoRange(true);
	loadaxis.setAutoRangeIncludesZero(true);
	xyplot.setRangeAxis(loadaxis);

	DateAxis daxis = new DateAxis(SnmpResources.getString("CHART.TIME"));
	daxis.setAutoRange(true);
	daxis.setFixedAutoRange(maxage);
	xyplot.setDomainAxis(daxis);
    }

    /**
     * Return the chart that is created.
     *
     * @return  The created chart
     */
    public JFreeChart getChart() {
	return chart;
    }

    /*
     * Add another oid to the current chart. If it's an oid we already know
     * about, then simply re-enable the display.
     *
     * @param oid the name of the oid to add
     */
    private void addOid(String oid) {
	if (tsmap.containsKey(oid)) {
	    dataset.addSeries(tsmap.get(oid));
	}
    }

    /*
     * Remove the requested oid from the current chart. It will be hidden, and
     * data will still be collected, so that the graph will be complete if and
     * when it is reinstated.
     *
     * @param oid the name of the oid to remove
     */
    // FIXME is it valid to remove all oids?
    private void removeOid(String oid) {
	dataset.removeSeries(tsmap.get(oid));
    }

    /**
     * Set the maximum age of the chart. Only oids younger than this age will
     * be shown.
     *
     * @param age The required maximum age in seconds.
     */
    public void setMaxAge(int age) {
	maxage = age*1000;
	for (TimeSeries ts : tsmap.values()) {
	    ts.setMaximumItemAge(maxage);
	}
    }

    /**
     * Update the oids.
     */
    public void updateAccessory() {
	double value;
	long newsnap = new Date().getTime();
	// loop over all oids
	for (String stat : tsmap.keySet()) {
	    try {
		BigInteger newvalue = sc.getValue(stat).getNumber();
		if (showdelta) {
		    BigInteger bd = newvalue.subtract(valueMap.get(stat));
		    double dt = (double) (newsnap - lastsnap);
		    value = 1000.0*(bd.doubleValue()/dt);
		    valueMap.put(stat, newvalue);
		} else {
		    value = newvalue.doubleValue();
		}
		tsmap.get(stat).add(new Millisecond(), value);
	    } catch (SnmpException sne) {}
	}
	lastsnap = newsnap;
	fireTableDataChanged();
    }

    /**
     * Start the timer, so the display will continually update.
     */
    public void startLoop() {
	if (timer == null) {
	    timer = new Timer(interval*1000, this);
	}
	timer.start();
    }

    /**
     * Stop the timer, so the display will not update.
     */
    public void stopLoop() {
	if (timer != null) {
	    timer.stop();
	}
    }

    /**
     * Set the update delay.
     *
     * @param interval the delay value in seconds
     */
    public void setDelay(int interval) {
	this.interval = interval;
	if (timer != null) {
	    timer.setDelay(interval*1000);
	}
    }

    public void actionPerformed(ActionEvent e) {
	updateAccessory();
    }

    private String getStringValue(String oid) {
	try {
	    return sc.getValue(oid).valueString();
	} catch (SnmpException sne) {
	    return "";
	}
    }

    /*
     * The following implement the TableModel
     */

    public int getRowCount() {
	return allnames.size();
    }

    public int getColumnCount() {
	return 3;
    }

    public Object getValueAt(int row, int column) {
	String oid = allnames.get(row);
	if (column == 0) {
	    return smm.prettifyOID(oid);
	} else if (column == 1) {
	    return valueMap.get(oid) == null ? getStringValue(oid)
		: valueMap.get(oid);
	} else {
	    return valueMap.get(oid) == null ? Boolean.FALSE :
		Boolean.valueOf(dataset.indexOf(tsmap.get(oid)) > -1);
	}
    }

    @Override
    public String getColumnName(int column) {
	if (column == 0) {
	    return SnmpResources.getString("COLUMN.OID");
	} else if (column == 1) {
	    return SnmpResources.getString("COLUMN.VALUE");
	} else {
	    return SnmpResources.getString("COLUMN.CHARTED");
	}
    }

    @Override
    public Class<?> getColumnClass(int column) {
	if (column == 0) {
	    return String.class;
	} else if (column == 1) {
	    return Object.class;
	} else {
	    return Boolean.class;
	}
    }

    @Override
    public boolean isCellEditable(int row, int column) {
	String oid = allnames.get(row);
	return column == 2 && valueMap.get(oid) != null;
    }

    /*
     * The documentation isn't very clear, but provided you have an editor
     * on a cell - and for a Boolean you get a checkbox editor provided for
     * you - then all you need to do is have the following to capture the
     * output that it generates.
     */

    public void setValueAt(Object value, int row, int column) {
	if (column == 2) {
	    String oid = allnames.get(row);
	    if (((Boolean) value).booleanValue()) {
		addOid(oid);
	    } else {
		removeOid(oid);
	    }
	}
    }
}
