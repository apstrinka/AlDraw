package strinka.aldrawandroid;

import java.util.Comparator;

public class ArcComparator implements Comparator<Pathable>
{
	Arc axis;
	int side;

	public ArcComparator(Arc axis, int side)
	{
		this.axis = axis;
		this.side = side;
	}

	@Override
	public int compare(Pathable arg0, Pathable arg1)
	{
		if (axis.comparePathableTo(arg0) == side && axis.comparePathableTo(arg1) != side)
			return -1;
		if (axis.comparePathableTo(arg1) == side && axis.comparePathableTo(arg0) != side)
			return 1;
		double d0 = distance(arg0);
		double d1 = distance(arg1);
		if (axis.comparePathableTo(arg0) == side)
		{
			if (Utils.doublesEqual(d0, d1))
			{
				double angle = 0;//axis.getP1().angle(axis.getP2());
				double a0 = arg0.angleAtStart();
				double a1 = arg1.angleAtStart();
				if (Utils.smallerAngleDifference(angle, a0) < Utils.smallerAngleDifference(angle, a1))
					return 1;
				else
					return -1;
			} else if (d0 < d1)
				return -1;
			else
				return 1;
		} else
		{
			if (Utils.doublesEqual(d0, d1))
			{
				double angle = 0;//axis.getP1().angle(axis.getP2());
				double a0 = arg0.angleAtStart();
				double a1 = arg1.angleAtStart();
				if (Utils.smallerAngleDifference(angle, a0) < Utils.smallerAngleDifference(angle, a1))
					return -1;
				else
					return 1;
			} else if (d0 < d1)
				return 1;
			else
				return -1;
		}
	}
	
	private double distance(Pathable p)
	{
		if (p instanceof Segment)
			return distance((Segment)p);
		else if (p instanceof Ray)
			return distance((Ray)p);
		else
			return -1;
	}
	
	private double distance(Segment s)
	{
		return 0;
		//return axis.getP1().dist(s.getP1());
	}
	
	private double distance(Ray r)
	{
		return 0;
		//return axis.getP1().dist(r.getStart());
	}
}
