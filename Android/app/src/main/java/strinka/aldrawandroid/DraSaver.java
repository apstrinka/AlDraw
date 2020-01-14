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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Color;

public class DraSaver implements FileChooserDialog.OnFileChosenListener
{
	AlDrawController controller;
	
	public DraSaver(AlDrawController controller)
	{
		this.controller = controller;
	}

	@Override
	public void fileChosen(File file) {
		try {
			save(file, controller.getState(), controller.getConverter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void save(File file, AlDrawState currentState, CoordinateConverter converter) throws IOException
	{
		DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

		// Write version info
		out.writeChars("Drav");
		out.writeInt(2);

		// Write CoordinateConverter info
		out.writeDouble(converter.getCX());
		out.writeDouble(converter.getCY());
		out.writeDouble(converter.getConversionRatio());
		out.writeDouble(converter.getAngle());

		// Write mark distance
		out.writeDouble(currentState.getMark());

		// Write Segments
		int num = currentState.getNumSegments();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Segment l = currentState.getSegment(i);
			out.writeDouble(l.getP1().getX());
			out.writeDouble(l.getP1().getY());
			out.writeDouble(l.getP2().getX());
			out.writeDouble(l.getP2().getY());
		}

		// Write Rays
		num = currentState.getNumRays();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Ray r = currentState.getRay(i);
			out.writeDouble(r.getStart().getX());
			out.writeDouble(r.getStart().getY());
			out.writeDouble(r.getAngle());
		}

		// Write Lines
		num = currentState.getNumLines();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Line l = currentState.getLine(i);
			out.writeDouble(l.getSlope());
			out.writeDouble(l.getIntercept());
		}

		// Write Arcs
		num = currentState.getNumArcs();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Arc a = currentState.getArc(i);
			out.writeDouble(a.getCenter().getX());
			out.writeDouble(a.getCenter().getY());
			out.writeDouble(a.getRadius());
			out.writeDouble(a.getStart());
			out.writeDouble(a.getSweep());
		}

		// Write Circles
		num = currentState.getNumCircles();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Circle a = currentState.getCircle(i);
			out.writeDouble(a.getCenter().getX());
			out.writeDouble(a.getCenter().getY());
			out.writeDouble(a.getRadius());
		}

		// Write Points
		num = currentState.getNumPoints();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			DPoint p = currentState.getPoint(i);
			out.writeDouble(p.getX());
			out.writeDouble(p.getY());
		}
		
		//Write Enclosures
		num = currentState.getNumEnclosures();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			writeEnclosure(currentState.getEnclosure(i), out);
		}
		
		out.flush();
		out.close();
	}
	
	private static void writeEnclosure(Enclosure enclosure, DataOutputStream out) throws IOException
	{
		out.writeInt(Color.red(enclosure.getColor()));
		out.writeInt(Color.green(enclosure.getColor()));
		out.writeInt(Color.blue(enclosure.getColor()));
		int num = enclosure.getNumPathables();
		out.writeInt(num);
		for (int i = 0; i < num; i++)
		{
			Pathable p = enclosure.getPathable(i);
			if (p instanceof Segment)
			{
				out.writeBoolean(true);
				Segment s = (Segment)p;
				out.writeDouble(s.getP1().getX());
				out.writeDouble(s.getP1().getY());
				out.writeDouble(s.getP2().getX());
				out.writeDouble(s.getP2().getY());
			}
			else //p is Arc
			{
				out.writeBoolean(false);
				Arc a = (Arc)p;
				out.writeDouble(a.getCenter().getX());
				out.writeDouble(a.getCenter().getY());
				out.writeDouble(a.getRadius());
				out.writeDouble(a.getStart());
				out.writeDouble(a.getSweep());
				out.writeBoolean(a.isInverse());
			}
		}
	}
}
