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

public class Ray extends AbstractLine
{
	private DPoint start;
	private double angle;

	public Ray(DPoint start, DPoint direction)
	{
		setStart(start);
		setAngle(start.angle(direction));
	}

	public Ray(DPoint start, double angle)
	{// public void setSlope(double
		// slope)
		setStart(start);
		setAngle(angle);
	}// constructor

	public Ray(Ray other)
	{
		setStart(other.getStart());
		setAngle(other.getAngle());
	}// constructor

	private void setStart(DPoint start)
	{
		this.start = start;
	}// public void setStart(DPoint start)

	private void setAngle(double angle)
	{
		this.angle = angle;
	}

	public DPoint getStart()
	{
		return start;
	}// public DPoint getStart()

	public double getAngle()
	{
		return angle;
	}

	@Override
	public double getSlope()
	{
		double ret = 0;
		if (Utils.doublesEqual(angle, Math.PI / 2))
		{
			ret = Double.POSITIVE_INFINITY;
		} else if (Utils.doublesEqual(angle, 3 * Math.PI / 2))
		{
			ret = Double.NEGATIVE_INFINITY;
		} else
		{
			ret = Math.tan(angle);
		}
		return ret;
	}

	@Override
	public double getIntercept()
	{
		double slope = this.getSlope();
		if (Double.isInfinite(slope))
			return start.getX();
		else
			return start.getY() - slope * start.getX();
	}
	
	public Segment getPortionInsideRectangle(Segment[] bounds)
	{
		ArrayList<DPoint> p = new ArrayList<DPoint>();
		p.add(this.getStart());
		for (int i = 0; i < bounds.length; i++)
		{
			DPoint t = this.intersection(bounds[i]);
			if (t != null && !p.contains(t))
			{
				p.add(t);
			}
		}
		if (p.size() == 1)
			return null;
		else if (p.size() == 2)
			return new Segment(p.get(0), p.get(1));
		else
			return new Segment(p.get(1), p.get(2));
	}
	
	@Override
	public DPoint startPoint()
	{
		return getStart();
	}
	
	@Override
	public double angleAtStart()
	{
		return getAngle();
	}
	
	@Override
	public double angleAtPoint(DPoint p)
	{
		return this.getAngle();
	}
	
	@Override
	public boolean canSwitchSides()
	{
		return false;
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
		return getStart();
	}
	
	@Override
	public double distanceAlong(Pathable p)
	{
		DPoint s = p.startPoint();
		return getStart().dist(s);
	}
	
	public Pathable[] divide(DPoint p)
	{
		if (!this.contains(p))
			p = closestPoint(p);
		if (this.getStart().equals(p))
		{
			Pathable[] ret = new Pathable[1];
			ret[0] = new Ray(p, getAngle());
			return ret;
		}
		Pathable[] ret = new Pathable[2];
		ret[0] = new Segment(p, getStart());
		ret[1] = new Ray(p, getAngle());
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
		double pAng = start.angle(p);
		return Utils.doublesEqual(pAng, angle) || p.equals(start);
	}

	@Override
	public Ray copy(DPoint copy, DPoint paste)
	{
		DPoint s = getStart().copy(copy, paste);
		return new Ray(s, getAngle());
	}

	@Override
	public Ray copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		DPoint s = getStart().copy(copy1, copy2, paste1, paste2);
		double angle = Utils.angleDifference(paste1.angle(paste2), copy1.angle(copy2));
		angle = Utils.angleSum(angle, getAngle());
		return new Ray(s, angle);
	}

	@Override
	public void addToState(AlDrawState state)
	{
		state.addRayWithIntersections(this);
	}

	@Override
	public void draw(Graphics g, CoordinateConverter s, PreferenceOptions prefOpts)
	{
		// ArrayList<DPoint> p = new ArrayList<DPoint>();
		// p.add(s.abstractToScreenCoord(this.getStart()));
		// Segment[] bounds = s.getAbstractBoundaries();
		// for (int i = 0; i < bounds.length; i++) {
		// DPoint t = this.intersection(bounds[i]);
		// if (t != null && !p.contains(s.abstractToScreenCoord(t))) {
		// p.add(s.abstractToScreenCoord(t));
		// }
		// }
		// if (p.size() == 1)
		// ;// do nothing
		// else if (p.size() == 2)
		// g.drawLine((int) (p.get(0).getX()), (int) (p.get(0).getY()), (int)
		// (p.get(1).getX()), (int) (p.get(1).getY()));
		// else
		// g.drawLine((int) (p.get(1).getX()), (int) (p.get(1).getY()), (int)
		// (p.get(2).getX()), (int) (p.get(2).getY()));
	
		Segment[] bounds = s.getAbstractBoundaries();
		Segment draw = this.getPortionInsideRectangle(bounds);
		if (draw != null)
		{
			DPoint p1 = s.abstractToScreenCoord(draw.getP1());
			DPoint p2 = s.abstractToScreenCoord(draw.getP2());
			g.drawLine((int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY());
		}
	}
	
	@Override
	public boolean sameDirection(Pathable p)
	{
		if (!(p instanceof Ray))
			return false;
		Ray r = (Ray)p;
		return this.getStart().equals(r.getStart()) && Utils.doublesEqual(this.getAngle(), r.getAngle());
	}

	public boolean equals(Ray other)
	{
		return (start.equals(other.getStart())) && Utils.doublesEqual(angle, other.getAngle());
	}

	public String toString()
	{
		return "Ray: " + start.toString() + ", " + angle;
	}
}