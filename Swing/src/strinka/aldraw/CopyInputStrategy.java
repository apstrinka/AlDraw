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

public class CopyInputStrategy extends InputStrategy
{
	public CopyInputStrategy(AlDrawController alDrawController)
	{
		super("Copy", 2, new String[] { "Select first point", "Select second point" }, alDrawController);
	}

	@Override
	void endHook()
	{
		DPoint[] copy = new DPoint[2];
		copy[0] = controller.getSel(0);
		copy[1] = controller.getSel(1);
		controller.setCopy(copy);
	}
}
