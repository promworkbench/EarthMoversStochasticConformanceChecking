package org.processmining.earthmoversstochasticconformancechecking.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.processmining.earthmoversstochasticconformancechecking.tracealignments.projection.StochasticTraceAlignmentsLogProjection;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMapFixed;
import org.processmining.plugins.graphviz.colourMaps.ColourMapOpacity;

/**
 * Class to visualise a stochastic aligned trace.
 * 
 * Adapted from ProMTraceView (Widgets package) by Felix Mannhardt.
 * 
 * @author sander
 *
 */
public class StochasticTraceAlignmentsLogVisualisationTraceComponent extends JComponent {

	//parameters
	private static final int marginX = 3;
	private static final int marginY = 5;
	private static final int infoWidth = 40;
	private static final int wedgeDent = 10;
	private static final int wedgeWidth_expanded = 100;
	private static final int wedgeWidth_collapsed = 35;

	private static final Color infoColour = Color.black;
	private static final ColourMap wedgeColour = new ColourMapOpacity(new ColourMapFixed(Color.red));

	private static final long serialVersionUID = -6504025920394207911L;
	private static final int DECIMALS = 4;
	private static final String ABBREVIATION_SUFFIX = "..";
	private static final Color wedgeTextColour = Color.black;

	//state
	private int traceIndex;
	private boolean traceSelected;

	//view
	private int wedgeHeight;
	private int extraLabelHeight;
	private FontMetrics fontMetrics;
	private Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

	private Dimension cachedPreferredSize;
	private StochasticTraceAlignmentsLogProjection projection;

	public StochasticTraceAlignmentsLogVisualisationTraceComponent(StochasticTraceAlignmentsLogProjection projection) {
		setProjection(projection);

		this.fontMetrics = getFontMetrics(defaultFont);
		this.wedgeHeight = fontMetrics.getHeight() * 2;
		this.extraLabelHeight = fontMetrics.getHeight();

		updatePreferredSize();
		setDoubleBuffered(true);
		setOpaque(false);
	}

	public void set(int traceIndex) {
		this.traceIndex = traceIndex;
	}

	public void setProjection(StochasticTraceAlignmentsLogProjection projection) {
		this.projection = projection;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(defaultFont);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

		Dimension preferredSize = getPreferredSize();
		Rectangle visibleRegion = g.getClipBounds();
		Rectangle completeRegion = new Rectangle(preferredSize.width, preferredSize.height);

		int y = completeRegion.y;
		int x = completeRegion.x + marginX;

		// draw trace info
		if (isInVisibleRegion(visibleRegion, x, x + infoWidth)) {
			drawTraceLabel(g2d, x, y + marginY, infoColour, formatNumber(projection.getTraceProbability(traceIndex)),
					infoWidth);
		}
		x += infoWidth;
		x += marginX;

		// draw event wedges
		for (int eventIndex = 0; eventIndex < projection.getTrace(traceIndex).length; eventIndex++) {
			int eventWidth = getWedgeWidth() + wedgeDent;

			double synchronousProbability = projection.getEventSyncLikelihoods(traceIndex)[eventIndex];
			Color fill = wedgeColour.colour(1 - synchronousProbability);
			Color strokeColour = Color.black;
			Stroke stroke = new BasicStroke(1.5f);

			// check if we should paint the wedge
			if (isInVisibleRegion(visibleRegion, x, x + eventWidth)) {
				//wedge
				drawEventWedge(g2d, fill, strokeColour, stroke, x, y + marginY, eventIndex);

				//label
				drawTraceLabel(g2d, x + wedgeDent + marginX, y + marginY, wedgeTextColour,
						projection.getTrace(traceIndex)[eventIndex], getWedgeWidth() - marginX);

				//extra label
				drawExtraLabel(g2d, y, x, eventIndex);
			}

			x += getWedgeWidth() + marginX;
		}
	}

	private void drawExtraLabel(Graphics2D g2d, int y, int x, int eventIndex) {
		double syncMove = projection.getEventSyncLikelihoods(traceIndex)[eventIndex];

		int wedgeBottom = y + marginY + wedgeHeight;
		int baseline = wedgeBottom + g2d.getFontMetrics().getAscent();

		if (isTraceSelected()) {
			if (syncMove < 1) {
				g2d.setColor(Color.red);
				g2d.drawString(formatNumber(syncMove) + " sync move", x + marginX, baseline);
			} else {
				g2d.setColor(Color.black);
				g2d.drawString("1 sync move", x + marginX, baseline);
			}
		} else {
			if (syncMove < 1) {
				g2d.setColor(Color.red);
				g2d.drawString(formatNumber(syncMove), x + marginX, baseline);
			}
		}
	}

	private final void drawEventWedge(final Graphics2D g, final Color colour, final Color strokeColour,
			Stroke wedgeStroke, int x, int y, int eventIndex) {

		//wedge
		int t0X = x;
		int t0Y = y;

		int t1X = x + getWedgeWidth();
		int t1Y = y;

		int t2X = x + getWedgeWidth() + wedgeDent;
		int t2Y = y + wedgeHeight / 2;

		int t3X = x + getWedgeWidth();
		int t3Y = y + wedgeHeight;

		int t4X = x;
		int t4Y = y + wedgeHeight;

		int t5X = x + wedgeDent;
		int t5Y = y + wedgeHeight / 2;

		int t6X = x;
		int t6Y = y;

		int[] xCoords = new int[] { t0X, t1X, t2X, t3X, t4X, t5X, t6X };
		int[] yCoords = new int[] { t0Y, t1Y, t2Y, t3Y, t4Y, t5Y, t6Y };

		//stroke
		g.setColor(colour);
		g.fillPolygon(xCoords, yCoords, 6);

		g.setColor(strokeColour);

		Stroke oldStroke = g.getStroke();
		try {
			g.setStroke(wedgeStroke);
			g.drawPolyline(xCoords, yCoords, 7);
		} finally {
			g.setStroke(oldStroke);
		}
	}

	private int getWedgeWidth() {
		if (isTraceSelected()) {
			return wedgeWidth_expanded;
		} else {
			return wedgeWidth_collapsed;
		}
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, wedgeHeight + marginY);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return cachedPreferredSize;
	}

	public void updatePreferredSize() {
		this.cachedPreferredSize = new Dimension(getSelectedTraceWidth(),
				marginY + wedgeHeight + marginY / 2 + extraLabelHeight);
	}

	private int getSelectedTraceWidth() {
		return marginX + infoWidth + (marginX + getWedgeWidth()) * projection.getTrace(traceIndex).length + marginX;
	}

	private static boolean isInVisibleRegion(Rectangle visibleRegion, int x, int elementWidth) {
		int partlyVisibleHorizon = elementWidth;
		int startX = visibleRegion.x;
		int endX = visibleRegion.x + visibleRegion.width;
		return x + partlyVisibleHorizon > startX && x - partlyVisibleHorizon < endX;
	}

	public static String formatNumber(double number) {
		return String.format("%." + DECIMALS + "f", number);
	}

	public String formatPercentage(double number) {
		return String.format("%." + (DECIMALS - 2) + "f", number * 100.0) + "%";
	}

	private final void drawTraceLabel(Graphics2D g2d, int x, int y, Color color, String label, int maxWidth) {
		g2d.setColor(color == null ? getForeground() : color);

		//measure the width of the text
		FontMetrics fontMetrics = g2d.getFontMetrics();
		Rectangle2D bounds = fontMetrics.getStringBounds(label, g2d);
		if (bounds.getWidth() > maxWidth) {
			char[] charArray = label.toCharArray();
			int cutoffPoint = label.length();
			for (int i = label.length(); i > 0; i--) {
				if (fontMetrics.charsWidth(charArray, 0, i) <= maxWidth) {
					cutoffPoint = i;
					break;
				}
			}
			label = label.substring(0, Math.max(0, cutoffPoint - ABBREVIATION_SUFFIX.length()))
					.concat(ABBREVIATION_SUFFIX);
			bounds = fontMetrics.getStringBounds(label, g2d);
		}

		//center vertically
		int center = y + wedgeHeight / 2;

		//g2d.drawLine(x, center, x + infoWidth, center);

		double xHeight = getXHeight(g2d);

		g2d.drawString(label, x, (int) (center + xHeight / 2));
	}

	private double getXHeight(Graphics2D g2d) {
		FontRenderContext fc = new FontRenderContext(null, false, false);
		TextLayout layout = new TextLayout("x", g2d.getFont(), fc);
		double xHeight = layout.getBounds().getHeight();
		return xHeight;
	}

	public boolean isTraceSelected() {
		return traceSelected;
	}

	public void setTraceSelected(boolean traceSelected) {
		this.traceSelected = traceSelected;
	}
}