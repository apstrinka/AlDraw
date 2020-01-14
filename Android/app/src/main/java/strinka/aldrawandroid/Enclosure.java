package strinka.aldrawandroid;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class Enclosure implements Drawable
{
	private ArrayList<Pathable> path;
	private int color;
	
	public Enclosure(ArrayList<Pathable> path)
	{
		this.path = path;
		this.color = Color.RED;
	}
	
	public Enclosure(ArrayList<Pathable> path, int color)
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
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color)
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
	public void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context) {
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		Path drawPath = new Path();
		for (int i = 0; i < path.size(); i++) {
			Pathable pathable = path.get(i);
			if (i == 0){
				DPoint p = c.abstractToScreenCoord(pathable.startPoint());
				drawPath.moveTo((float)p.getX(), (float)p.getY());
			}
			if (pathable instanceof Arc) {
				Arc arc = (Arc)pathable;
				DPoint p = c.abstractToScreenCoord(arc.getCenter());
				double screenRadius = c.abstractToScreenDist(arc.getRadius());
				RectF rect = new RectF((float)(p.getX() - screenRadius), (float)(p.getY() - screenRadius), (float)(p.getX() + screenRadius), (float)(p.getY() + screenRadius));
				float angSt = Math.round(Math.toDegrees(arc.getStart() + c.getAngle()));
				float angExt = Math.round(Math.toDegrees(arc.getSweep()));
				if (arc.isInverse()){
					angSt = (int) Math.round(Math.toDegrees(arc.getEnd() + c.getAngle()));
					angExt = -angExt;
				}
				drawPath.arcTo(rect, angSt, angExt);
			} else if (pathable instanceof Segment) {
				Segment segment = (Segment)pathable;
				DPoint p = c.abstractToScreenCoord(segment.getP2());
				drawPath.lineTo((float)p.getX(), (float)p.getY());
			}
		}
		canvas.drawPath(drawPath, paint);
		paint.setStyle(Paint.Style.STROKE);
	}

}
