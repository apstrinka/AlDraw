package strinka.aldrawandroid;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;


public class Circle extends Pathable implements Selectable
{
	private DPoint center;
	private double radius;

	public Circle(DPoint center, double radius)
	{
		setCenter(center);
		setRadius(radius);
	}
	
	public Circle(DPoint center, DPoint radius)
	{
		setCenter(center);
		setRadius(center.dist(radius));
	}

	public Circle(Circle other)
	{
		setCenter(other.getCenter());
		setRadius(other.getRadius());
	}

	private void setCenter(DPoint center)
	{
		this.center = center;
	}

	private void setRadius(double radius)
	{
		this.radius = radius;
	}

	public DPoint getCenter()
	{
		return center;
	}

	public double getRadius()
	{
		return radius;
	}

	public DPoint getPointAtAngle(double angle)
	{
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new DPoint(x, y);
	}
	
	protected boolean angleLessThan(double a, double b)
	{
		return a < b;
	}

	protected ArrayList<DPoint> sortByAngle(ArrayList<DPoint> p)
	{
		ArrayList<DPoint> q = new ArrayList<DPoint>();
		q.add(p.get(0));
		for (int i = 1; i < p.size(); i++)
		{
			DPoint t = p.get(i);
			double a = center.angle(t);
			for (int j = 0; j < q.size(); j++)
			{
				double b = center.angle(q.get(j));
				if (angleLessThan(a, b))
				{
					q.add(j, t);
					break;
				}
				if (j == q.size() - 1)
				{
					q.add(t);
					break;
				}
			}
		}
		return q;
	}

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
		if (p.size() < 2)
		{
			DPoint t = getPointAtAngle(0);
			if (t.isInRectangle(bounds))
			{
				Arc[] ret = new Arc[1];
				ret[0] = new Arc(center, radius, 0, 2 * Math.PI);
				return ret;
			} else
				return new Arc[0];
		}
		p = sortByAngle(p);
		ArrayList<Arc> arcs = new ArrayList<Arc>();
		for (int i = 0; i < p.size(); i++)
		{
			double startAngle = center.angle(p.get(p.size() - 1));
			if (i > 0)
				startAngle = center.angle(p.get(i - 1));
			double endAngle = center.angle(p.get(i));
			double middle = Utils.angleSum(Utils.angleDifference(endAngle, startAngle) / 2, startAngle);
			DPoint t = getPointAtAngle(middle);
			if (t.isInRectangle(bounds))
			{
				Arc arc = new Arc(center, radius, startAngle, Utils.angleDifference(endAngle, startAngle));
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
		return Utils.doublesEqual(p.dist(getCenter()), getRadius());
	}
	
	@Override
	public DPoint[] intersection(Intersectable other)
	{
		if (other instanceof AbstractLine)
		{
			return intersection((AbstractLine)other);
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

	public DPoint[] intersection(AbstractLine other)
	{
		return other.intersection(this);
	}

	public DPoint[] intersection(Circle other)
	{
		double r1 = radius;
		double r2 = other.getRadius();
		double dist = center.dist(other.getCenter());
		double ang = center.angle(other.getCenter());
		if (center.equals(other.getCenter()))
			return new DPoint[0];
		if (Utils.doublesEqual(dist, (r1 + r2)))
		{
			DPoint ret[] = new DPoint[1];
			ret[0] = center.translate(r1 * Math.cos(ang), r1 * Math.sin(ang));
			if (!this.contains(ret[0]) || !other.contains(ret[0]))
				ret[0] = null;
			return ret;
		}
		if (Utils.doublesEqual(dist, Math.abs(r1 - r2)))
		{
			DPoint ret[] = new DPoint[1];
			ret[0] = center.translate(r1 * Math.cos(ang), r1 * Math.sin(ang));
			if (r1 < r2)
				ret[0] = center.translate(r1 * Math.cos(Utils.oppositeAngle(ang)), r1 * Math.sin(Utils.oppositeAngle(ang)));
			if (!this.contains(ret[0]) || !other.contains(ret[0]))
				ret[0] = null;
			return ret;
		}
		if (dist > r1 + r2 || dist < Math.abs(r1 - r2))
		{
			return new DPoint[0];
		}
		DPoint ret[] = new DPoint[2];
		double sepAng = Math.acos((Math.pow(r1, 2) + Math.pow(dist, 2) - Math.pow(r2, 2)) / (2 * r1 * dist));
		ret[0] = center.translate(r1 * Math.cos(ang + sepAng), r1 * Math.sin(ang + sepAng));
		ret[1] = center.translate(r1 * Math.cos(ang - sepAng), r1 * Math.sin(ang - sepAng));
		for (int i = 0; i < 2; i++)
		{
			if (!this.contains(ret[i]) || !other.contains(ret[i]))
				ret[i] = null;
		}
		return ret;
	}
	
	@Override
	public boolean canSwitchSides()
	{
		return false;
	}

	@Override
	public double distance(DPoint p)
	{
		return Math.abs(getCenter().dist(p) - getRadius());
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
	public double curvature()
	{
		return 1/getRadius();
	}

	@Override
	public DPoint closestPoint(DPoint p)
	{
		double angle = getCenter().angle(p);
		return getPointAtAngle(angle);
	}

	@Override
	public double distanceAlong(Pathable p)
	{
		return -1;
	}

	@Override
	public Pathable[] divide(DPoint p)
	{
		if (!contains(p))
			p = closestPoint(p);
		Pathable[] ret = new Pathable[2];
		double angle = getCenter().angle(p);
		ret[0] = new Arc(getCenter(), getRadius(), angle, 2*Math.PI, false);
		ret[1] = new Arc(getCenter(), getRadius(), angle, 2*Math.PI, true);
		return ret;
	}
	
	@Override
	public Pathable shortened(Pathable p)
	{
		return null;
	}

	@Override
	public Circle copy(DPoint copy, DPoint paste)
	{
		DPoint p = getCenter();
		p = p.copy(copy, paste);
		return new Circle(p, getRadius());
	}

	@Override
	public Circle copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		DPoint p = getCenter();
		p = p.copy(copy1, copy2, paste1, paste2);
		double dist = paste1.dist(paste2)/copy1.dist(copy2);
		return new Circle(p, getRadius()*dist);
	}

	@Override
	public void addToState(AlDrawState state)
	{
		state.addCircleWithIntersections(this);
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context)
	{
		DPoint p = c.abstractToScreenCoord(this.getCenter());
		double screenRadius = c.abstractToScreenDist(this.getRadius());
		
		canvas.drawCircle((float)p.getX(), (float)p.getY(), (float)screenRadius, paint);
	}
	
	public boolean same(Circle other)
	{
		return (getCenter().equals(other.getCenter()) && Utils.doublesEqual(getRadius(), other.getRadius()));
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
		Circle other = (Circle) obj;
		return same(other);
	}
}
