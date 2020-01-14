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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class AlDrawController implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	AlDrawState currentState;
	AlDrawView view;
	CoordinateConverter converter;
	private int numSel = 0;
	private DPoint[] sel;
	private DPoint[] copy;
	private ArrayList<Drawable> shadows;
	private ArrayList<Selectable> selected;
	private InputStrategy inputStrategy = null;
	private FreePanInputStrategy freePan = new FreePanInputStrategy(this);
	private boolean isDragging = false;
	private DraOpener draOpener;
	private DraSaver draSaver;
	private SvgExporter svgExporter;
	private PngExporter pngExporter;
	private JFileChooser openFileChooser;
	private JFileChooser saveFileChooser;
	private JFileChooser exportSvgFileChooser;
	private JFileChooser exportPngFileChooser;
	private Printer printer;
	private PreferenceSaver prefSaver;
	private PreferenceOptions prefOpts;
	private PreferenceDialog preference;
	private Color color;

	public AlDrawController()
	{
		converter = new CoordinateConverter();
		currentState = new AlDrawState(this);
		currentState.start();
		sel = new DPoint[3];
		copy = new DPoint[0];
		for (int i = 0; i < sel.length; i++)
			sel[i] = null;
		shadows = new ArrayList<Drawable>();
		selected = new ArrayList<Selectable>();
		prefSaver = new PreferenceSaver(".preferences");
		prefOpts = prefSaver.open();
		if (prefOpts == null)
		{
			prefOpts = new PreferenceOptions();
			setDefaultPrefOpts(prefOpts);
			savePreferences();
		}
		preference = new PreferenceDialog(this);
		color = Color.BLACK;
		draOpener = new DraOpener(this);
		draSaver = new DraSaver();
		svgExporter = new SvgExporter();
		openFileChooser = newFileChooser("dra", prefOpts.getDefaultDraDir());
		saveFileChooser = newFileChooser("dra", prefOpts.getDefaultDraDir());
		exportSvgFileChooser = newFileChooser("svg", prefOpts.getDefaultSvgDir());
		exportPngFileChooser = newFileChooser("png", prefOpts.getDefaultPngDir());
		pngExporter = new PngExporter(exportPngFileChooser, this);
		printer = new Printer();

	}

	private JFileChooser newFileChooser(String filter, File defaultDir)
	{
		JFileChooser ret = new JFileChooser(defaultDir);
		ret.addChoosableFileFilter(new FileNameExtensionFilter(filter, filter));
		return ret;
	}
	
	public AlDrawState getCurrentState()
	{
		return currentState;
	}
	
	public AlDrawView getView(){
		return view;
	}
	
	public CoordinateConverter getConverter(){
		return converter;
	}

	ArrayList<Drawable> getShadows()
	{
		return shadows;
	}
	
	ArrayList<Selectable> getSelected()
	{
		return selected;
	}

	boolean getIsDragging()
	{
		return isDragging;
	}

	void setIsDragging(boolean isDragging)
	{
		this.isDragging = isDragging;
	}

	InputStrategy getInputStrategy()
	{
		return inputStrategy;
	}

	String getStrategyDisplay()
	{
		String ret = null;
		if (inputStrategy != null)
		{
			ret = inputStrategy.getDisplay(numSel);
		}
		return ret;
	}

	DPoint getSel(int index)
	{
		return sel[index];
	}

	int getNumSel()
	{
		return numSel;
	}
	
	DPoint getCopy(int index)
	{
		return copy[index];
	}
	
	int getNumCopy()
	{
		return copy.length;
	}

	PreferenceOptions getPrefOpts()
	{
		return prefOpts;
	}

	void setDefaultPrefOpts(PreferenceOptions pref)
	{
		pref.setLineWidth(2.0);
		pref.setDotRadius(6.0);
		pref.setAntialias(AntialiasPreference.OFFWHENDRAGGING);
		pref.setDefaultDraDir(new File("./Saves"));
		pref.setDefaultSvgDir(new File("./Saves"));
		pref.setDefaultPngDir(new File("./Saves"));
	}
	
	Color getColor()
	{
		return color;
	}

	public void addState(AlDrawState newState)
	{
		currentState.setNext(newState);
		newState.setPrevious(currentState);
		currentState = newState;
	}

	void setView(AlDrawView view)
	{
		this.view = view;
	}
	
	void addSelected(Selectable select)
	{
		selected.add(select);
	}
	
	void clearSelected()
	{
		selected = new ArrayList<Selectable>();
		updateView();
	}

	void setShadows(ArrayList<Drawable> shadows)
	{
		this.shadows = shadows;
		updateView();
	}

	public void setInputStrategy(InputStrategy is)
	{
		inputStrategy = is;
		clearSelPoints();
		updateView();
	}

	public DPoint stickPoint(DPoint click)
	{
		DPoint fromScreenClick = converter.screenToAbstractCoord(click);
		DPoint nearest = currentState.nearestPoint(fromScreenClick);
		if (converter.abstractToScreenDist(fromScreenClick.dist(nearest)) < 20)
			return nearest;
		else
			return fromScreenClick;
	}

	public void selNearestPoint(DPoint p)
	{
		selPoint(currentState.nearestPoint(converter.screenToAbstractCoord(p)));
	}

	public void selPoint(DPoint p)
	{
		boolean isNew = true;
		for (int i = 0; i < numSel; i++)
		{
			if (p.equals(sel[i]))
				isNew = false;
		}
		if (isNew && numSel < sel.length)
		{
			sel[numSel] = p;
			numSel++;
		}
		view.updateLabel(getStrategyDisplay());
	}
	
	public void setCopy(DPoint[] copy)
	{
		this.copy = copy;
	}

	JFileChooser getExportSvgFileChooser()
	{
		return exportSvgFileChooser;
	}
	
	JFileChooser getExportPngFileChooser()
	{
		return exportPngFileChooser;
	}

	JFileChooser getOpenFileChooser()
	{
		return openFileChooser;
	}

	JFileChooser getSaveFileChooser()
	{
		return saveFileChooser;
	}

	public void clearSelPoints()
	{
		setIsDragging(false);
		numSel = 0;
		for (int i = 0; i < sel.length; i++)
			sel[i] = null;
		shadows.clear();
		view.updateLabel(getStrategyDisplay());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JMenuItem source = (JMenuItem) (e.getSource());
		String menuText = source.getText();
		if (menuText.equals("New"))
			startNew();
		else if (menuText.equals("Open"))
			open();
		else if (menuText.equals("Save"))
			save();
		else if (menuText.equals("SVG") || menuText.equals("Export"))
			exportSVG();
		else if (menuText.equals("PNG"))
			exportPNG();
		else if (menuText.equals("Print"))
			printer.newJob(currentState, converter);
		else if (menuText.equals("Close"))
			System.exit(0);
		else if (menuText.equals("Undo"))
			undo();
		else if (menuText.equals("Redo"))
			redo();
		else if (menuText.equals("Clear Selections"))
			clearSelected();
		else if (menuText.equals("Preferences"))
			preferences();
		else if (menuText.equals("Zoom In"))
			zoomIn();
		else if (menuText.equals("Zoom Out"))
			zoomOut();
		else if (menuText.equals("Default View"))
			defaultZoom();
		else if (menuText.equals("Auto Zoom"))
			autoZoom();
		else if (menuText.equals("Pan Left"))
			panLeft();
		else if (menuText.equals("Pan Right"))
			panRight();
		else if (menuText.equals("Pan Up"))
			panUp();
		else if (menuText.equals("Pan Down"))
			panDown();
		else if (menuText.equals("Clockwise 15 deg"))
			rotateCW15();
		else if (menuText.equals("Counterclockwise 15 deg"))
			rotateCCW15();
		else if (menuText.equals("Default Rotation"))
			defaultRotation();
		else if (menuText.equals("Random"))
			random();
		else if (menuText.equals("Select Color"))
			selectColor();
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Don't react to a mouse entering
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// Don't react to a mouse exiting
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON2)
			freePan.press(e.getX(), e.getY());
		else if (e.getButton() == MouseEvent.BUTTON3)
			clearSelPoints();
		else if (inputStrategy != null)
		{
			setIsDragging(true);
			inputStrategy.press(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON2)
			freePan.release(e.getX(), e.getY());
		else if (inputStrategy != null)
		{
			inputStrategy.release(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (e.getButton() == 4)
			undo();
		else if ( e.getButton() == 5)
			redo();
		else if (inputStrategy != null)
		{
			inputStrategy.click(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (inputStrategy != null)
		{
			inputStrategy.move(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		freePan.drag(e.getX(), e.getY());
		if (inputStrategy != null)
		{
			inputStrategy.drag(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.getWheelRotation() < 0)
			converter.zoomIn(e.getX(), e.getY());
		else
			converter.zoomOut(e.getX(), e.getY());
		updateView();
	}

	public void updateView()
	{
		view.update(currentState);
	}

	private void startNew()
	{
		AlDrawState newState = new AlDrawState(this);
		newState.start();
		addState(newState);
		clearSelected();
		updateView();
	}

	void savePreferences()
	{
		try
		{
			prefSaver.save(prefOpts);
		} catch (Exception e)
		{
			// TODO Make more user friendly message. See open, save, export
			System.out.println("Error writing preferences file");
		}
	}

	private void open()
	{
		int returnVal = openFileChooser.showOpenDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File file = openFileChooser.getSelectedFile();
				AlDrawState newState = draOpener.open(file, converter);
				addState(newState);
				clearSelPoints();
				clearSelected();
			} catch (IOException e)
			{
				// TODO: Make this error message more user friendly. See save()
				// and exportSVG()
				System.out.println("There was an error opening the file");
			}
		}
		updateView();
	}

	private void save()
	{
		int returnVal = saveFileChooser.showSaveDialog(view);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = saveFileChooser.getSelectedFile();
			String filename = file.getAbsolutePath();
			if (!filename.endsWith(".dra"))
			{
				filename = filename + ".dra";
				file = new File(filename);
			}
			try
			{
				draSaver.save(file, currentState, converter);
			} catch (IOException e)
			{
				// TODO: Make this a more user friendly error message. See
				// exportSVG()
				System.out.println("There was an error saving the file");
			}
		}
	}

	private void exportSVG()
	{
		int returnVal = exportSvgFileChooser.showDialog(view, "Export");
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = exportSvgFileChooser.getSelectedFile();
			String filename = file.getAbsolutePath();
			if (!filename.endsWith(".svg"))
			{
				filename = filename + ".svg";
				file = new File(filename);
			}
			try
			{
				svgExporter.exportSVG(file, currentState, converter);
			} catch (IOException e)
			{
				// TODO: Make this a more user friendly error message. See
				// save()
				System.err.println("There was an error exporting the file");
			}
		}
	}
	
	private void exportPNG()
	{
		pngExporter.updateSizeLabel();
		pngExporter.setVisible(true);
	}

	private void undo()
	{
		clearSelPoints();
		if (currentState.getPrevious() != null)
		{
			currentState = currentState.getPrevious();
			updateView();
		}
	}

	private void redo()
	{
		clearSelPoints();
		if (currentState.getNext() != null)
		{
			currentState = currentState.getNext();
			updateView();
		}
	}

	private void preferences()
	{
		preference.setPrefOpts(prefOpts);
		preference.setVisible(true);
	}

	public void defaultZoom()
	{
		converter.setDefaults();
		updateView();
	}

	public void autoZoom()
	{
		converter.autoZoom(currentState);
		updateView();
	}

	public void zoomIn()
	{
		converter.zoomIn();
		updateView();
	}

	public void zoomOut()
	{
		converter.zoomOut();
		updateView();
	}

	public void panLeft()
	{
		converter.panLeft();
		updateView();
	}

	public void panRight()
	{
		converter.panRight();
		updateView();
	}

	public void panUp()
	{
		converter.panUp();
		updateView();
	}

	public void panDown()
	{
		converter.panDown();
		updateView();
	}

	public void rotateCW15()
	{
		converter.setAngle(Utils.angleSum(converter.getAngle(), Math.toRadians(15)));
		updateView();
	}

	public void rotateCCW15()
	{
		converter.setAngle(Utils.angleSum(converter.getAngle(), -Math.toRadians(15)));
		updateView();
	}

	public void defaultRotation()
	{
		converter.setDefaultAngle();
		updateView();
	}
	
	public void selectColor()
	{
		Color temp = JColorChooser.showDialog(view, "Select Color", color);
		if (temp != null)
			color = temp;
	}
	
	public void pickColor(DPoint sel)
	{
		Enclosure e = currentState.getTopEnclosure(sel);
		if (e != null)
			color = e.getColor();
	}

	public double getMark()
	{
		return currentState.getMark();
	}

	public void addSegment(Segment line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addSegmentWithIntersections(line);
		addState(newState);
		updateView();
	}

	public void addRay(Ray ray)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addRayWithIntersections(ray);
		addState(newState);
		updateView();
	}

	public void addLine(Line line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addLineWithIntersections(line);
		addState(newState);
		updateView();
	}

	public void addArc(Arc arc)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addArcWithIntersections(arc);
		addState(newState);
		updateView();
	}

	public void addCircle(Circle circle)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addCircleWithIntersections(circle);
		addState(newState);
		updateView();
	}

	public void setMarkDistance(double dist)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.setMark(dist);
		addState(newState);
		updateView();
	}

	public void addMarkLine(Segment newLine, DPoint newPoint)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addPoint(newPoint);
		newState.addSegmentWithIntersections(newLine);
		addState(newState);
		updateView();
	}

	public void addPoint(DPoint point)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addPoint(point);
		addState(newState);
		updateView();
	}

	public void addPoints(DPoint[] points)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		for (DPoint newPoint : points)
		{
			newState.addPoint(newPoint);
		}
		addState(newState);
		updateView();
	}

	public void addPerpendicularBisector(DPoint mid, Line line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addPoint(mid);
		newState.addLineWithIntersections(line);
		addState(newState);
		updateView();
	}
	
	public void addPointAndCircle(DPoint p, Circle circle){
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.addPoint(p);
		newState.addCircleWithIntersections(circle);
		addState(newState);
		updateView();
	}

	public void addRays(Ray[] rays)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		for (Ray ray : rays)
		{
			newState.addRayWithIntersections(ray);
		}

		addState(newState);
		updateView();
	}

	public void removeSegment(Segment line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeSegment(line);
		addState(newState);
		updateView();
	}

	public void removeRay(Ray ray)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeRay(ray);
		addState(newState);
		updateView();
	}

	public void removeLine(Line line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeLine(line);
		addState(newState);
		updateView();
	}

	public void removeArc(Arc arc)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeArc(arc);
		addState(newState);
		updateView();
	}

	public void removeCircle(Circle circle)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeCircle(circle);
		addState(newState);
		updateView();
	}

	public void removePoint(DPoint point)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(currentState);
		newState.removePoint(point);
		addState(newState);
		updateView();
	}
	
	public void addEnclosure(Enclosure enclosure)
	{
		AlDrawState newState = new AlDrawState(currentState);
		newState.addEnclosure(enclosure);
		addState(newState);
		updateView();
	}
	
	public void removeEnclosure(DPoint p)
	{
		AlDrawState newState = new AlDrawState(currentState);
		newState.removeEnclosure(p);
		addState(newState);
		updateView();
	}
	
	void random()
	{
		Random r = new Random();
		int operation = r.nextInt(3);
		int q1 = r.nextInt(currentState.getNumPoints());
		int q2 = r.nextInt(currentState.getNumPoints() - 1);
		if (q2 >= q1)
			q2++;
		DPoint p1 = currentState.getPoint(q1);
		DPoint p2 = currentState.getPoint(q2);
		if (operation == 0)//Add segment
		{
			addSegment(new Segment(p1, p2));
		}
		else if (operation == 1)//Add line
		{
			addLine(new Line(p1, p2));
		}
		else if (operation == 2)//Add circle
		{
			addCircle(new Circle(p1, p1.dist(p2)));
		}
	}
}