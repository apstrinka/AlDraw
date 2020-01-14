package strinka.aldraw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;

public class Enclosure implements Drawable
{
	private ArrayList<Pathable> path;
	private Color color;
	
	public Enclosure(ArrayList<Pathable> path)
	{
		this.path = path;
		this.color = Color.RED;
	}
	
	public Enclosure(ArrayList<Pathable> path, Color color)
	{
		this.path = path;
		this.color = color;
	}
	
	public static Enclosure findEnclosure(DPoint selPoint, AlDrawState state, boolean debug)
	{
		Enclosure ret = null;
		ArrayList<Pathable> closest = new ArrayList<Pathable>();
		for (int i = 0; i < state.getNumPathables(); i++)
		{
			Pathable t = state.getPathable(i);
			closest.add(t);
		}
		Collections.sort(closest, new DistComparator(selPoint));
		if (debug)
		{
			Pathable p = closest.get(0).divide(selPoint)[0];
			int side = p.comparePointTo(selPoint);
			ArrayList<Pathable> arr = p.getIntersectionsInOrder(side, state);
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println(p);
			System.out.println("side: " + side + " curv: " + p.curvature());
			System.out.println("-----------------------------------------------------------------------------");
			for (int i = 0; i < arr.size(); i++)
			{
				Pathable q = arr.get(i);
				System.out.println(q);
				System.out.println("side: " + p.comparePathableTo(q) + " dist: " + p.distanceAlong(q) + " ang: " + p.angleDifference(q) + " curv: " + q.curvature());
			}
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		}
		for (int i = 0; i < closest.size(); i++)
		{
			Pathable s = closest.get(i);
			Pathable[] ss = s.divide(s.closestPoint(selPoint));	
			for (int j = 0; j < ss.length; j++)
			{
				int side = ss[j].comparePointTo(selPoint);
				ret = findEnclosure(selPoint, new ArrayList<Pathable>(), ss[j], side, state, debug);
				if (ret != null && ret.contains(selPoint))
				{
					return ret;
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static Enclosure findEnclosure(DPoint selPoint, ArrayList<Pathable> oldpath, Pathable pathable, int side, AlDrawState state, boolean debug)
	{
		ArrayList<Pathable> newpath = null;
		ArrayList<Pathable> l = pathable.getIntersectionsInOrder(side, state);
		for (int i = 0; i < l.size(); i++)
		{
			newpath = (ArrayList<Pathable>)oldpath.clone();
			Pathable s = l.get(i);
			Pathable t = pathable.shortened(s);
			if ((newpath.indexOf(t) != newpath.lastIndexOf(t)) || (newpath.contains(t) && t.sameDirection(newpath.get(newpath.indexOf(t)))))
			{
				int index = newpath.indexOf(t);
				for (int j = 0; j < index; j++)
					newpath.remove(0);
				return new Enclosure(newpath);
			}
			newpath.add(t);
			Enclosure ret = findEnclosure(selPoint, newpath, s, side, state, debug);
			if (ret != null)
				return ret;
		}
		return null;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public int getNumPathables()
	{
		return path.size();
	}
	
	public Pathable getPathable(int i)
	{
		return path.get(i);
	}
	
	public boolean contains(DPoint p)
	{
		Ray r1 = new Ray(p, 0);
		Ray r2 = new Ray(p, Math.PI);
		int num1 = 0;
		int num2 = 0;
		for (int i = 0; i < path.size(); i++)
		{
			Pathable cur = path.get(i);
			DPoint[] inter = r1.intersection(cur);
			for (int j = 0; j < inter.length; j++)
			{
				if (inter[j] != null)
					num1++;
			}
			inter = r2.intersection(path.get(i));
			for (int j = 0; j < inter.length; j++)
			{
				if (inter[j] != null)
					num2++;
			}
		}
		if (num1 % 2 != num2 % 2 || num1 % 2 == 0)
			return false;
		return true;
	}
	
	@Override
	public void draw(Graphics g, CoordinateConverter c, PreferenceOptions prefOpts)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(color);
		Path2D.Double path2d = new Path2D.Double(Path2D.WIND_EVEN_ODD);
		for (int i = 0; i < path.size(); i++)
		{
			Pathable pathable = path.get(i);
			if (pathable instanceof Arc)
			{
				Arc arc = (Arc)pathable;
				path2d.append(arc.getJavaGeom(c), true);
			}
			else if (pathable instanceof Segment)
			{
				Segment segment = (Segment)pathable;
				path2d.append(segment.getJavaGeom(c), true);
			}
		}
		g2.fill(path2d);
	}

}
