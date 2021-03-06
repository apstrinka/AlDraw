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

public class SelectSegmentInputStrategy extends InputStrategy
{
	public SelectSegmentInputStrategy(AlDrawController alDrawController)
	{
		super("Select Segment", 2, new String[] { "Select start", "Select end" }, alDrawController);
	}

	private Segment calculateLine(DPoint p1, DPoint p2)
	{
		return new Segment(p1, p2);
	}

	@Override
	void endHook()
	{
		controller.addSelected(calculateLine(controller.getSel(0), controller.getSel(1)));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = calculateLine(p1, p2);
		shadows.add(shadow);
		return shadows;
	}
}
