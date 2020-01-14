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

public class CoordinateConverter
{
	/*
	 * There are two coordinate systems used for AlDraw. The abstract coordinate
	 * system is how points are represented internally. The screen coordinate
	 * system is how those points are displayed on the screen. This class is
	 * used for converting one into the other.
	 */

	private int width; // This is the width of the screen, in screen coordinates
	private int height;// This is the height of the screen, in screen
						// coordinates
	private double cX; // This is the abstract x coordinate that is displayed at
						// the center of the screen
	private double cY; // This is the abstract y coordinate that is displayed at
						// the center of the screen
	private double conversionRatio;
	private double angle;

	private double zoomAmt = 1.2;
	private double defaultCX = 0;
	private double defaultCY = 0;
	private double defaultConversionRatio = 300;
	private double defaultAngle = 0;

	public CoordinateConverter()
	{
		width = 600;
		height = 600;
		setDefaults();
	}
	
	public CoordinateConverter(CoordinateConverter other){
		width = other.width;
		height = other.height;
		cX = other.cX;
		cY = other.cY;
		conversionRatio = other.conversionRatio;
		angle = other.angle;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public Segment[] getAbstractBoundaries()
	{
		DPoint upperLeft = screenToAbstractCoord(new DPoint(0, 0));
		DPoint upperRight = screenToAbstractCoord(new DPoint(width, 0));
		DPoint lowerRight = screenToAbstractCoord(new DPoint(width, height));
		DPoint lowerLeft = screenToAbstractCoord(new DPoint(0, height));
		Segment[] ret = new Segment[4];
		ret[0] = new Segment(upperLeft, upperRight);
		ret[1] = new Segment(upperRight, lowerRight);
		ret[2] = new Segment(lowerRight, lowerLeft);
		ret[3] = new Segment(lowerLeft, upperLeft);
		return ret;
	}

	public double getCX()
	{
		return cX;
	}

	public double getCY()
	{
		return cY;
	}

	public double getConversionRatio()
	{
		return conversionRatio;
	}

	public double getAngle()
	{
		return angle;
	}

	public void setDefaults()
	{
		setDefaultCenter();
		setDefaultConversionRatio();
		setDefaultAngle();
	}

	public void setDefaultCenter()
	{
		setCX(defaultCX);
		setCY(defaultCY);
	}

	public void setDefaultConversionRatio()
	{
		setConversionRatio(defaultConversionRatio);
	}

	public void setDefaultAngle()
	{
		setAngle(defaultAngle);
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public void setCX(double x)
	{
		cX = x;
	}

	public void setCY(double y)
	{
		cY = y;
	}

	public void setConversionRatio(double c)
	{
		conversionRatio = c;
	}

	public void setAngle(double a)
	{
		angle = a;
	}

	public void resize(DPoint p)
	{
		double len, lendif;
		DPoint q = p.rotateAroundOrigin(angle);
		DPoint c = new DPoint(cX, cY).rotateAroundOrigin(angle);
		if (q.getX() < c.getX() - width / (2 * conversionRatio))
		{
			len = width / conversionRatio;
			lendif = c.getX() - len / 2 - q.getX();
			conversionRatio = width / (len + lendif);
			c.setX(c.getX() - lendif / 2);
		}
		if (q.getY() < c.getY() - height / (2 * conversionRatio))
		{
			len = height / conversionRatio;
			lendif = c.getY() - len / 2 - q.getY();
			conversionRatio = height / (len + lendif);
			c.setY(c.getY() - lendif / 2);
		}
		if (q.getX() > c.getX() + width / (2 * conversionRatio))
		{
			len = width / conversionRatio;
			lendif = q.getX() - c.getX() - len / 2;
			conversionRatio = width / (len + lendif);
			c.setX(c.getX() + lendif / 2);
		}
		if (q.getY() > c.getY() + height / (2 * conversionRatio))
		{
			len = height / conversionRatio;
			lendif = q.getY() - c.getY() - len / 2;
			conversionRatio = height / (len + lendif);
			c.setY(c.getY() + lendif / 2);
		}
		c = c.rotateAroundOrigin(-angle);
		cX = c.getX();
		cY = c.getY();
	}

	public void panLeft()
	{
		panAngle(Math.PI);
	}

	public void panRight()
	{
		panAngle(0);
	}

	public void panUp()
	{
		panAngle(3 * Math.PI / 2);
	}

	public void panDown()
	{
		panAngle(Math.PI / 2);
	}

	private void panAngle(double ang)
	{
		int min = width;
		if (height < min)
			min = height;
		DPoint p = new DPoint(screenToAbstractDist(min) / 4, 0);
		p = p.rotateAroundOrigin(ang);
		p = p.rotateAroundOrigin(-angle);
		cX = cX + p.getX();
		cY = cY + p.getY();
	}

	public void zoomIn()
	{
		conversionRatio = conversionRatio * Math.pow(zoomAmt, 2);
	}

	public void zoomIn(int x, int y)
	{
		conversionRatio = conversionRatio * zoomAmt;
		DPoint a = screenToAbstractCoord(new DPoint(x, y));
		double angle = new DPoint(cX, cY).angle(a);
		double d = new DPoint(cX, cY).dist(a);
		cX += (zoomAmt - 1) * d * Math.cos(angle);
		cY += (zoomAmt - 1) * d * Math.sin(angle);
		a = screenToAbstractCoord(new DPoint(x, y));
	}

	public void zoomOut()
	{
		conversionRatio = conversionRatio / Math.pow(zoomAmt, 2);
	}

	public void zoomOut(int x, int y)
	{
		DPoint a = screenToAbstractCoord(new DPoint(x, y));
		double angle = new DPoint(cX, cY).angle(a);
		double d = new DPoint(cX, cY).dist(a);
		cX -= (zoomAmt - 1) * d * Math.cos(angle);
		cY -= (zoomAmt - 1) * d * Math.sin(angle);
		conversionRatio = conversionRatio / zoomAmt;
	}

	public void autoZoom(AlDrawState state)
	{
		double minX, maxX, minY, maxY;
		double conRatX, conRatY, conRat;
		Arc a;
		DPoint p, q, c;
		Circle d;
		p = state.getPoint(0).rotateAroundOrigin(angle);
		minX = maxX = p.getX();
		minY = maxY = p.getY();
		for (int i = 1; i < state.getNumPoints(); i++)
		{
			p = state.getPoint(i).rotateAroundOrigin(angle);
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getX() > maxX)
				maxX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
			if (p.getY() > maxY)
				maxY = p.getY();
		}
		for (int i = 0; i < state.getNumArcs(); i++)
		{
			a = state.getArc(i);
			c = a.getCenter().rotateAroundOrigin(angle);
			p = c.translate(-a.getRadius(), 0);
			q = p.rotateAroundOrigin(-angle);
			if (a.contains(q))
			{
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
				if (p.getY() > maxY)
					maxY = p.getY();
			}
			p = c.translate(a.getRadius(), 0);
			q = p.rotateAroundOrigin(-angle);
			if (a.contains(q))
			{
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
				if (p.getY() > maxY)
					maxY = p.getY();
			}
			p = c.translate(0, -a.getRadius());
			q = p.rotateAroundOrigin(-angle);
			if (a.contains(q))
			{
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
				if (p.getY() > maxY)
					maxY = p.getY();
			}
			p = c.translate(0, a.getRadius());
			q = p.rotateAroundOrigin(-angle);
			if (a.contains(q))
			{
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
				if (p.getY() > maxY)
					maxY = p.getY();
			}
		}
		for (int i = 0; i < state.getNumCircles(); i++)
		{
			d = state.getCircle(i);
			c = d.getCenter().rotateAroundOrigin(angle);
			p = c.translate(-d.getRadius(), 0);
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getX() > maxX)
				maxX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
			if (p.getY() > maxY)
				maxY = p.getY();
			p = c.translate(d.getRadius(), 0);
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getX() > maxX)
				maxX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
			if (p.getY() > maxY)
				maxY = p.getY();
			p = c.translate(0, -d.getRadius());
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getX() > maxX)
				maxX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
			if (p.getY() > maxY)
				maxY = p.getY();
			p = c.translate(0, d.getRadius());
			if (p.getX() < minX)
				minX = p.getX();
			if (p.getX() > maxX)
				maxX = p.getX();
			if (p.getY() < minY)
				minY = p.getY();
			if (p.getY() > maxY)
				maxY = p.getY();
		}
		conRatX = width / (maxX - minX);
		conRatY = height / (maxY - minY);
		conRat = conRatX;
		if (conRatY < conRat)
			conRat = conRatY;

		p = new DPoint((maxX + minX) / 2, (maxY + minY) / 2);
		p = p.rotateAroundOrigin(-angle);

		setCX(p.getX());
		setCY(p.getY());
		setConversionRatio(conRat);
	}

	public double abstractToScreenDist(double d)
	{
		return d * conversionRatio;
	}

	public double screenToAbstractDist(double d)
	{
		return d / conversionRatio;
	}

	public DPoint abstractToScreenCoord(DPoint a)
	{
		double x1 = (a.getX() - cX) * conversionRatio;
		double y1 = (a.getY() - cY) * conversionRatio;
		DPoint s = new DPoint(x1, y1);
		s = s.rotateAroundOrigin(angle);
		s.setX(s.getX() + width / 2);
		s.setY(s.getY() + height / 2);
		return s;
	}

	public DPoint screenToAbstractCoord(DPoint s)
	{
		double x1 = (s.getX() - width / 2);
		double y1 = (s.getY() - height / 2);
		DPoint a = new DPoint(x1, y1);
		a = a.rotateAroundOrigin(-angle);
		a.setX(a.getX() / conversionRatio + cX);
		a.setY(a.getY() / conversionRatio + cY);
		return a;
	}
}
