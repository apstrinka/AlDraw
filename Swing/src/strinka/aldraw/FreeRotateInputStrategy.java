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

public class FreeRotateInputStrategy extends InputStrategy
{
	double oldAngle;

	public FreeRotateInputStrategy(AlDrawController alDrawController)
	{
		super("Free Rotate", 0, new String[] { "Click and drag" }, alDrawController);
	}

	@Override
	void press(int x, int y)
	{
		CoordinateConverter converter = controller.getConverter();
		oldAngle = new DPoint(converter.getCX(), converter.getCY()).angle(converter.screenToAbstractCoord(new DPoint(x, y)));
	}

	@Override
	void release(int x, int y)
	{
		// Do nothing
	}

	@Override
	void click(int x, int y)
	{
		// Do nothing
	}

	@Override
	void drag(int x, int y)
	{
		CoordinateConverter converter = controller.getConverter();
		double newAngle = new DPoint(converter.getCX(), converter.getCY()).angle(converter.screenToAbstractCoord(new DPoint(x, y)));
		double diff = newAngle - oldAngle;
		converter.setAngle(Utils.angleSum(converter.getAngle(), diff));
		controller.updateView();
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
