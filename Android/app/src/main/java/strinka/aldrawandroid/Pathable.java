package strinka.aldrawandroid;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Pathable implements Intersectable, Drawable
{
	abstract public boolean contains(DPoint p);
	abstract public DPoint startPoint();
	abstract public double angleAtStart();
	abstract public double angleAtPoint(DPoint p);
	abstract public double curvature();
	abstract public boolean canSwitchSides();
	abstract public double distance(DPoint p);
	abstract public DPoint closestPoint(DPoint p);
	abstract public double distanceAlong(Pathable p);
	public double angleDifference(Pathable p)
	{
		double a = angleAtPoint(p.startPoint());
		double b = p.angleAtStart();
		double dif = Utils.angleDifference(b, a);
		return dif;
	}
	public int comparePathableTo(Pathable p)
	{
		double angle = angleDifference(p);
		if (Utils.doublesEqual(angle, 0))
		{
			if (Utils.doublesEqual(p.curvature(), this.curvature()))
				return 0;
			if (p.curvature() < this.curvature())
				return -1;
			return 1;
		}
		if (Utils.doublesEqual(angle, Math.PI))
		{
			if (Utils.doublesEqual(p.curvature(), -this.curvature()))
				return 0;
			if (p.curvature() < -this.curvature())
				return 1;
			return -1;
		}
		if (angle < Math.PI)
			return 1;
		return -1;
	}
	public int comparePointTo(DPoint p)
	{
		DPoint closest = closestPoint(p);
		Segment s = new Segment(closest, p);
		return comparePathableTo(s);
	}
	abstract public Pathable[] divide(DPoint p);
	public ArrayList<Pathable> getIntersectionsInOrder(int side, AlDrawState state)
	{
		ArrayList<Pathable> ret = new ArrayList<Pathable>();
		for (int i = 0; i < state.getNumPathables(); i++)
		{
			Pathable s = state.getPathable(i);
			DPoint[] intersections = intersection(s);
			for (int j = 0; j < intersections.length; j++)
			{
				if (intersections[j]!=null && !intersections[j].equals(startPoint()))
				{
					Pathable[] t = s.divide(intersections[j]);
					for (int k = 0; k < t.length; k++)
					{
						if (this.canSwitchSides() || this.comparePathableTo(t[k])==side)
							ret.add(t[k]);
					}
				}
			}
		}
		Collections.sort(ret, new PathableComparator(this, side));
		return ret;
	}
	abstract public Pathable shortened(Pathable p);
	abstract public boolean sameDirection(Pathable p);
}
