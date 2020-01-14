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

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;

public class AlDrawState
{
	private ArrayList<Enclosure> enclosures;
	private ArrayList<Segment> segments;
	private ArrayList<Ray> rays;
	private ArrayList<Line> lines;
	private ArrayList<Arc> arcs;
	private ArrayList<Circle> circles;
	private ArrayList<DPoint> points;
	double mark;
	AlDrawState previous;
	AlDrawState next;
	AlDrawController controller;

	public AlDrawState(AlDrawController control)
	{
		enclosures = new ArrayList<Enclosure>();
		segments = new ArrayList<Segment>();
		rays = new ArrayList<Ray>();
		lines = new ArrayList<Line>();
		arcs = new ArrayList<Arc>();
		circles = new ArrayList<Circle>();
		points = new ArrayList<DPoint>();
		mark = 0;
		previous = null;
		next = null;
		controller = control;
	}

	public AlDrawState(AlDrawState other)
	{
		enclosures = new ArrayList<Enclosure>();
		for (int i = 0; i < other.getNumEnclosures(); i++)
		{
			enclosures.add(other.getEnclosure(i));
		}
		segments = new ArrayList<Segment>();
		for (int i = 0; i < other.getNumSegments(); i++)
		{
			segments.add(other.getSegment(i));
		}
		rays = new ArrayList<Ray>();
		for (int i = 0; i < other.getNumRays(); i++)
		{
			rays.add(other.getRay(i));
		}
		lines = new ArrayList<Line>();
		for (int i = 0; i < other.getNumLines(); i++)
		{
			lines.add(other.getLine(i));
		}
		arcs = new ArrayList<Arc>();
		for (int i = 0; i < other.getNumArcs(); i++)
		{
			arcs.add(other.getArc(i));
		}
		circles = new ArrayList<Circle>();
		for (int i = 0; i < other.getNumCircles(); i++)
		{
			circles.add(other.getCircle(i));
		}
		points = new ArrayList<DPoint>();
		for (int i = 0; i < other.getNumPoints(); i++)
		{
			points.add(other.getPoint(i));
		}
		mark = other.getMark();
		previous = null;
		next = null;
		controller = other.controller;
	}

	public void start()
	{
		this.addPoint(new DPoint(0, 0));
		double initRad = 1;
		this.addCircleWithIntersections(new Circle(new DPoint(0.0, 0.0), initRad));
		for (int i = 0; i < 6; i++)
		{
			this.addPoint(new DPoint(initRad * Math.cos(i * Math.PI / 3), initRad * Math.sin(i * Math.PI / 3)));
		}
	}
	
	public int getNumEnclosures()
	{
		return enclosures.size();
	}
	
	public Enclosure getEnclosure(int i)
	{
		return enclosures.get(i);
	}
	
	public Enclosure getTopEnclosure(DPoint p)
	{
		for (int i = enclosures.size()-1; i >= 0; i--)
		{
			Enclosure e = enclosures.get(i);
			if (e.contains(p))
				return e;
		}
		return null;
	}
	
	public int getNumPathables()
	{
		return getNumSegments() + getNumRays() + getNumLines() + getNumArcs() + getNumCircles();
	}
	
	public Pathable getPathable(int i)
	{
		if (i < getNumSegments())
			return getSegment(i);
		i -= getNumSegments();
		if (i < getNumRays())
			return getRay(i);
		i -= getNumRays();
		if (i < getNumLines())
			return getLine(i);
		i -= getNumLines();
		if (i < getNumArcs())
			return getArc(i);
		i -= getNumArcs();
		if (i < getNumCircles())
			return getCircle(i);
		return null;
	}

	public int getNumSegments()
	{
		return segments.size();
	}

	public Segment getSegment(int i)
	{
		return segments.get(i);
	}

	public int getNumRays()
	{
		return rays.size();
	}

	public Ray getRay(int i)
	{
		return rays.get(i);
	}

	public int getNumLines()
	{
		return lines.size();
	}

	public Line getLine(int i)
	{
		return lines.get(i);
	}

	public int getNumArcs()
	{
		return arcs.size();
	}

	public Arc getArc(int i)
	{
		return arcs.get(i);
	}

	public int getNumCircles()
	{
		return circles.size();
	}

	public Circle getCircle(int i)
	{
		return circles.get(i);
	}

	public int getNumPoints()
	{
		return points.size();
	}

	public DPoint getPoint(int i)
	{
		return points.get(i);
	}

	public double getMark()
	{
		return mark;
	}

	public AlDrawState getPrevious()
	{
		return previous;
	}

	public AlDrawState getNext()
	{
		return next;
	}

	public void setMark(double m)
	{
		mark = m;
	}

	public void setPrevious(AlDrawState s)
	{
		previous = s;
	}

	public void setNext(AlDrawState s)
	{
		next = s;
	}
	
	public void addEnclosure(Enclosure e)
	{
		enclosures.add(e);
	}

	private void addAllIntersections(AbstractLine n)
	{
		for (int i = 0; i < this.getNumSegments(); i++)
		{
			Segment s = this.getSegment(i);
			this.addPoint(n.intersection(s));
		}
		for (int i = 0; i < this.getNumRays(); i++)
		{
			Ray r = this.getRay(i);
			this.addPoint(n.intersection(r));
		}
		for (int i = 0; i < this.getNumLines(); i++)
		{
			Line l = this.getLine(i);
			this.addPoint(n.intersection(l));
		}
		for (int i = 0; i < this.getNumArcs(); i++)
		{
			Arc a = this.getArc(i);
			this.addPoint(n.intersection(a));
		}
		for (int i = 0; i < this.getNumCircles(); i++)
		{
			Circle c = this.getCircle(i);
			this.addPoint(n.intersection(c));
		}
	}

	private void addAllIntersections(Circle n)
	{
		for (int i = 0; i < this.getNumSegments(); i++)
		{
			Segment s = this.getSegment(i);
			this.addPoint(n.intersection(s));
		}
		for (int i = 0; i < this.getNumRays(); i++)
		{
			Ray r = this.getRay(i);
			this.addPoint(n.intersection(r));
		}
		for (int i = 0; i < this.getNumLines(); i++)
		{
			Line l = this.getLine(i);
			this.addPoint(n.intersection(l));
		}
		for (int i = 0; i < this.getNumArcs(); i++)
		{
			Arc a = this.getArc(i);
			this.addPoint(n.intersection(a));
		}
		for (int i = 0; i < this.getNumCircles(); i++)
		{
			Circle c = this.getCircle(i);
			this.addPoint(n.intersection(c));
		}
	}

	public void addSegmentWithIntersections(Segment newSegment)
	{
		if (!Utils.doublesEqual(newSegment.getP1().dist(newSegment.getP2()), 0))
		{
			addAllIntersections(newSegment);
			addSegmentWithoutIntersections(newSegment);
		}
	}
	
	public void addSegmentWithoutIntersections(Segment newSegment)
	{
		if (Utils.doublesEqual(newSegment.getLength(), 0))
			return;
		Iterator<Line> lineIterator = lines.iterator();
		while(lineIterator.hasNext())
		{
			Line l = lineIterator.next();
			if (newSegment.same(l))
			{
				return;
			}
		}
		Iterator<Ray> rayIterator = rays.iterator();
		while(rayIterator.hasNext())
		{
			Ray r = rayIterator.next();
			if (newSegment.same(r))
			{
				if (r.contains(newSegment.getP1()) && r.contains(newSegment.getP2()))
				{
					return;
				}
				else if (r.contains(newSegment.getP1()))
				{
					rayIterator.remove();
					addRayWithoutIntersections(new Ray(newSegment.getP2(), r.getAngle()));
					return;
				}
				else if (r.contains(newSegment.getP2()))
				{
					rayIterator.remove();
					addRayWithoutIntersections(new Ray(newSegment.getP1(), r.getAngle()));
					return;
				}
			}
		}
		Iterator<Segment> segmentIterator = segments.iterator();
		while(segmentIterator.hasNext())
		{
			Segment s = segmentIterator.next();
			if (newSegment.same(s))
			{
				if (newSegment.isIn(s))
				{
					return;
				}
				else if (s.isIn(newSegment))
				{
					segmentIterator.remove();
				}
				else if (newSegment.contains(s.getP1()) && s.contains(newSegment.getP1()))
				{
					newSegment = new Segment(newSegment.getP2(), s.getP2());
					segmentIterator.remove();
				}
				else if (newSegment.contains(s.getP1()) && s.contains(newSegment.getP2()))
				{
					newSegment = new Segment(newSegment.getP1(), s.getP2());
					segmentIterator.remove();
				}
				else if (newSegment.contains(s.getP2()) && s.contains(newSegment.getP1()))
				{
					newSegment = new Segment(newSegment.getP2(), s.getP1());
					segmentIterator.remove();
				}
				else if (newSegment.contains(s.getP2()) && s.contains(newSegment.getP2()))
				{
					newSegment = new Segment(newSegment.getP1(), s.getP1());
					segmentIterator.remove();
				}
			}
		}
		segments.add(newSegment);
	}

	public void addRayWithIntersections(Ray newRay)
	{
		addAllIntersections(newRay);
		addRayWithoutIntersections(newRay);
	}
	
	public void addRayWithoutIntersections(Ray newRay)
	{
		Iterator<Line> lineIterator = lines.iterator();
		while(lineIterator.hasNext())
		{
			Line l = lineIterator.next();
			if (newRay.same(l))
			{
				return;
			}
		}
		Iterator<Ray> rayIterator = rays.iterator();
		while(rayIterator.hasNext())
		{
			Ray r = rayIterator.next();
			if (newRay.same(r))
			{
				if (Utils.doublesEqual(newRay.getAngle(), r.getAngle()))
				{
					if (newRay.contains(r.getStart()))
					{
						rayIterator.remove();
					}
					else
					{
						return;
					}
				}
				else
				{
					if (newRay.contains(r.getStart()))
					{
						rayIterator.remove();
						addLineWithoutIntersections(new Line(newRay.getSlope(), newRay.getIntercept()));
						return;
					}
				}
			}
		}
		Iterator<Segment> segmentIterator = segments.iterator();
		while(segmentIterator.hasNext())
		{
			Segment s = segmentIterator.next();
			if (newRay.same(s))
			{
				if (newRay.contains(s.getP1()) && newRay.contains(s.getP2()))
				{
					segmentIterator.remove();
				}
				else if (newRay.contains(s.getP1()))
				{
					newRay = new Ray(s.getP2(), newRay.getAngle());
					segmentIterator.remove();
				}
				else if (newRay.contains(s.getP2()))
				{
					newRay = new Ray(s.getP1(), newRay.getAngle());
					segmentIterator.remove();
				}
			}
		}
		rays.add(newRay);
	}

	public void addLineWithIntersections(Line newLine)
	{
		addAllIntersections(newLine);
		addLineWithoutIntersections(newLine);
	}
	
	public void addLineWithoutIntersections(Line newLine)
	{
		Iterator<Line> lineIterator = lines.iterator();
		while(lineIterator.hasNext())
		{
			Line l = lineIterator.next();
			if (newLine.same(l))
			{
				return;
			}
		}
		Iterator<Ray> rayIterator = rays.iterator();
		while(rayIterator.hasNext())
		{
			Ray r = rayIterator.next();
			if (newLine.same(r))
			{
				rayIterator.remove();
			}
		}
		Iterator<Segment> segmentIterator = segments.iterator();
		while(segmentIterator.hasNext())
		{
			Segment s = segmentIterator.next();
			if (newLine.same(s))
			{
				segmentIterator.remove();
			}
		}
		lines.add(newLine);
	}

	public void addArcWithIntersections(Arc newArc)
	{
		addAllIntersections(newArc);
		addArcWithoutIntersections(newArc);
	}
	
	public void addArcWithoutIntersections(Arc newArc)
	{
		Iterator<Circle> circleIterator = circles.iterator();
		while (circleIterator.hasNext())
		{
			Circle c = circleIterator.next();
			if (newArc.same(c))
			{
				return;
			}
		}
		Iterator<Arc> arcIterator = arcs.iterator();
		while (arcIterator.hasNext())
		{
			Arc a = arcIterator.next();
			if (newArc.same(a))
			{
				if(newArc.isIn(a))
				{
					return;
				}
				else if (a.isIn(newArc))
				{
					arcIterator.remove();
				}
				else if (Utils.doublesEqual(newArc.getStart(), a.getEnd()) && Utils.doublesEqual(newArc.getEnd(), a.getStart()))
				{
					arcIterator.remove();
					addCircleWithoutIntersections(new Circle(newArc.getCenter(), newArc.getRadius()));
					return;
				}
				else if (Utils.doublesEqual(newArc.getStart(), a.getEnd()) && Utils.angleDifference(newArc.getEnd(), a.getStart()) < a.getSweep())
				{
					arcIterator.remove();
					addCircleWithoutIntersections(new Circle(newArc.getCenter(), newArc.getRadius()));
					return;
				}
				else if (Utils.angleDifference(newArc.getStart(), a.getStart()) < a.getSweep() && Utils.angleDifference(newArc.getEnd(), a.getStart()) < a.getSweep())
				{
					arcIterator.remove();
					addCircleWithoutIntersections(new Circle(newArc.getCenter(), newArc.getRadius()));
					return;
				}
				else if (Utils.angleDifference(newArc.getStart(), a.getStart()) < a.getSweep())
				{
					arcIterator.remove();
					newArc = new Arc(newArc.getCenter(), newArc.getRadius(), a.getStart(), Utils.angleDifference(newArc.getEnd(), a.getStart()));
				}
				else if (Utils.angleDifference(newArc.getEnd(), a.getStart()) < a.getSweep())
				{
					arcIterator.remove();
					newArc = new Arc(newArc.getCenter(), newArc.getRadius(), newArc.getStart(), Utils.angleDifference(a.getEnd(), newArc.getStart()));
				}
			}
		}
		arcs.add(newArc);
	}

	public void addCircleWithIntersections(Circle newCircle)
	{
		addAllIntersections(newCircle);
		addCircleWithoutIntersections(newCircle);
	}
	
	public void addCircleWithoutIntersections(Circle newCircle)
	{
		Iterator<Circle> circleIterator = circles.iterator();
		while (circleIterator.hasNext())
		{
			Circle c = circleIterator.next();
			if (newCircle.same(c))
			{
				return;
			}
		}
		Iterator<Arc> arcIterator = arcs.iterator();
		while (arcIterator.hasNext())
		{
			Arc a = arcIterator.next();
			if (newCircle.same(a))
			{
				arcIterator.remove();
			}
		}
		circles.add(newCircle);
	}

	public void addPoint(DPoint p)
	{
		if (p != null && !points.contains(p))
		{
			if (controller != null)
				controller.getConverter().resize(p);
			points.add(p);
		}
	}

	public void addPoint(DPoint[] p)
	{
		for (int i = 0; i < p.length; i++)
		{
			addPoint(p[i]);
		}
	}
	
	public void removeEnclosure(DPoint p)
	{
		Iterator<Enclosure> iterator = enclosures.iterator();
		while(iterator.hasNext())
		{
			Enclosure e = iterator.next();
			if (e.contains(p))
				iterator.remove();
		}
	}

	public void removeSegment(Segment s)
	{
		int num = getNumSegments();
		for (int i = 0; i < num; i++)
		{
			Segment t = segments.get(i);
			if (t.equals(s) || t.isIn(s))
			{
				segments.remove(i);
				i--;
				num--;
			} else if (s.isIn(t))
			{
				DPoint t1 = t.getP1();
				DPoint t2 = t.getP2();
				DPoint l1 = s.getP1();
				DPoint l2 = s.getP2();
				if (Math.abs(t2.getX() - t1.getX()) < .001)
				{
					if (t2.getY() < t1.getY())
					{
						t1 = t.getP2();
						t2 = t.getP1();
					}
					if (l2.getY() < l1.getY())
					{
						l1 = s.getP2();
						l2 = s.getP1();
					}

					segments.remove(i);
					addSegmentWithoutIntersections(new Segment(t1, l1));
					addSegmentWithoutIntersections(new Segment(t2, l2));
					i--;
					num--;
				} else
				{
					if (t2.getX() < t1.getX())
					{
						t1 = t.getP2();
						t2 = t.getP1();
					}
					if (l2.getX() < l1.getX())
					{
						l1 = s.getP2();
						l2 = s.getP1();
					}

					segments.remove(i);
					addSegmentWithoutIntersections(new Segment(t1, l1));
					addSegmentWithoutIntersections(new Segment(t2, l2));
					i--;
					num--;
				}
			}
			else if (s.contains(t.getP1()))
			{
				segments.remove(i);
				if (t.contains(s.getP1()))
					addSegmentWithoutIntersections(new Segment(s.getP1(), t.getP2()));
				else
					addSegmentWithoutIntersections(new Segment(s.getP2(), t.getP2()));
				i--;
				num--;
			}
			else if (s.contains(t.getP2()))
			{
				segments.remove(i);
				if (t.contains(s.getP1()))
					addSegmentWithoutIntersections(new Segment(s.getP1(), t.getP1()));
				else
					addSegmentWithoutIntersections(new Segment(s.getP2(), t.getP1()));
				i--;
				num--;
			}
		}
		num = getNumRays();
		for (int i = 0; i < num; i++)
		{
			Ray r = rays.get(i);
			if (s.same(r))
			{
				if (r.contains(s.getP1()) && r.contains(s.getP2()))
				{
					Segment t = new Segment(r.getStart(), s.getP1());
					DPoint start = s.getP2();
					if (t.contains(s.getP2()))
					{
						t = new Segment(r.getStart(), s.getP2());
						start = s.getP1();
					}
					rays.remove(i);
					addRayWithoutIntersections(new Ray(start, r.getAngle()));
					addSegmentWithoutIntersections(t);
					i--;
					num--;
				} else if (r.contains(s.getP1()) && !r.getStart().equals(s.getP1()))
				{
					rays.remove(i);
					addRayWithoutIntersections(new Ray(s.getP1(), r.getAngle()));
					i--;
					num--;
				} else if (r.contains(s.getP2()) && !r.getStart().equals(s.getP2()))
				{
					rays.remove(i);
					addRayWithoutIntersections(new Ray(s.getP2(), r.getAngle()));
					i--;
					num--;
				}
			}
		}
		num = getNumLines();
		for (int i = 0; i < num; i++)
		{
			Line l = lines.get(i);
			if (s.same(l))
			{
				double ang1 = Utils.slopeToAngle(l.getSlope());
				double ang2 = Utils.oppositeAngle(ang1);
				Ray r1 = new Ray(s.getP1(), ang1);
				Ray r2 = new Ray(s.getP2(), ang2);
				if (r1.contains(s.getP2()) || r2.contains(s.getP1()))
				{
					r1 = new Ray(s.getP1(), ang2);
					r2 = new Ray(s.getP2(), ang1);
				}
				lines.remove(i);
				addRayWithoutIntersections(r1);
				addRayWithoutIntersections(r2);
				i--;
				num--;
			}
		}
	}

	public void removeRay(Ray r)
	{
		int num = getNumSegments();
		for (int i = 0; i < num; i++)
		{
			Segment s = segments.get(i);
			if (r.same(s))
			{
				if (r.contains(s.getP1()) && r.contains(s.getP2()))
				{
					segments.remove(i);
					i--;
					num--;
				} else if (r.contains(s.getP1()) && !r.getStart().equals(s.getP1()))
				{
					segments.remove(i);
					addSegmentWithoutIntersections(new Segment(s.getP2(), r.getStart()));
					i--;
					num--;
				} else if (r.contains(s.getP2()) && !r.getStart().equals(s.getP2()))
				{
					segments.remove(i);
					addSegmentWithoutIntersections(new Segment(s.getP1(), r.getStart()));
					i--;
					num--;
				}
			}
		}
		num = getNumRays();
		for (int i = 0; i < num; i++)
		{
			Ray t = rays.get(i);
			if (r.same(t))
			{
				if (Utils.doublesEqual(r.getAngle(), t.getAngle()))
				{
					if (r.contains(t.getStart()))
					{
						rays.remove(i);
						i--;
						num--;
					} else
					{
						rays.remove(i);
						addSegmentWithoutIntersections(new Segment(r.getStart(), t.getStart()));
						i--;
						num--;
					}
				} else
				{
					if (r.contains(t.getStart()) && !r.getStart().equals(t.getStart()))
					{
						rays.remove(i);
						addRayWithoutIntersections(new Ray(r.getStart(), t.getAngle()));
						i--;
						num--;
					}
				}

			}
		}
		num = getNumLines();
		for (int i = 0; i < num; i++)
		{
			Line l = lines.get(i);
			if (r.same(l))
			{
				lines.remove(l);
				addRayWithoutIntersections(new Ray(r.getStart(), Utils.oppositeAngle(r.getAngle())));
				i--;
				num--;
			}
		}
	}

	public void removeLine(Line l)
	{
		int num = getNumSegments();
		for (int i = 0; i < num; i++)
		{
			Segment s = segments.get(i);
			if (l.same(s))
			{
				segments.remove(i);
				i--;
				num--;
			}
		}
		num = getNumRays();
		for (int i = 0; i < getNumRays(); i++)
		{
			Ray r = rays.get(i);
			if (l.same(r))
			{
				rays.remove(i);
				i--;
				num--;
			}
		}
		num = getNumLines();
		for (int i = 0; i < getNumLines(); i++)
		{
			Line x = lines.get(i);
			if (l.same(x))
			{
				lines.remove(i);
				i--;
				num--;
			}
		}
	}

	public void removeArc(Arc newArc)
	{
		int num = getNumArcs();
		for (int i = 0; i < num; i++)
		{
			Arc a = arcs.get(i);
			if (newArc.same(a))
			{
				if (newArc.equals(a))
				{
					arcs.remove(i);
					i--;
					num--;
				}
				else if (a.isIn(newArc))
				{
					arcs.remove(i);
					i--;
					num--;
				}
				else if (newArc.isIn(a))
				{
					Arc b = new Arc(newArc.getCenter(), newArc.getRadius(), a.getStart(), Utils.angleDifference(newArc.getStart(), a.getStart()));
					Arc c = new Arc(newArc.getCenter(), newArc.getRadius(), newArc.getEnd(), Utils.angleDifference(a.getEnd(), newArc.getEnd()));
					arcs.remove(i);
					arcs.add(b);
					arcs.add(c);
					i--;
					num--;
				}
				else if (Utils.angleDifference(newArc.getStart(), a.getStart()) < a.getSweep() && Utils.angleDifference(newArc.getEnd(), a.getStart()) < a.getSweep())
				{
					arcs.remove(i);
					arcs.add(new Arc(newArc.getCenter(), newArc.getRadius(), newArc.getEnd(), Utils.angleDifference(newArc.getStart(), newArc.getEnd())));
					i--;
					num--;
				}
				else if (Utils.angleDifference(newArc.getStart(), a.getStart()) < a.getSweep())
				{
					arcs.remove(i);
					arcs.add(new Arc(newArc.getCenter(), newArc.getRadius(), a.getStart(), Utils.angleDifference(newArc.getStart(), a.getStart())));
					i--;
					num--;
				}
				else if (Utils.angleDifference(newArc.getEnd(), a.getStart()) < a.getSweep())
				{
					arcs.remove(i);
					arcs.add(new Arc(newArc.getCenter(), newArc.getRadius(), newArc.getEnd(), Utils.angleDifference(a.getEnd(), newArc.getEnd())));
					i--;
					num--;
				}
			}
		}
		num = getNumCircles();
		for (int i = 0; i < num; i++)
		{
			Circle c = getCircle(i);
			if (newArc.same(c))
			{
				circles.remove(i);
				addArcWithoutIntersections(new Arc(newArc.getCenter(), newArc.getRadius(), newArc.getEnd(), Utils.angleDifference(2 * Math.PI, newArc.getSweep())));
				i--;
				num--;
			}
		}
	}

	public void removeCircle(Circle a)
	{
		int num = getNumArcs();
		for (int i = 0; i < num; i++)
		{
			Arc t = arcs.get(i);
			if (a.same(t))
			{
				arcs.remove(i);
				i--;
				num--;
			}
		}
		num = getNumCircles();
		for (int i = 0; i < num; i++)
		{
			Circle c = circles.get(i);
			if (a.same(c))
			{
				circles.remove(i);
				i--;
				num--;
			}
		}
	}

	public void removePoint(DPoint p)
	{
		DPoint t = points.get(0);
		double minDist = p.dist(t);
		int index = 0;
		for (int i = 1; i < points.size(); i++)
		{
			t = points.get(i);
			if (p.dist(t) < minDist)
			{
				minDist = p.dist(t);
				index = i;
			}
		}
		points.remove(index);
	}

	public void paintState(Graphics g, CoordinateConverter c, PreferenceOptions prefOpts, boolean showPoints)
	{
		for (int i = 0; i < this.getNumEnclosures(); i++)
		{
			Enclosure e = this.getEnclosure(i);
			e.draw(g, c, prefOpts);
		}
		g.setColor(Color.BLACK);
		for (int i = 0; i < this.getNumSegments(); i++)
		{
			Segment l = this.getSegment(i);
			l.draw(g, c, prefOpts);
		}
		for (int i = 0; i < this.getNumRays(); i++)
		{
			Ray r = this.getRay(i);
			r.draw(g, c, prefOpts);
		}
		for (int i = 0; i < this.getNumLines(); i++)
		{
			Line l = this.getLine(i);
			l.draw(g, c, prefOpts);
		}
		for (int i = 0; i < this.getNumArcs(); i++)
		{
			Arc a = this.getArc(i);
			a.draw(g, c, prefOpts);
		}
		for (int i = 0; i < this.getNumCircles(); i++)
		{
			Circle a = this.getCircle(i);
			a.draw(g, c, prefOpts);
		}
		if (showPoints){
			for (int i = 0; i < this.getNumPoints(); i++)
			{
				DPoint p = this.getPoint(i);
				p.draw(g, c, prefOpts);
			}
		}
	}

	public DPoint nearestPoint(DPoint click)
	{
		DPoint ret = this.getPoint(0);
		double minDist = click.dist(ret);
		for (int i = 1; i < this.getNumPoints(); i++)
		{
			DPoint p = this.getPoint(i);
			if (click.dist(p) < minDist)
			{
				ret = p;
				minDist = click.dist(ret);
			}
		}
		return ret;
	}
}
