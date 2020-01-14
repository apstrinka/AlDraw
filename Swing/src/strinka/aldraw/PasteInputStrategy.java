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

public class PasteInputStrategy extends InputStrategy
{
	public PasteInputStrategy(AlDrawController alDrawController)
	{
		super("Paste", 2, new String[] { "Select first point", "Select second point" }, alDrawController);
	}

	@Override
	void endHook()
	{
		if (controller.getNumCopy() > 1)
		{
			ArrayList<Selectable> selected = controller.getSelected();
			AlDrawState newState = new AlDrawState(controller.getCurrentState());
			for (int i = 0; i < selected.size(); i++)
			{
				Selectable s = selected.get(i);
				s = s.copy(controller.getCopy(0), controller.getCopy(1), controller.getSel(0), controller.getSel(1));
				s.addToState(newState);
			}
			controller.clearSelPoints();
			controller.addState(newState);
			controller.updateView();
		}
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1)
	{
		if (controller.getNumCopy() > 1)
		{
			ArrayList<Drawable> shadows = new ArrayList<Drawable>();
			ArrayList<Selectable> selected = controller.getSelected();
			for (int i = 0; i < selected.size(); i++)
			{
				Selectable s = selected.get(i);
				shadows.add(s.copy(controller.getCopy(0), p1));
			}
			return shadows;
		}
		return new ArrayList<Drawable>();
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		if (controller.getNumCopy() > 1)
		{
			ArrayList<Drawable> shadows = new ArrayList<Drawable>();
			ArrayList<Selectable> selected = controller.getSelected();
			for (int i = 0; i < selected.size(); i++)
			{
				Selectable s = selected.get(i);
				shadows.add(s.copy(controller.getCopy(0), controller.getCopy(1), p1, p2));
			}
			return shadows;
		}
		return new ArrayList<Drawable>();
	}
}
