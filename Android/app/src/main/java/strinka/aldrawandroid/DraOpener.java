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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Color;

public class DraOpener implements FileChooserDialog.OnFileChosenListener
{
	AlDrawController controller;
	
	public DraOpener(AlDrawController controller)
	{
		this.controller = controller;
	}
	
	@Override
	public void fileChosen(File file) {
		try {
			AlDrawState state = open(file, controller.getConverter());
			if (state != null){
				controller.clearSelPoints();
				controller.clearSelected();
				controller.addState(state);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AlDrawState open(File file, CoordinateConverter converter) throws IOException
	{
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		AlDrawState ret = null;

		if (in.readChar() == 'D' && in.readChar() == 'r' && in.readChar() == 'a')
		{
			char w = in.readChar();
			if (w == 'w')
			{
				ret = openVersion0(in, converter);
			} else if (w == 'v')
			{
				int v = in.readInt();
				if (v == 1)
				{
					ret = openVersion1(in, converter);
				}
				else if (v == 2)
				{
					ret = openVersion2(in, converter);
				}
			}
		}
		// TODO: Make this a nicer error message
		if (ret == null)
			System.out.println("File not proper format.");
		return ret;

	}

	private AlDrawState openVersion0(DataInputStream in, CoordinateConverter converter) throws IOException
	{
		AlDrawState state = new AlDrawState(controller);

		// Load CoordinateConverter info
		converter.setDefaults();
		converter.setCX(in.readDouble());
		converter.setCY(in.readDouble());
		converter.setConversionRatio(converter.getWidth() / in.readDouble());
		converter.setAngle(0);

		// Load mark distance
		state.setMark(in.readDouble());

		// Load Segments
		int num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x1 = in.readDouble();
			double y1 = in.readDouble();
			double x2 = in.readDouble();
			double y2 = in.readDouble();
			state.addSegmentWithIntersections(new Segment(x1, y1, x2, y2));
		}

		// Load Rays
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			double slope = in.readDouble();
			boolean r = in.readBoolean();
			double angle = Utils.slopeToAngle(slope);
			if (!r && !Double.isInfinite(angle))
				angle = Utils.oppositeAngle(angle);
			state.addRayWithIntersections(new Ray(new DPoint(x, y), angle));
		}

		// Load Arcs
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			double rad = in.readDouble();
			if (Utils.doublesEqual(rad, 0))
				continue;
			double start = in.readDouble();
			double end = in.readDouble();
			double sweep = Utils.angleDifference(end, start);
			if (Utils.doublesEqual(sweep, 0) || Utils.doublesEqual(sweep, 2 * Math.PI))
				state.addCircleWithIntersections(new Circle(new DPoint(x, y), rad));
			else
				state.addArcWithIntersections(new Arc(new DPoint(x, y), rad, start, sweep));
		}

		// Load Points
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			state.addPoint(new DPoint(x, y));
		}

		return state;
	}

	private AlDrawState openVersion1(DataInputStream in, CoordinateConverter converter) throws IOException
	{
		AlDrawState state = new AlDrawState(controller);

		// Load CoordinateConverter info
		converter.setCX(in.readDouble());
		converter.setCY(in.readDouble());
		converter.setConversionRatio(in.readDouble());
		converter.setAngle(in.readDouble());

		// Load mark distance
		state.setMark(in.readDouble());

		// Load Segments
		int num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x1 = in.readDouble();
			double y1 = in.readDouble();
			double x2 = in.readDouble();
			double y2 = in.readDouble();
			state.addSegmentWithoutIntersections(new Segment(x1, y1, x2, y2));
		}

		// Load Rays
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			double angle = in.readDouble();
			state.addRayWithoutIntersections(new Ray(new DPoint(x, y), angle));
		}

		// Load Lines
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double slope = in.readDouble();
			double intercept = in.readDouble();
			state.addLineWithoutIntersections(new Line(slope, intercept));
		}

		// Load Arcs
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			double rad = in.readDouble();
			if (Utils.doublesEqual(rad, 0))
				continue;
			double start = in.readDouble();
			double sweep = in.readDouble();
			state.addArcWithoutIntersections(new Arc(new DPoint(x, y), rad, start, sweep));
		}

		// Load Circles
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			double rad = in.readDouble();
			if (Utils.doublesEqual(rad, 0))
				continue;
			state.addCircleWithoutIntersections(new Circle(new DPoint(x, y), rad));
		}

		// Load Points
		num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			double x = in.readDouble();
			double y = in.readDouble();
			state.addPoint(new DPoint(x, y));
		}

		return state;
	}
	
	private AlDrawState openVersion2(DataInputStream in, CoordinateConverter converter) throws IOException
	{
		AlDrawState state = openVersion1(in, converter);

		// Load Enclosures
		int numEnclosures = in.readInt();
		for (int i = 0; i < numEnclosures; i++)
		{
			Enclosure e = readEnclosure(in);
			state.addEnclosure(e);
		}

		return state;
	}
	
	private static Enclosure readEnclosure(DataInputStream in) throws IOException
	{
		int r = in.readInt();
		int g = in.readInt();
		int b = in.readInt();
		int color = Color.rgb(r, g, b);
		ArrayList<Pathable> list = new ArrayList<Pathable>();
		int num = in.readInt();
		for (int i = 0; i < num; i++)
		{
			boolean isSegment = in.readBoolean();
			if (isSegment)
			{
				double x1 = in.readDouble();
				double y1 = in.readDouble();
				double x2 = in.readDouble();
				double y2 = in.readDouble();
				list.add(new Segment(x1, y1, x2, y2));
			}
			else //is Arc
			{
				double x = in.readDouble();
				double y = in.readDouble();
				double rad = in.readDouble();
				double start = in.readDouble();
				double sweep = in.readDouble();
				boolean inverse = in.readBoolean();
				list.add(new Arc(new DPoint(x, y), rad, start, sweep, inverse));
			}
		}
		return new Enclosure(list, color);
	}
}