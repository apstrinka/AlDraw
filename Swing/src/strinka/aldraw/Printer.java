package strinka.aldraw;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Printer implements Printable, Pageable
{
	private AlDrawState state;
	private CoordinateConverter converter;

	public Printer()
	{
		// Do nothing
	}

	public void newJob(AlDrawState state, CoordinateConverter converter)
	{
		this.state = state;
		this.converter = converter;
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(this);
		// job.setPageable(this);
		boolean ok = job.printDialog();
		if (ok)
		{
			try
			{
				job.print();
			} catch (PrinterException ex)
			{
				/* The job did not successfully complete */
			}
		}
	}

	@Override
	public int getNumberOfPages()
	{
		return 1;
	}

	@Override
	public PageFormat getPageFormat(int arg0) throws IndexOutOfBoundsException
	{
		PageFormat ret = new PageFormat();
		Paper paper = new Paper();
		paper.setImageableArea(15, 15, paper.getWidth() - 30, paper.getHeight() - 30);
		ret.setPaper(paper);
		ret.setOrientation(PageFormat.LANDSCAPE);
		return ret;
	}

	@Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException
	{
		return this;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException
	{
		/* We have only one page, and 'page' is zero-based */
		if (page > 0)
		{
			return NO_SUCH_PAGE;
		}

		/*
		 * User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX() + 1, pf.getImageableY() + 1);
		int imageableHeight = (int) pf.getImageableHeight();
		int imageableWidth = (int) pf.getImageableWidth();
		int screenHeight = converter.getHeight();
		int screenWidth = converter.getWidth();
		double m = Math.min((double) imageableHeight / (double) screenHeight, (double) imageableWidth / (double) screenWidth);
		int imageHeight = (int) (screenHeight * m);
		int imageWidth = (int) (screenWidth * m);
		int top = (imageableHeight - imageHeight) / 2;
		int left = (imageableWidth - imageWidth) / 2;

		// Borders around the area being printed for debugging
		// g.drawLine(left, top, left+imageWidth, top);
		// g.drawLine(left+imageWidth, top, left+imageWidth, top+imageHeight);
		// g.drawLine(left+imageWidth, top+imageHeight, left, top+imageHeight);
		// g.drawLine(left, top+imageHeight, left, top);

		Segment[] bounds = converter.getAbstractBoundaries();
		for (int i = 0; i < state.getNumSegments(); i++)
		{
			Segment segment = state.getSegment(i);
			segment = segment.getPortionInsideRectangle(bounds);
			if (segment != null)
			{
				DPoint p1 = converter.abstractToScreenCoord(segment.getP1());
				DPoint p2 = converter.abstractToScreenCoord(segment.getP2());
				g.drawLine((int) (p1.getX() * m + left), (int) (p1.getY() * m + top), (int) (p2.getX() * m + left), (int) (p2.getY() * m + top));
			}
		}
		for (int i = 0; i < state.getNumRays(); i++)
		{
			Ray ray = state.getRay(i);
			Segment segment = ray.getPortionInsideRectangle(bounds);
			if (segment != null)
			{
				DPoint p1 = converter.abstractToScreenCoord(segment.getP1());
				DPoint p2 = converter.abstractToScreenCoord(segment.getP2());
				g.drawLine((int) (p1.getX() * m + left), (int) (p1.getY() * m + top), (int) (p2.getX() * m + left), (int) (p2.getY() * m + top));
			}
		}
		for (int i = 0; i < state.getNumLines(); i++)
		{
			Line line = state.getLine(i);
			Segment segment = line.getPortionInsideRectangle(bounds);
			if (segment != null)
			{
				DPoint p1 = converter.abstractToScreenCoord(segment.getP1());
				DPoint p2 = converter.abstractToScreenCoord(segment.getP2());
				g.drawLine((int) (p1.getX() * m + left), (int) (p1.getY() * m + top), (int) (p2.getX() * m + left), (int) (p2.getY() * m + top));
			}
		}
		for (int i = 0; i < state.getNumCircles(); i++)
		{
			Circle circle = state.getCircle(i);
			Arc[] arcs = circle.getPortionInsideRectangle(bounds);
			for (int j = 0; j < arcs.length; j++)
			{
				DPoint center = converter.abstractToScreenCoord(arcs[j].getCenter());
				double radius = converter.abstractToScreenDist(arcs[j].getRadius());
				int start = (int) Math.round(-Math.toDegrees(arcs[j].getStart() + converter.getAngle()));
				int sweep = (int) Math.round(-Math.toDegrees(arcs[j].getSweep()));
				int x = (int) ((center.getX() - radius) * m + left);
				int y = (int) ((center.getY() - radius) * m + top);
				g.drawArc(x, y, (int) (2 * m * radius), (int) (2 * m * radius), start, sweep);
			}
		}
		for (int i = 0; i < state.getNumArcs(); i++)
		{
			Arc arc = state.getArc(i);
			Arc[] arcs = arc.getPortionInsideRectangle(bounds);
			for (int j = 0; j < arcs.length; j++)
			{
				DPoint center = converter.abstractToScreenCoord(arcs[j].getCenter());
				double radius = converter.abstractToScreenDist(arcs[j].getRadius());
				int start = (int) Math.round(-Math.toDegrees(arcs[j].getStart() + converter.getAngle()));
				int sweep = (int) Math.round(-Math.toDegrees(arcs[j].getSweep()));
				int x = (int) ((center.getX() - radius) * m + left);
				int y = (int) ((center.getY() - radius) * m + top);
				g.drawArc(x, y, (int) (2 * m * radius), (int) (2 * m * radius), start, sweep);
			}
		}

		return PAGE_EXISTS;
	}

}
