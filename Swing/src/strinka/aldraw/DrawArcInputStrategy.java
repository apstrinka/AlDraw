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

public class DrawArcInputStrategy extends InputStrategy
{
	public DrawArcInputStrategy(AlDrawController alDrawController)
	{
		super("Draw Arc", 3, new String[] { "Select center", "Select start", "Select end" }, alDrawController);
	}

	private Circle calculateArc(DPoint p1, DPoint p2)
	{
		return new Circle(p1, p2.dist(p1));
	}

	private Arc calculateArc(DPoint p1, DPoint p2, DPoint p3)
	{
		return new Arc(p1, p2, p3);
	}

	@Override
	void endHook()
	{
		controller.addArc(calculateArc(controller.getSel(0), controller.getSel(1), controller.getSel(2)));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateArc(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateArc(p1, p2, p3);
		shadows.add(shadow);
		return shadows;
	}
}
