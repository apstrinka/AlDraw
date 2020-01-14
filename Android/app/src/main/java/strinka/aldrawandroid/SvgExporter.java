/*
 *  AlDraw - A tool for creating geometrical constructions
 *  Copyright (C) 2010  Alex Strinka
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package strinka.aldrawandroid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.graphics.Color;

public class SvgExporter implements FileChooserDialog.OnFileChosenListener
{
	AlDrawController controller;
	
	public SvgExporter(AlDrawController controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void fileChosen(File file) {
		try {
			exportSVG(file, controller.getState(), controller.getConverter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exportSVG(File file, AlDrawState currentState, CoordinateConverter converter) throws IOException
	{
		FileWriter fw = new FileWriter(file);
		int width = converter.getWidth();
		int height = converter.getHeight();
		fw.write("<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"" + width + "px\" height=\"" + height + "px\">\n");

		//Write Enclosures
		for (int i = 0; i < currentState.getNumEnclosures(); i++)
		{
			Enclosure e = currentState.getEnclosure(i);
			fw.write("<path d=\"");
			DPoint p = converter.abstractToScreenCoord(e.getPathable(0).startPoint());
			fw.write("M " + p.getX() + " " + p.getY());
			for (int j = 0; j < e.getNumPathables(); j++)
			{
				Pathable pathable = e.getPathable(j);
				if (pathable instanceof Segment)
				{
					Segment segment = (Segment)pathable;
					DPoint end = converter.abstractToScreenCoord(segment.getP2());
					fw.write(" L " + end.getX() + " " + end.getY());
				}
				else
				{
					Arc arc = (Arc)pathable;
					if (Utils.doublesEqual(arc.getSweep(), 2*Math.PI))
					{
						DPoint mid = converter.abstractToScreenCoord(arc.getPointAtAngle(Utils.angleSum(arc.getStart(), Math.PI)));
						DPoint end = converter.abstractToScreenCoord(arc.getPointAtAngle(arc.getStart()));
						double radius = converter.abstractToScreenDist(arc.getRadius());
						int sweepflag = 1;
						if (arc.isInverse())
							sweepflag = 0;
						fw.write(" A " + radius + " " + radius + " 0 0 " + sweepflag + " " + mid.getX() + " " + mid.getY());
						fw.write(" A " + radius + " " + radius + " 0 1 " + sweepflag + " " + end.getX() + " " + end.getY());
					}
					else
					{
						DPoint end = converter.abstractToScreenCoord(arc.getPointAtAngle(arc.getEnd()));
						double radius = converter.abstractToScreenDist(arc.getRadius());
						int largearc = 0;
						if (arc.getSweep() > Math.PI)
							largearc = 1;
						int sweepflag = 1;
						if (arc.isInverse())
						{
							end = converter.abstractToScreenCoord(arc.getPointAtAngle(arc.getStart()));
							sweepflag = 0;
						}
						fw.write(" A " + radius + " " + radius + " 0 " + largearc + " " + sweepflag + " " + end.getX() + " " + end.getY());
					}
				}
			}
			fw.write("\" fill=\"rgb(" + Color.red(e.getColor()) + "," + Color.green(e.getColor()) + "," + Color.blue(e.getColor()) +")\" ");
			fw.write("fill-rule=\"evenodd\" stroke=\"none\"/>\n");
		}
		
		// Write segments
		for (int i = 0; i < currentState.getNumSegments(); i++)
		{
			Segment l = currentState.getSegment(i);
			DPoint p1 = converter.abstractToScreenCoord(l.getP1());
			DPoint p2 = converter.abstractToScreenCoord(l.getP2());
			fw.write("<line x1=\"" + (p1.getX()) + "\" y1=\"" + (p1.getY()) + "\" x2=\"" + (p2.getX()) + "\" y2=\"" + (p2.getY()) + "\" style=\"stroke:#000000;\"/>\n");
		}

		// Write rays
		for (int i = 0; i < currentState.getNumRays(); i++)
		{
			Ray r = currentState.getRay(i);
			Segment s = r.getPortionInsideRectangle(converter.getAbstractBoundaries());
			if (s != null)
			{
				DPoint p1 = converter.abstractToScreenCoord(s.getP1());
				DPoint p2 = converter.abstractToScreenCoord(s.getP2());
				fw.write("<line x1=\"" + (p1.getX()) + "\" y1=\"" + (p1.getY()) + "\" x2=\"" + (p2.getX()) + "\" y2=\"" + (p2.getY()) + "\" style=\"stroke:#000000;\"/>\n");
			}
		}

		// Write lines
		for (int i = 0; i < currentState.getNumLines(); i++)
		{
			Line l = currentState.getLine(i);
			Segment s = l.getPortionInsideRectangle(converter.getAbstractBoundaries());
			if (s != null)
			{
				DPoint p1 = converter.abstractToScreenCoord(s.getP1());
				DPoint p2 = converter.abstractToScreenCoord(s.getP2());
				fw.write("<line x1=\"" + (p1.getX()) + "\" y1=\"" + (p1.getY()) + "\" x2=\"" + (p2.getX()) + "\" y2=\"" + (p2.getY()) + "\" style=\"stroke:#000000;\"/>\n");
			}
		}

		// Write arcs
		for (int i = 0; i < currentState.getNumArcs(); i++)
		{
			Arc a = currentState.getArc(i);
			DPoint start = converter.abstractToScreenCoord(new DPoint(a.getCenter(), new DPoint(a.getRadius() * Math.cos(a.getStart()), a.getRadius() * Math.sin(a.getStart()))));
			DPoint end = converter.abstractToScreenCoord(new DPoint(a.getCenter(), new DPoint(a.getRadius() * Math.cos(a.getEnd()), a.getRadius() * Math.sin(a.getEnd()))));
			double radius = converter.abstractToScreenDist(a.getRadius());
			int largearc = 0;
			if (a.getSweep() > Math.PI)
				largearc = 1;
			fw.write("<path d=\"M" + start.getX() + " " + start.getY() + " A" + radius + " " + radius + " 0 " + largearc + " 1 " + end.getX() + " " + end.getY() + "\" style=\"stroke:#000000; fill:none\"/>\n");
		}

		// Write circles
		for (int i = 0; i < currentState.getNumCircles(); i++)
		{
			Circle c = currentState.getCircle(i);
			DPoint center = converter.abstractToScreenCoord(c.getCenter());
			double radius = converter.abstractToScreenDist(c.getRadius());
			fw.write("<circle cx=\"" + center.getX() + "\" cy=\"" + center.getY() + "\" r=\"" + radius + "\" style=\"stroke:#000000; fill:none\"/>\n");
		}

		fw.write("</svg>");
		fw.close();
	}
}
