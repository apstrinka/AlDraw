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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Segment extends AbstractLine
{
	private DPoint p1;
	private DPoint p2;

	public Segment(double x1, double y1, double x2, double y2)
	{
		setP1(new DPoint(x1, y1));
		setP2(new DPoint(x2, y2));
	}

	public Segment(DPoint p1, DPoint p2)
	{
		setP1(p1);
		setP2(p2);
	}// constructor

	public Segment(Segment other)
	{
		setP1(other.getP1());
		setP2(other.getP2());
	}// constructor

	private void setP1(DPoint p1)
	{
		this.p1 = p1;
	}// public void setP1(DPoint p1)Ray

	private void setP2(DPoint p2)
	{
		this.p2 = p2;
	}// public void setP2(DPoint p2)

	public DPoint getP1()
	{
		return p1;
	}// public DPoint getP1()

	public DPoint getP2()
	{
		return p2;
	}// public DPoint getP2()

	@Override
	public double getSlope()
	{
		if (Math.abs(p2.getX() - p1.getX()) < .001)
			return Double.POSITIVE_INFINITY;
		return (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
	}

	@Override
	public double getIntercept() // returns Y intercept, unless vertical, in
									// which case X intercept
	{
		double m = getSlope();
		if (Double.isInfinite(m))
			return p1.getX();
		return p1.getY() - m * p1.getX();
	}

	public double getLength()
	{
		return p1.dist(p2);
	}

	public Segment getPortionInsideRectangle(Segment[] bounds)
	{
		boolean p1InRect = p1.isInRectangle(bounds);
		boolean p2InRect = p2.isInRectangle(bounds);
		if (p1InRect && p2InRect)
			return this;
		ArrayList<DPoint> p = new ArrayList<DPoint>();
		for (int i = 0; i < bounds.length; i++)
		{
			DPoint t = this.intersection(bounds[i]);
			if (t != null && !p.contains(t))
			{
				p.add(t);
			}
		}
		if (p.size() == 0)
			return null;
		else if (p.size() == 2)
			return new Segment(p.get(0), p.get(1));
		else
		{
			DPoint inside = p1;
			if (p2InRect)
				inside = p2;
			return new Segment(inside, p.get(0));
		}
	}
	
	@Override
	public DPoint startPoint()
	{
		return getP1();
	}
	
	@Override
	public double angleAtStart()
	{
		return this.getP1().angle(this.getP2());
	}
	
	@Override
	public double angleAtPoint(DPoint p)
	{
		return this.getP1().angle(this.getP2());
	}
	
	@Override
	public boolean canSwitchSides()
	{
		return true;
	}

	@Override
	public double distance(DPoint p)
	{
		DPoint closest = closestPoint(p);
		return closest.dist(p);
	}

	@Override
	public DPoint closestPoint(DPoint p)
	{
		DPoint closest = super.closestPoint(p);
		if (closest != null)
			return closest;
		closest = getP1();
		if (p.dist(getP1()) > p.dist(getP2()))
			closest = getP2();
		return closest;
	}
	
	@Override
	public double distanceAlong(Pathable p)
	{
		DPoint s = p.startPoint();
		return getP1().dist(s);
	}

	@Override
	public Segment[] divide(DPoint p)
	{
		if (!this.contains(p))
			p = closestPoint(p);
		if (this.getP1().equals(p))
		{
			Segment[] ret = new Segment[1];
			ret[0] = new Segment(p, this.getP2());
			return ret;
		}
		if (this.getP2().equals(p))
		{
			Segment[] ret = new Segment[1];
			ret[0] = new Segment(p, this.getP1());
			return ret;
		}
		Segment[] ret = new Segment[2];
		ret[0] = new Segment(p, this.getP1());
		ret[1] = new Segment(p, this.getP2());
		return ret;
	}
	
	@Override
	public Segment shortened(Pathable p)
	{
		return new Segment(startPoint(), p.startPoint());
	}

	@Override
	public boolean contains(DPoint p)
	{
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p.getX();
		double y2 = p.getY();
		double x3 = p2.getX();
		double y3 = p2.getY();
		double m = this.getSlope();
		boolean isOn = (m == Double.POSITIVE_INFINITY && Math.abs(x2 - x1) < .001) || (Math.abs(y2 - y1 - m * (x2 - x1)) < .001);
		isOn = isOn && Utils.isBetween(x2, x1, x3) && Utils.isBetween(y2, y1, y3);
		return isOn;
	}

	public boolean isIn(Segment other)
	{
		return other.contains(getP1()) && other.contains(getP2());
		// return this.same(other) && DPoint.isBetween(p1, other.getP1(),
		// other.getP2()) && DPoint.isBetween(p2, other.getP1(), other.getP2());
	}

	@Override
	public Segment copy(DPoint copy, DPoint paste)
	{
		DPoint s1 = p1.copy(copy, paste);
		DPoint s2 = p2.copy(copy, paste);
		return new Segment(s1, s2);
	}

	@Override
	public Segment copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		DPoint s1 = p1.copy(copy1, copy2, paste1, paste2);
		DPoint s2 = p2.copy(copy1, copy2, paste1, paste2);
		return new Segment(s1, s2);
	}

	@Override
	public void addToState(AlDrawState state)
	{
		state.addSegmentWithIntersections(this);
	}
	
//	public Line2D.Double getJavaGeom(CoordinateConverter c)
//	{
//		DPoint p1 = c.abstractToScreenCoord(getP1());
//		DPoint p2 = c.abstractToScreenCoord(getP2());
//		int x1 = (int)Math.round(p1.getX());
//		int y1 = (int)Math.round(p1.getY());
//		int x2 = (int)Math.round(p2.getX());
//		int y2 = (int)Math.round(p2.getY());
//		return new Line2D.Double(x1, y1, x2, y2);
//	}

	@Override
	public void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context)
	{
		DPoint q1 = c.abstractToScreenCoord(getP1());
		DPoint q2 = c.abstractToScreenCoord(getP2());
		
		canvas.drawLine((float)q1.getX(), (float)q1.getY(), (float)q2.getX(), (float)q2.getY(), paint);
	}
	
	@Override
	public boolean sameDirection(Pathable p)
	{
		if (!(p instanceof Segment))
			return false;
		Segment s = (Segment)p;
		return this.getP1().equals(s.getP1()) && this.getP2().equals(s.getP2());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment other = (Segment) obj;
		return this.equals(other);
	}

	public boolean equals(Segment other)
	{
		return ((p1.equals(other.getP1())) && (p2.equals(other.getP2()))) || ((p1.equals(other.getP2())) && (p2.equals(other.getP1())));
	}

	@Override
	public String toString()
	{
		return "Segment: " + this.getP1().toString() + ", " + this.getP2().toString();
	}
}