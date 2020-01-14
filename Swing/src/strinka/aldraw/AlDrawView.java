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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.SwingConstants;

public class AlDrawView extends JComponent implements ComponentListener
{
	static final long serialVersionUID = 0;

	AlDrawController controller;
	CoordinateConverter converter;
	AlDrawState state;
	JLabel label;

	public AlDrawView(AlDrawController controller)
	{
		setController(controller);
		converter = controller.getConverter();
		state = controller.getCurrentState();

		label = new JLabel("Welcome to AlDraw!", SwingConstants.CENTER);
		label.setOpaque(true);
		label.setBackground(new Color(200, 200, 255));
		label.setFont(new Font(null, Font.BOLD, 20));
		label.setPreferredSize(new Dimension(converter.getWidth() + 1, 30));

		JFrame frame = new JFrame();
		frame.getContentPane().add(label, BorderLayout.NORTH);
		frame.getContentPane().add(this, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(converter.getWidth() + 1, converter.getHeight() + 1));

		JMenuBar menuBar = MenuBuilder.parseMenus(controller);//setupMenus();

		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setTitle("AlDraw");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		this.addComponentListener(this);
	}

	CoordinateConverter getCoordinateConverter()
	{
		return converter;
	}

	void setController(AlDrawController controller)
	{
		this.controller = controller;
		this.addMouseListener(controller);
		this.addMouseMotionListener(controller);
		this.addMouseWheelListener(controller);
	}

	void setState(AlDrawState state)
	{
		this.state = state;
	}

	@Override
	public void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		g.setStroke(new BasicStroke((float) controller.getPrefOpts().getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Object value_antialias = RenderingHints.VALUE_ANTIALIAS_OFF;
		if (controller.getPrefOpts().getAntialias() == AntialiasPreference.ON || (controller.getPrefOpts().getAntialias() == AntialiasPreference.OFFWHENDRAGGING && !controller.getIsDragging()))
		{
			value_antialias = RenderingHints.VALUE_ANTIALIAS_ON;
		}
		g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, value_antialias));
		g.setColor(Color.white);
		g.fillRect(0, 0, converter.getWidth(), converter.getHeight());
		g.setColor(Color.black);
		state.paintState(g, converter, controller.getPrefOpts(), true);
		g.setColor(Color.red);
		for (int i = 0; i < controller.getNumSel(); i++)
		{
			DPoint p = new DPoint(controller.getSel(i), Color.RED);
			p.draw(g, converter, controller.getPrefOpts());
		}
		if (controller.getInputStrategy() != null && (controller.getInputStrategy().getName().startsWith("Select")
				|| controller.getInputStrategy().getName().equals("Copy") || controller.getInputStrategy().getName().equals("Paste")))
		{
			g.setColor(Color.red);
			ArrayList<Selectable> selected = controller.getSelected();
			for (int i = 0; i < selected.size(); i++)
			{
				Selectable s = selected.get(i);
				if (s instanceof DPoint)
				{
					s = new DPoint((DPoint)s, Color.RED);
				}
				s.draw(g, converter, controller.getPrefOpts());
			}
			for (int i = 0; i < controller.getNumCopy(); i++)
			{
				DPoint p = new DPoint(controller.getCopy(i), Color.CYAN);
				p.draw(g, converter, controller.getPrefOpts());
			}
		}
		g.setColor(Color.lightGray);
		ArrayList<Drawable> shadows = controller.getShadows();
		for (int i = 0; i < shadows.size(); i++)
		{
			Drawable d = shadows.get(i);
			d.draw(g, converter, controller.getPrefOpts());
		}
	}

	public void updateLabel(String display)
	{
		if (display != null)
		{
			label.setText(display);
		}
	}

	public void update(AlDrawState state)
	{
		setState(state);
		repaint();
	}

	public void update()
	{
		repaint();
	}

	@Override
	public void componentHidden(ComponentEvent arg0)
	{
		// Do nothing
	}

	@Override
	public void componentMoved(ComponentEvent arg0)
	{
		// Do nothing
	}

	@Override
	public void componentResized(ComponentEvent arg0)
	{
		converter.setWidth(this.getWidth());
		converter.setHeight(this.getHeight());
	}

	@Override
	public void componentShown(ComponentEvent arg0)
	{
		// Do nothing
	}
}