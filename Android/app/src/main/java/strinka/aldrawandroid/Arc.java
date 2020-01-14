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
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Arc extends Circle
{
	private double start;
	private double sweep;
	private boolean inverse;

	public Arc(DPoint center, double radius, double start, double sweep)
	{
		super(center, radius);
		setStart(start);
		setSweep(sweep);
		setInverse(false);
	}
	
	public Arc(DPoint center, double radius, double start, double sweep, boolean inverse)
	{
		super(center, radius);
		setStart(start);
		setSweep(sweep);
		setInverse(inverse);
	}

	public Arc(DPoint center, DPoint start, DPoint end)
	{
		super(center, center.dist(start));
		setStart(center.angle(start));
		setSweep(Utils.angleDifference(center.angle(end), center.angle(start)));
		setInverse(false);
	}

	public Arc(Arc other)
	{
		super(other.getCenter(), other.getRadius());
		setStart(other.getStart());
		setSweep(other.getSweep());
		setInverse(other.isInverse());
	}

	private void setStart(double start)
	{
		this.start = start;
	}

	private void setSweep(double sweep)
	{
		this.sweep = sweep;
	}
	
	private void setInverse(boolean inverse)
	{
		this.inverse = inverse;
	}

	public double getStart()
	{
		return start;
	}

	public double getSweep()
	{
		return sweep;
	}
	
	public boolean isInverse()
	{
		return inverse;
	}

	public double getEnd()
	{
		return Utils.angleSum(getStart(), getSweep());
	}

	@Override
	protected boolean angleLessThan(double a, double b)
	{
		a = Utils.angleDifference(a, start);
		b = Utils.angleDifference(b, start);
		return a < b;
	}

	@Override
	public Arc[] getPortionInsideRectangle(Segment[] bounds)
	{
		ArrayList<DPoint> p = new ArrayList<DPoint>();
		for (int i = 0; i < bounds.length; i++)
		{
			DPoint[] t = bounds[i].intersection(this);
			for (int j = 0; j < t.length; j++)
			{
				if (t[j] != null && !p.contains(t[j]))
					p.add(t[j]);
			}
		}
		if (!p.isEmpty())
			p = sortByAngle(p);
		p.add(0, getPointAtAngle(start));
		p.add(getPointAtAngle(Utils.angleSum(start, sweep)));
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		for (int i = 1; i < p.size(); i++)
		{
			double startAngle = getCenter().angle(p.get(i - 1));
			double endAngle = getCenter().angle(p.get(i));
			double middle = Utils.angleSum(Utils.angleDifference(endAngle, startAngle) / 2, startAngle);
			DPoint t = getPointAtAngle(middle);
			if (t.isInRectangle(bounds))
			{
				Arc arc = new Arc(getCenter(), getRadius(), startAngle, Utils.angleDifference(endAngle, startAngle));
				arcs.add(arc);
			}
		}
		Arc[] ret = new Arc[arcs.size()];
		for (int i = 0; i < arcs.size(); i++)
			ret[i] = arcs.get(i);
		return ret;
	}

	@Override
	public boolean contains(DPoint p)
	{
		double d = getCenter().dist(p);
		if (!Utils.doublesEqual(d, getRadius()))
			return false;
		double ang = getCenter().angle(p);
		return Utils.angleDifference(ang, getStart()) < getSweep() || Utils.doublesEqual(ang, getStart()) || Utils.doublesEqual(ang, getEnd());
	}

	public boolean isIn(Arc other)
	{
		boolean startIsIn = Utils.angleDifference(getStart(), other.getStart()) <= other.getSweep();
		boolean theRestIsIn = getSweep() < Utils.angleDifference(other.getEnd(), getStart());
		return startIsIn && theRestIsIn;
	}
	
	@Override
	public double angleAtPoint(DPoint p)
	{
		if (isInverse())
			return Utils.angleDifference(getCenter().angle(p), Math.PI/2);
		else
			return Utils.angleSum(getCenter().angle(p), Math.PI/2);
	}
	
	//@Override
	@Override
	public DPoint startPoint()
	{
		if (inverse)
			return getPointAtAngle(getEnd());
		else
			return getPointAtAngle(getStart());
	}
	
	//@Override
	@Override
	public double angleAtStart()
	{
		if (isInverse())
			return Utils.angleDifference(getEnd(), Math.PI/2);
		else
			return Utils.angleSum(getStart(), Math.PI/2);
	}
	
	@Override
	public double curvature()
	{
		double ret = 1/getRadius();
		if (isInverse())
			ret = -1 * ret;
		return ret;
	}
	
	@Override
	public boolean canSwitchSides()
	{
		return !Utils.doublesEqual(getSweep(), 2*Math.PI);
	}
	
	@Override
	public double distance(DPoint p)
	{
		DPoint t = closestPoint(p);
		return t.dist(p);
	}

	@Override
	public DPoint closestPoint(DPoint p)
	{
		double angle = getCenter().angle(p);
		DPoint closest = getPointAtAngle(angle);
		if (contains(closest))
			return closest;
		closest = getPointAtAngle(getStart());
		DPoint t = getPointAtAngle(getEnd());
		if (p.dist(t) < p.dist(closest))
			closest = t;
		return closest;
	}
	
	@Override
	public double distanceAlong(Pathable p)
	{
		DPoint s = p.startPoint();
		double a0 = getCenter().angle(s);
		double a1 = Utils.angleDifference(a0, getStart());
		if (isInverse())
			a1 = Utils.angleDifference(getEnd(), a0);
		return a1;
	}

	@Override
	public Pathable[] divide(DPoint p)
	{
		if (!contains(p))
			p = closestPoint(p);
		double angle = getCenter().angle(p);
		if (Utils.doublesEqual(angle, getStart()))
		{
			Pathable[] ret = new Pathable[1];
			ret[0] = new Arc(getCenter(), getRadius(), getStart(), getSweep(), false);
			return ret;
		}
		if (Utils.doublesEqual(angle, getEnd()))
		{
			Pathable[] ret = new Pathable[1];
			ret[0] = new Arc(getCenter(), getRadius(), getStart(), getSweep(), true);
			return ret;
		}
		Pathable[] ret = new Pathable[2];
		ret[0] = new Arc(getCenter(), getRadius(), angle, Utils.angleDifference(getEnd(), angle), false);
		ret[1] = new Arc(getCenter(), getRadius(), getStart(), Utils.angleDifference(angle, getStart()), true);
		return ret;
	}
	
	@Override
	public ArrayList<Pathable> getIntersectionsInOrder(int side, AlDrawState state)
	{
		ArrayList<Pathable> ret = super.getIntersectionsInOrder(side, state);
		if (Utils.doublesEqual(getSweep(), 2*Math.PI))
		{
			ArrayList<Pathable> atStart = new ArrayList<Pathable>();
			for (int i = 0; i < state.getNumPathables(); i++)
			{
				Pathable s = state.getPathable(i);
				DPoint[] intersections = intersection(s);
				for (int j = 0; j < intersections.length; j++)
				{
					if (intersections[j]!=null && intersections[j].equals(startPoint()))
					{
						Pathable[] t = s.divide(intersections[j]);
						for (int k = 0; k < t.length; k++)
						{
							if (this.comparePathableTo(t[k])==side)
								atStart.add(t[k]);
						}
					}
				}
			}
			Collections.sort(atStart, new PathableComparator(this, side));
			ret.addAll(atStart);
			ret.add(new Arc(this));
		}
		return ret;
	}
	
	@Override
	public Arc shortened(Pathable p)
	{
		double startAngle = getStart();
		double endAngle = getCenter().angle(p.startPoint());
		if (inverse)
		{
			startAngle = getCenter().angle(p.startPoint());
			endAngle = getEnd();
		}
		double sweep = Utils.angleDifference(endAngle, startAngle);
		if (Utils.doublesEqual(sweep, 0))
			sweep = 2*Math.PI;
		return new Arc(getCenter(), getRadius(), startAngle, sweep, inverse);
	}

	@Override
	public Arc copy(DPoint copy, DPoint paste)
	{
		DPoint p = getCenter();
		p = p.copy(copy, paste);
		return new Arc(p, getRadius(), getStart(), getSweep());
	}

	@Override
	public Arc copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		DPoint p = getCenter();
		p = p.copy(copy1, copy2, paste1, paste2);
		double dist = paste1.dist(paste2)/copy1.dist(copy2);
		double angle = Utils.angleDifference(paste1.angle(paste2), copy1.angle(copy2));
		return new Arc(p, getRadius()*dist, Utils.angleSum(angle, getStart()), getSweep());
	}
	
	@Override
	public void addToState(AlDrawState state)
	{
		state.addArcWithIntersections(this);
	}
	
//	public Arc2D.Double getJavaGeom(CoordinateConverter c)
//	{
//		DPoint p = c.abstractToScreenCoord(this.getCenter());
//		double screenRadius = c.abstractToScreenDist(this.getRadius());
//		int x = (int) Math.round(p.getX() - screenRadius);
//		int y = (int) Math.round(p.getY() - screenRadius);
//		int d = (int) Math.round(2 * screenRadius);
//		int angSt = (int) Math.round(-Math.toDegrees(this.getStart() + c.getAngle()));
//		int angExt = (int) Math.round(-Math.toDegrees(this.getSweep()));
//		if (isInverse())
//		{
//			angSt = (int) Math.round(-Math.toDegrees(this.getEnd() + c.getAngle()));
//			angExt = -angExt;
//		}
//		return new Arc2D.Double(x, y, d, d, angSt, angExt, Arc2D.OPEN);
//	}

	@Override
	public void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context)
	{
		DPoint p = c.abstractToScreenCoord(this.getCenter());
		double screenRadius = c.abstractToScreenDist(this.getRadius());
		RectF rect = new RectF((float)(p.getX() - screenRadius), (float)(p.getY() - screenRadius), (float)(p.getX() + screenRadius), (float)(p.getY() + screenRadius));
		float angSt = Math.round(Math.toDegrees(this.getStart() + c.getAngle()));
		float angExt = Math.round(Math.toDegrees(this.getSweep()));
		
		canvas.drawArc(rect, angSt, angExt, false, paint);
	}
	
	@Override
	public boolean sameDirection(Pathable p)
	{
		if (!(p instanceof Arc))
			return false;
		Arc a = (Arc)p;
		return this.equals(a) && this.isInverse()==a.isInverse();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Arc other = (Arc) obj;
		return (same(other) && Utils.doublesEqual(getStart(), other.getStart()) && Utils.doublesEqual(getSweep(), other.getSweep()));
	}
	
	@Override
	public String toString()
	{
		return "Arc: " + getCenter() .toString() + ", " + getRadius() + ", " + getStart() + ", " + getSweep();
	}
}// class Arc