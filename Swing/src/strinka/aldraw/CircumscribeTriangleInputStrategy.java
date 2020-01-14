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

package strinka.aldraw;

import java.awt.Color;
import java.util.ArrayList;

public class CircumscribeTriangleInputStrategy extends InputStrategy
{
	public CircumscribeTriangleInputStrategy(AlDrawController alDrawController)
	{
		super("Circumscribe Triangle", 3, new String[] { "Select first point", "Select second point", "Select third point" }, alDrawController);
	}

	private DPoint calculateCenter(DPoint p1, DPoint p2, DPoint p3)
	{
		Line l1 = new Line(p1, p2);
		l1 = new Line(Utils.midpoint(p1, p2), Utils.perpindicularSlope(l1.getSlope()));
		Line l2 = new Line(p2, p3);
		l2 = new Line(Utils.midpoint(p2, p3), Utils.perpindicularSlope(l2.getSlope()));
		return l1.intersection(l2);

	}

	private Circle calculateCircle(DPoint p1, DPoint p2)
	{
		DPoint midpoint = Utils.midpoint(p1, p2);
		return new Circle(midpoint, midpoint.dist(p1));
	}

	private Circle calculateCircle(DPoint p1, DPoint p2, DPoint p3)
	{
		DPoint center = calculateCenter(p1, p2, p3);
		if (center == null)
			return null;
		return new Circle(center, center.dist(p1));
	}

	@Override
	void endHook()
	{
		DPoint center = calculateCenter(controller.getSel(0), controller.getSel(1), controller.getSel(2));
		if (center != null)
		{
			Circle circle = calculateCircle(controller.getSel(0), controller.getSel(1), controller.getSel(2));
			controller.addPointAndCircle(center, circle);
		}
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateCircle(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		DPoint shadow = calculateCenter(p1, p2, p3);
		if (shadow != null)
		{
			shadow.setColor(Color.LIGHT_GRAY);
			shadows.add(shadow);
			shadows.add(calculateCircle(p1, p2, p3));
		}
		return shadows;
	}
}
