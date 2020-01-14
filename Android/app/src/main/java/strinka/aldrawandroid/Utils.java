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

public class Utils
{
	private static double defaultTolerance = .001;

	private Utils()
	{
	}

	public static boolean isBetween(double middle, double a, double b)
	{
		return (middle <= a + .001 && middle >= b - .001) || (middle >= a - .001 && middle <= b + .001);
	}

	public static boolean doublesEqual(double a, double b)
	{
		return doublesEqual(a, b, defaultTolerance);
	}

	public static boolean doublesEqual(double a, double b, double tolerance)
	{
		boolean ret = false;
		if (Double.isInfinite(a) && Double.isInfinite(b))
			ret = true;
		if (Math.abs(a - b) < tolerance)
			ret = true;
		return ret;
	}

	public static boolean areColinear(DPoint p1, DPoint p2, DPoint p3)
	{
		DPoint[] points = new DPoint[3];
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
		return areColinear(points);
	}

	public static boolean areColinear(DPoint[] points)
	{
		if (points.length < 3)
			return true;
		Line line = new Line(points[0], points[1]);
		for (int i = 2; i < points.length; i++)
		{
			if (!line.contains(points[i]))
				return false;
		}
		return true;
	}

	public static DPoint midpoint(DPoint p1, DPoint p2)
	{
		double x = (p1.getX() + p2.getX()) / 2;
		double y = (p1.getY() + p2.getY()) / 2;
		return new DPoint(x, y);
	}

	public static double perpindicularSlope(double slope)
	{
		double ret = Double.POSITIVE_INFINITY;
		if (!Utils.doublesEqual(slope, 0))
			ret = -1 / slope;
		return ret;
	}

	// Returns 1 if the point is above the line, -1 if below, and 0 if on.
	// For vertical lines, a higher x coordinate is considered above.
	public static int comparePointToLine(DPoint point, AbstractLine line)
	{
		double slope = line.getSlope();
		double intercept = line.getIntercept();
		int ret = 0;
		if (Double.isInfinite(slope))
		{
			double pX = point.getX();
			if (Utils.doublesEqual(pX, intercept))
				ret = 0;
			else if (pX > intercept)
				ret = 1;
			else
				ret = -1;
		} else
		{
			double pY = point.getY();
			double lY = slope * point.getX() + intercept;
			if (Utils.doublesEqual(pY, lY))
				ret = 0;
			else if (pY > lY)
				ret = 1;
			else
				ret = -1;
		}
		return ret;
	}

	public static double normalizeAngle(double a)
	{
		double b = Math.IEEEremainder(a, 2 * Math.PI);
		if (b < 0)
			b += 2 * Math.PI;
		if (doublesEqual(b, 2 * Math.PI))
			b = 0;
		return b;
	}

	public static double angleSum(double a, double b)
	{
		double c = a + b;
		c = normalizeAngle(c);
		return c;
	}

	public static double angleDifference(double a, double b)
	{
		double c = a - b;
		c = normalizeAngle(c);
		return c;
	}

	public static double smallerAngleDifference(double a, double b)
	{
		return Math.min(angleDifference(a, b), angleDifference(b, a));
	}

	public static double oppositeAngle(double a)
	{
		return angleSum(a, Math.PI);
	}

	public static double angleToSlope(double a)
	{
		double s;
		s = Math.tan(a);
		if (Utils.doublesEqual(a, Math.PI/2) || Utils.doublesEqual(a, 3*Math.PI/2))
			s = Double.POSITIVE_INFINITY;
		return s;
	}

	public static double slopeToAngle(double s)
	{
		double a = Math.atan(s);
		a = normalizeAngle(a);
		return a;
	}
	
	public static int darkerColor(int color)
	{
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		r = r * 4 / 5;
		g = g * 4 / 5;
		b = b * 4 / 5;
		return Color.rgb(r, g, b);
	}
}
