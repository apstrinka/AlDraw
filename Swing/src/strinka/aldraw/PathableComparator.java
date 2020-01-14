package strinka.aldraw;

import java.util.Comparator;

public class PathableComparator implements Comparator<Pathable>
{
	Pathable axis;
	int side;

	public PathableComparator(Pathable axis, int side)
	{
		this.axis = axis;
		this.side = side;
	}

	@Override
	public int compare(Pathable arg0, Pathable arg1)
	{
		int s0 = axis.comparePathableTo(arg0);
		int s1 = axis.comparePathableTo(arg1);
		if (s0 == side && s1 != side)
			return -1;
		if (s1 == side && s0 != side)
			return 1;
		double d0 = axis.distanceAlong(arg0);
		double d1 = axis.distanceAlong(arg1);
		double a0 = axis.angleDifference(arg0);
		if (s0 == -1 && Utils.doublesEqual(a0, 0))
			a0 = 2*Math.PI;
		double a1 = axis.angleDifference(arg1);
		if (s1 == -1 && Utils.doublesEqual(a1, 0))
			a1 = 2*Math.PI;
		if (s0 == side)
		{
			if (Utils.doublesEqual(d0, d1))
			{
				if (Utils.doublesEqual(a0, a1))
				{
					double c0 = arg0.curvature();
					double c1 = arg1.curvature();
					if (Utils.doublesEqual(c0, c1))
						return 0;
					else if (c0 > c1)
						return -side;
					else
						return side;
				}
				else if (a0 > a1)
					return -side;
				else
					return side;
			}
			else if (d0 < d1)
				return -1;
			else
				return 1;
		}
		else
		{
			if (Utils.doublesEqual(d0, d1))
			{
				if (Utils.doublesEqual(a0, a1))
				{
					double c0 = arg0.curvature();
					double c1 = arg1.curvature();
					if (Utils.doublesEqual(c0, c1))
						return 0;
					else if (c0 > c1)
						return -side;
					else
						return side;
				}
				else if (a0 > a1)
					return -side;
				else
					return side;
			}
			else if (d0 < d1)
				return 1;
			else
				return -1;
		}
	}
}
