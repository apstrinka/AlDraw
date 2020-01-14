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

import java.util.ArrayList;

public class ParallelLineInputStrategy extends InputStrategy
{
	public ParallelLineInputStrategy(AlDrawController alDrawController)
	{
		super("Find Parallel Line", 3, new String[] { "Select line", "Select line", "Select point" }, alDrawController);
	}

	private Line calculateLine(DPoint p1, DPoint p2)
	{
		return new Line(p1, p2);
	}

	private Line calculateLine(DPoint p1, DPoint p2, DPoint p3)
	{
		Line temp = new Line(p1, p2);
		double slope = temp.getSlope();
		Line newLine = new Line(p3, slope);
		return newLine;
	}

	@Override
	void endHook()
	{
		controller.addLine(calculateLine(controller.getSel(0), controller.getSel(1), controller.getSel(2)));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateLine(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateLine(p1, p2, p3);
		shadows.add(shadow);
		return shadows;
	}
}
