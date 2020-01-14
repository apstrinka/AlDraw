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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class AbstractLine extends Pathable implements Selectable
{
	public abstract double getSlope();

	public abstract double getIntercept();

	@Override
	public abstract boolean contains(DPoint p);

	@Override
	public double distance(DPoint p)
	{
		double m = this.getSlope();
		double b = this.getIntercept();
		double x = p.getX();
		double y = p.getY();
		double dist = 0;
		if (Double.isInfinite(m))
		{
			dist = Math.abs(x - b);
		} else
		{
			dist = Math.abs(y - m * x - b) / Math.hypot(m, 1); 
			// hypot is sqrt(m^2 + 1^2)
		}
		return dist;
	}
	
	@Override
	public double curvature()
	{
		return 0;
	}

	@Override
	public DPoint closestPoint(DPoint p)
	{
		double slope = 0;
		if (Utils.doublesEqual(this.getSlope(), 0))
			slope = Double.POSITIVE_INFINITY;
		else if (!Double.isInfinite(this.getSlope()))
			slope = -1 / this.getSlope();
		Line other = new Line(p, slope);
		return this.intersection(other);
	}
	
	@Override
	public DPoint[] intersection(Intersectable other)
	{
		if (other instanceof AbstractLine)
		{
			DPoint p = intersection((AbstractLine)other);
			if (p==null)
				return new DPoint[0];
			DPoint[] ret = new DPoint[1];
			ret[0] = p;
			return ret;
		}
		else if (other instanceof Circle)
		{
			return intersection((Circle)other);
		}
		else
		{
			return new DPoint[0];
		}
	}

	public DPoint intersection(AbstractLine other)
	{
		double m1 = this.getSlope();
		double b1 = this.getIntercept();
		double m2 = other.getSlope();
		double b2 = other.getIntercept();
		if (Utils.doublesEqual(m1, m2))
			return null;
		double x, y;
		if (Double.isInfinite(m1))
		{
			x = b1;
			y = m2 * x + b2;
		} else if (Double.isInfinite(m2))
		{
			x = b2;
			y = m1 * x + b1;
		} else
		{
			x = (b2 - b1) / (m1 - m2);
			y = m1 * x + b1;
		}
		DPoint ret = new DPoint(x, y);
		if (this.contains(ret) && other.contains(ret))
			return ret;
		else
			return null;
	}

	public DPoint[] intersection(Circle other)
	{
		double[] x = new double[2];
		double[] y = new double[2];
		DPoint[] ret = new DPoint[2];
		double xc = other.getCenter().getX();
		double yc = other.getCenter().getY();
		double r = other.getRadius();
		double m = this.getSlope();
		double intercept = this.getIntercept();
		if (Utils.doublesEqual(distance(other.getCenter()), other.getRadius()))
		{
			DPoint tan = closestPoint(other.getCenter());
			if (!other.contains(tan))
				return new DPoint[0];
			ret = new DPoint[1];
			ret[0] = tan;
			return ret;
		}
		if (Double.isInfinite(m))
		{
			if (intercept < xc - r || intercept > xc + r)
				return new DPoint[0];
			x[0] = intercept;
			x[1] = intercept;
			y[0] = yc + Math.sqrt(Math.pow(r, 2) - Math.pow(x[0] - xc, 2));
			y[1] = yc - Math.sqrt(Math.pow(r, 2) - Math.pow(x[1] - xc, 2));
		} else
		{
			double a = 1 + Math.pow(m, 2);
			double b = 2 * (m * intercept - xc - m * yc);
			double c = Math.pow(xc, 2) + Math.pow(intercept, 2) - 2 * intercept * yc + Math.pow(yc, 2) - Math.pow(r, 2);
			double d = Math.pow(b, 2) - 4 * a * c;
			if (d < 0)
				return new DPoint[0];
			x[0] = (-b + Math.sqrt(d)) / (2 * a);
			x[1] = (-b - Math.sqrt(d)) / (2 * a);
			y[0] = m * x[0] + intercept;
			y[1] = m * x[1] + intercept;
		}
		for (int i = 0; i < 2; i++)
		{
			ret[i] = new DPoint(x[i], y[i]);
			if (!this.contains(ret[i]) || !other.contains(ret[i]))
				ret[i] = null;
		}
		return ret;
	}

	public boolean parallel(AbstractLine other)
	{
		return Utils.doublesEqual(this.getSlope(), other.getSlope());
	}

	public boolean same(AbstractLine other)
	{
		return (Utils.doublesEqual(this.getIntercept(), other.getIntercept()) && Utils.doublesEqual(this.getSlope(), other.getSlope()));
	}

	@Override
	public abstract void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context);

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractLine other = (AbstractLine) obj;
		return same(other);
	}
}
