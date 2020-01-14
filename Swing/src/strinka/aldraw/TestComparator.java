package strinka.aldraw;

import java.util.Comparator;

public class TestComparator implements Comparator<Segment>
{
	Segment axis;
	int side;

	public TestComparator(Segment axis, int side)
	{
		this.axis = axis;
		this.side = side;
	}

	@Override
	public int compare(Segment arg0, Segment arg1)
	{
		if (Utils.comparePointToLine(arg0.getP2(), axis) == side && Utils.comparePointToLine(arg1.getP2(), axis) != side)
			return -1;
		if (Utils.comparePointToLine(arg1.getP2(), axis) == side && Utils.comparePointToLine(arg0.getP2(), axis) != side)
			return 1;
		double d0 = axis.getP1().dist(arg0.getP1());
		double d1 = axis.getP1().dist(arg1.getP1());
		if (Utils.comparePointToLine(arg0.getP2(), axis) == side)
		{
			if (Utils.doublesEqual(d0, d1))
			{
				double angle = axis.getP1().angle(axis.getP2());
				double a0 = arg0.getP1().angle(arg0.getP2());
				double a1 = arg1.getP1().angle(arg1.getP2());
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
				double angle = axis.getP1().angle(axis.getP2());
				double a0 = arg0.getP1().angle(arg0.getP2());
				double a1 = arg1.getP1().angle(arg1.getP2());
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

}
