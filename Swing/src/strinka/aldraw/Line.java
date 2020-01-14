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

import java.awt.Graphics;
import java.util.ArrayList;

public class Line extends AbstractLine
{
	private double slope;
	private double intercept; // this is the y intercept, unless the slope is
								// vertical

	public Line(double slope, double intercept)
	{
		setSlope(slope);
		setIntercept(intercept);
	}

	public Line(DPoint p1, DPoint p2)
	{
		if (Utils.doublesEqual(p2.getX(), p1.getX()))
			setSlope(Double.POSITIVE_INFINITY);
		else
			setSlope((p2.getY() - p1.getY()) / (p2.getX() - p1.getX()));

		if (Double.isInfinite(slope))
			setIntercept(p1.getX());
		else
			setIntercept(p1.getY() - slope * p1.getX());
	}

	public Line(DPoint point, double slope)
	{
		setSlope(slope);
		if (Double.isInfinite(slope))
		{
			setIntercept(point.getX());
		} else
		{
			setIntercept(point.getY() - slope * point.getX());
		}
	}

	public Line(AbstractLine other)
	{
		setSlope(other.getSlope());
		setIntercept(other.getIntercept());
	}

	private void setSlope(double slope)
	{
		this.slope = slope;
	}

	private void setIntercept(double intercept)
	{
		this.intercept = intercept;
	}

	@Override
	public double getSlope()
	{
		return slope;
	}

	@Override
	public double getIntercept()
	{
		return intercept;
	}
	
	public DPoint getInterceptPoint()
	{
		if (Double.isInfinite(getSlope()))
		{
			return new DPoint(getIntercept(), 0);
		}
		else
			return new DPoint(0, getIntercept());
	}

	public ArrayList<Pathable> getIntersectionsInOrder(int side, AlDrawState state)
	{
		return null;
	}

	public Segment getPortionInsideRectangle(Segment[] bounds)
	{
		ArrayList<DPoint> p = new ArrayList<DPoint>();
		for (int i = 0; i < bounds.length; i++)
		{
			DPoint t = this.intersection(bounds[i]);
			if (t != null && !p.contains(t))
			{
				p.add(t);
			}
		}
		if (p.size() >= 2)
			return new Segment(p.get(0), p.get(1));
		else
			return null;
	}
	
	@Override
	public DPoint startPoint()
	{
		return null;
	}
	
	@Override
	public double angleAtStart()
	{
		return -1;
	}
	
	@Override
	public double angleAtPoint(DPoint p)
	{
		return -1;
	}
	
	@Override
	public boolean canSwitchSides()
	{
		return false;
	}
	
	@Override
	public double distanceAlong(Pathable p)
	{
		return -1;
	}

	public Ray[] divide(DPoint p)
	{
		
		if (!this.contains(p))
			p = closestPoint(p);
		Ray[] ret = new Ray[2];
		double angle = Utils.slopeToAngle(this.getSlope());
		ret[0] = new Ray(p, angle);
		angle = Utils.angleSum(angle, Math.PI);
		ret[1] = new Ray(p, angle);
		
		return ret;
	}
	
	@Override
	public Pathable shortened(Pathable p)
	{
		return null;
	}
	
	@Override
	public boolean contains(DPoint p)
	{
		if (Double.isInfinite(slope))
			return Utils.doublesEqual(intercept, p.getX());
		else
			return Utils.doublesEqual(p.getY(), slope * p.getX() + intercept);
	}

	@Override
	public Line copy(DPoint copy, DPoint paste)
	{
		DPoint p = getInterceptPoint();
		p = p.copy(copy, paste);
		return new Line(p, getSlope());
	}

	@Override
	public Line copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		DPoint p = getInterceptPoint();
		p = p.copy(copy1, copy2, paste1, paste2);
		double angle = Utils.angleDifference(paste1.angle(paste2), copy1.angle(copy2));
		double s = Utils.angleToSlope(Utils.angleSum(angle, Utils.slopeToAngle(getSlope())));
		if (Utils.doublesEqual(angle, 0) || Utils.doublesEqual(angle, Math.PI))
			s = getSlope();
		return new Line(p, s);
	}

	@Override
	public void addToState(AlDrawState state)
	{
		state.addLineWithIntersections(this);
	}

	@Override
	public void draw(Graphics g, CoordinateConverter c, PreferenceOptions prefOpts)
	{
		// ArrayList<DPoint> p = new ArrayList<DPoint>();
		// Segment[] bounds = c.getAbstractBoundaries();
		// for (int i = 0; i < bounds.length; i++) {
		// DPoint t = this.intersection(bounds[i]);
		// if (t != null && !p.contains(c.abstractToScreenCoord(t))) {
		// p.add(c.abstractToScreenCoord(t));
		// }
		// }
		// if (p.size() >= 2)
		// g.drawLine((int) (p.get(0).getX()), (int) (p.get(0).getY()), (int)
		// (p.get(1).getX()), (int) (p.get(1).getY()));
	
		Segment[] bounds = c.getAbstractBoundaries();
		Segment draw = this.getPortionInsideRectangle(bounds);
		if (draw != null)
		{
			DPoint p1 = c.abstractToScreenCoord(draw.getP1());
			DPoint p2 = c.abstractToScreenCoord(draw.getP2());
			g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
		}
	
	}
	
	@Override
	public boolean sameDirection(Pathable p)
	{
		return false;
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
		Line other = (Line) obj;
		return (Utils.doublesEqual(this.getIntercept(), other.getIntercept()) && Utils.doublesEqual(this.getSlope(), other.getSlope()));
	}
}
