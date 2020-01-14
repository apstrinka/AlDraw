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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class InputStrategy implements ActionListener
{
	String name;
	int pointsNeeded;
	String[] actions;
	AlDrawController controller;

	public InputStrategy(String name, int pointsNeeded, String[] actions, AlDrawController alDrawController)
	{
		setName(name);
		setPointsNeeded(pointsNeeded);
		setActions(actions);
		setAlDrawController(alDrawController);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		controller.setInputStrategy(this);
	}

	private void setName(String name)
	{
		this.name = name;
	}

	private void setPointsNeeded(int pointsNeeded)
	{
		this.pointsNeeded = pointsNeeded;
	}

	private void setActions(String[] actions)
	{
		this.actions = actions;
	}

	private void setAlDrawController(AlDrawController alDrawController)
	{
		this.controller = alDrawController;
	}

	public String getName()
	{
		return name;
	}

	public int getPointsNeeded()
	{
		return pointsNeeded;
	}

	public String[] getActions()
	{
		return actions;
	}

	public String getDisplay(int num)
	{
		if (num >= 0 && num < actions.length)
		{
			return name + " - " + actions[num];
		} else
		{
			return name + " - " + "Error";
		}
	}

	abstract void endHook();

	@SuppressWarnings("all")
	ArrayList<Drawable> shadowHook(DPoint p1)
	{
		return new ArrayList<Drawable>();
	}

	@SuppressWarnings("all")
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2)
	{
		return shadowHook(p1);
	}

	@SuppressWarnings("all")
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3)
	{
		return shadowHook(p1, p2);
	}

	void press(int x, int y)
	{
		controller.selNearestPoint(new DPoint(x, y));
		if (controller.getNumSel() == pointsNeeded)
		{
			endHook();
			controller.clearSelPoints();
		}
	}

	void release(int x, int y)
	{
		if (controller.getNumSel() == 1)
		{
			press(x, y);
		}
	}

	void click(int x, int y)
	{
		release(x, y);
	}

	void drag(int x, int y)
	{
		if (controller.getNumSel() == 0)
		{
			ArrayList<Drawable> shadows = shadowHook(controller.stickPoint(new DPoint(x, y)));
			controller.setShadows(shadows);
		} else if (controller.getNumSel() == 1)
		{
			ArrayList<Drawable> shadows = shadowHook(controller.getSel(0), controller.stickPoint(new DPoint(x, y)));
			controller.setShadows(shadows);
		} else if (controller.getNumSel() == 2)
		{
			ArrayList<Drawable> shadows = shadowHook(controller.getSel(0), controller.getSel(1), controller.stickPoint(new DPoint(x, y)));
			controller.setShadows(shadows);
		}
	}

	void move(int x, int y)
	{
		drag(x, y);
	}
}
