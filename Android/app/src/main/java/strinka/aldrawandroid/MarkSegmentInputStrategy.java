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

import java.util.ArrayList;

public class MarkSegmentInputStrategy extends InputStrategy
{
	public MarkSegmentInputStrategy(AlDrawController alDrawController)
	{
		super("Draw Mark Line", 2, alDrawController);
	}

	private DPoint calculatePoint(DPoint p1, DPoint p2)
	{
		double angle = p1.angle(p2);
		double dist = controller.getMark();
		return new DPoint(p1.getX() + dist * Math.cos(angle), p1.getY() + dist * Math.sin(angle));
	}

	private Segment calculateMarkLine(DPoint p1, DPoint p2)
	{
		DPoint end = calculatePoint(p1, p2);
		return new Segment(p1, end);
	}

	@Override
	void endHook()
	{
		controller.addMarkLine(calculateMarkLine(controller.getSelPoint(0), controller.getSelPoint(1)), calculatePoint(controller.getSelPoint(0), controller.getSelPoint(1)));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateMarkLine(p1, p2);
		shadows.add(shadow);
		return shadows;
	}
}
