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

public class SelectPointInputStrategy extends InputStrategy
{
	public SelectPointInputStrategy(AlDrawController alDrawController)
	{
		super("Select Point", 1, new String[] { "Select point" }, alDrawController);
	}

	@Override
	void endHook()
	{
		controller.addSelected(controller.getSel(0));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = p1;
		shadows.add(shadow);
		return shadows;
	}
}
