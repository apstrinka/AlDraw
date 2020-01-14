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

public class TrisectLineInputStrategy extends InputStrategy
{
	public TrisectLineInputStrategy(AlDrawController alDrawController)
	{
		super("Trisect Line", 2, new String[] { "Select first point", "Select second point" }, alDrawController);
	}

	private DPoint calculateFirstPoint(DPoint p1, DPoint p2)
	{
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double newX = (p1.getX() + dx / 3);
		double newY = (p1.getY() + dy / 3);
		return new DPoint(newX, newY);
	}

	private DPoint calculateSecondPoint(DPoint p1, DPoint p2)
	{
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		double newX = (p1.getX() + 2 * dx / 3);
		double newY = (p1.getY() + 2 * dy / 3);
		return new DPoint(newX, newY);
	}

	@Override
	void endHook()
	{
		DPoint[] newPoints = new DPoint[2];
		newPoints[0] = calculateFirstPoint(controller.getSel(0), controller.getSel(1));
		newPoints[1] = calculateSecondPoint(controller.getSel(0), controller.getSel(1));
		controller.addPoints(newPoints);
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		DPoint shadow = calculateFirstPoint(p1, p2);
		shadow.setColor(Color.LIGHT_GRAY);
		shadows.add(shadow);
		shadow = calculateSecondPoint(p1, p2);
		shadow.setColor(Color.LIGHT_GRAY);
		shadows.add(shadow);
		return shadows;
	}
}
