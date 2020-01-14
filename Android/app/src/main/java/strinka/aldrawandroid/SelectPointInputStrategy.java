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

import android.graphics.Color;

public class SelectPointInputStrategy extends InputStrategy
{
	public SelectPointInputStrategy(AlDrawController controller)
	{
		super("Select Point", 1, controller);
	}

	@Override
	void endHook()
	{
		controller.addSelected(controller.getSelPoint(0));
	}

	@Override
	ArrayList<Drawable> shadowHook(DPoint p1)
	{
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		DPoint shadow = p1;
		p1.setColor(Color.LTGRAY);
		shadows.add(shadow);
		return shadows;
	}
}
