package strinka.aldrawandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;

public class DPoint implements Drawable, Selectable
{
	private double x;
	private double y;
	private int color = Color.GREEN;
	
	public DPoint()
	{
		x = 0;
		y = 0;
	}

	public DPoint(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public DPoint(DPoint other)
	{
		this.x = other.getX();
		this.y = other.getY();
	}
	
	public DPoint(DPoint other,int color)
	{
		this(other);
		this.color = color;
	}
	
	public DPoint(DPoint p1, DPoint p2)
	{
		this.x = p1.getX() + p2.getX();
		this.y = p1.getY() + p2.getY();
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double dist(DPoint other)
	{
		return Math.hypot(x - other.getX(), y - other.getY());
	}
	
	public double angle(DPoint other)
	{
		double ang = Math.atan2(other.getY() - y, other.getX() - x);
		ang = Utils.normalizeAngle(ang);
		return ang;
	}
	
	public static boolean isBetween(DPoint middle, DPoint a, DPoint b)
	{
		return Utils.isBetween(middle.getX(), a.getX(), b.getX()) && Utils.isBetween(middle.getY(), a.getY(), b.getY());
	}

	// The array of segments must form a rectangle. There must be exactly four
	// segments. The first and third must be parallel
	// and the second and fourth must be parallel. The end points must meet. The
	// lines must not overlap.
	public boolean isInRectangle(Segment[] bounds)
	{
		boolean firstPair = (Utils.comparePointToLine(this, bounds[0])) != (Utils.comparePointToLine(this, bounds[2]));
		boolean secondPair = (Utils.comparePointToLine(this, bounds[1])) != (Utils.comparePointToLine(this, bounds[3]));
		return firstPair && secondPair;
	}
	
	public DPoint translate(double x, double y)
	{
		double newX = this.x + x;
		double newY = this.y + y;
		return new DPoint(newX, newY);
	}
	
	public DPoint rotateAroundOrigin(double angle)
	{
		double x = Math.cos(angle) * this.getX() - Math.sin(angle) * this.getY();
		double y = Math.sin(angle) * this.getX() + Math.cos(angle) * this.getY();
		return new DPoint(x, y);
	}
	
	public DPoint rotateAroundPoint(double angle, DPoint p)
	{
		double x = this.getX() - p.getX();
		double y = this.getY() - p.getY();
		x = Math.cos(angle) * x - Math.sin(angle) * y;
		y = Math.sin(angle) * x + Math.cos(angle) * y;
		x = x + p.getX();
		y = y + p.getY();
		return new DPoint(x, y);
	}
	
	@Override
	public DPoint copy(DPoint copy, DPoint paste)
	{
		double x = paste.getX() -  copy.getX();
		double y = paste.getY() -  copy.getY();
		return this.translate(x, y);
	}

	@Override
	public DPoint copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2)
	{
		if (copy1.equals(copy2) || paste1.equals(paste2))
			return copy(copy1, paste1);
		double angle = Utils.angleDifference(paste1.angle(paste2), copy1.angle(copy2));
		double dist = paste1.dist(paste2)/copy1.dist(copy2);
		DPoint ret = this.translate(-copy1.getX(), -copy1.getY());
		ret = ret.rotateAroundOrigin(angle);
		ret.setX(ret.getX()*dist);
		ret.setY(ret.getY()*dist);
		ret = ret.translate(paste1.getX(), paste1.getY());
		return ret;
	}

	@Override
	public void addToState(AlDrawState state)
	{
		state.addPoint(this);
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint, CoordinateConverter s, Context context)
	{
		int previousColor = paint.getColor();
		DPoint q = s.abstractToScreenCoord(this);
		paint.setColor(Utils.darkerColor(color));
		paint.setStyle(Paint.Style.FILL);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		double radius = pref.getInt("pref_dot_radius_int", 14);
		//double radius = 14;//prefOpts.getDotRadius();
		canvas.drawCircle(Math.round(q.getX()), Math.round(q.getY()), Math.round(radius), paint);
		paint.setColor(color);
		radius = radius / 2;
		canvas.drawCircle(Math.round(q.getX()), Math.round(q.getY()), Math.round(radius), paint);
		paint.setColor(previousColor);
		paint.setStyle(Paint.Style.STROKE);
	}
	
	@Override
	public boolean equals(Object other)
	{
		DPoint p = (DPoint) other;
		return equals(p);
	}

	public boolean equals(DPoint other)
	{
		double xdif = x - other.getX();
		double ydif = y - other.getY();
		double dist = Math.hypot(xdif, ydif);
		return Utils.doublesEqual(dist, 0);
	}

	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
}
