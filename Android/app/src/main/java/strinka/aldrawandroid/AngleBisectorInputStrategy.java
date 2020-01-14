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

import android.graphics.Color;

import java.util.ArrayList;

public class AngleBisectorInputStrategy extends InputStrategy
{
	public AngleBisectorInputStrategy(AlDrawController alDrawController)
	{
		super("Angle Bisector", 3, alDrawController);
	}

	private Line calculateLine(DPoint p1, DPoint p2, DPoint p3)
	{
		double angle1 = p2.angle(p1);
		double angle2 = p2.angle(p3);
		double angle3 = (angle1 + angle2)/2;
		double slope = Utils.angleToSlope(angle3);
		return new Line(p2, slope);
	}

	@Override
	void endHook()
	{
		Line line = calculateLine(controller.getSelPoint(0), controller.getSelPoint(1), controller.getSelPoint(2));
		controller.addLine(line);
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Line(p1, p2);
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
