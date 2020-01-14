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

public class FreePanInputStrategy extends InputStrategy
{
	DPoint oldPoint;

	public FreePanInputStrategy(AlDrawController alDrawController)
	{
		super("Free Pan", 0, new String[] { "Click and drag" }, alDrawController);
	}

	@Override
	void press(int x, int y)
	{
		CoordinateConverter converter = controller.getConverter();
		oldPoint = converter.screenToAbstractCoord(new DPoint(x, y));
	}

	@Override
	void release(int x, int y)
	{
		oldPoint = null;
	}

	@Override
	void click(int x, int y)
	{
		// Do nothing
	}

	@Override
	void drag(int x, int y)
	{
		if (oldPoint != null)
		{
			CoordinateConverter converter = controller.getConverter();
			DPoint diff = converter.screenToAbstractCoord(new DPoint(x, y));
			double xDiff = diff.getX() - oldPoint.getX();
			double yDiff = diff.getY() - oldPoint.getY();
			converter.setCX(converter.getCX() - xDiff);
			converter.setCY(converter.getCY() - yDiff);
			controller.updateView();
		}
	}

	@Override
	void move(int x, int y)
	{
		// Do nothing
	}

	@Override
	void endHook()
	{
		// Do nothing
	}
}
