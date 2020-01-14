package strinka.aldraw;

import java.util.Comparator;

public class DistComparator implements Comparator<Pathable>
{
	DPoint sel;

	public DistComparator(DPoint sel)
	{
		this.sel = sel;
	}

	@Override
	public int compare(Pathable arg0, Pathable arg1)
	{
		double d0 = arg0.distance(sel);
		double d1 = arg1.distance(sel);
		if (Utils.doublesEqual(d0, d1))
		{
			if (arg0 instanceof Circle && arg1 instanceof Circle)
			{
				double e0 = ((Circle)arg0).getCenter().dist(sel);
				double e1 = ((Circle)arg1).getCenter().dist(sel);
				if (Utils.doublesEqual(e0, e1))
					return 0;
				else if (e0 < e1)
					return -1;
				else
					return 1;
			}
			else
				return 0;
		}
		else if (d0 < d1)
			return -1;
		else
			return 1;
	}

}
