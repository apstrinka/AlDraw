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

public class PerpendicularBisectorInputStrategy extends InputStrategy
{
	public PerpendicularBisectorInputStrategy(AlDrawController alDrawController)
	{
		super("Find Perpendicular Bisector", 2, new String[] { "Select first point", "Select second point" }, alDrawController);
	}

	private DPoint calculatePoint(DPoint p1, DPoint p2)
	{
		return Utils.midpoint(p1, p2);
	}

	private Line calculateLine(DPoint p1, DPoint p2)
	{
		DPoint midpoint = calculatePoint(p1, p2);
		Line temp = new Line(p1, p2);
		double slope = temp.getSlope();
		Line newLine = new Line(midpoint, Utils.perpindicularSlope(slope));
		return newLine;
	}

	@Override
	void endHook()
	{
		DPoint mid = calculatePoint(controller.getSel(0), controller.getSel(1));
		Line line = calculateLine(controller.getSel(0), controller.getSel(1));
		controller.addPerpendicularBisector(mid, line);
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		DPoint p = calculatePoint(p1, p2);
		p.setColor(Color.LIGHT_GRAY);
		shadows.add(p);
		Drawable shadow = calculateLine(p1, p2);
		shadows.add(shadow);
		return shadows;
	}
}
