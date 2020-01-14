package strinka.aldrawandroid;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Color;
import android.view.MotionEvent;

public class AlDrawController
{
	AlDrawState state;
	AlDrawView view;
	CoordinateConverter converter;
	ArrayList<DPoint> selPoints;
	DPoint touchPoint;
	DPoint[] copyPoints = new DPoint[0];
	ArrayList<Drawable> shadows;
	ArrayList<Selectable> selected;
	InputStrategy strategy;
	ArrayList<InputStrategy> strategies;
	MultiTouchStrategy multiTouch;
	boolean multiTouchMode = false;
	int color;
	boolean showLines = true;
	boolean showPoints = true;
	boolean lockRotation = false;
	
	AlDrawController(AlDrawView view){
		state = new AlDrawState(this);
		state.start();
		this.view = view;
		converter = new CoordinateConverter();
		selPoints = new ArrayList<DPoint>();
		shadows = new ArrayList<Drawable>();
		selected = new ArrayList<Selectable>();
		strategy = new DrawSegmentInputStrategy(this);
		multiTouch = new MultiTouchStrategy(this);
		initializeStrategies();
		color = Color.RED;
	}
	
	private void initializeStrategies() {
		strategies = new ArrayList<InputStrategy>();
		strategies.add(new DrawSegmentInputStrategy(this));
		strategies.add(new DrawRayInputStrategy(this));
		strategies.add(new DrawLineInputStrategy(this));
		strategies.add(new DrawArcInputStrategy(this));
		strategies.add(new DrawCircleInputStrategy(this));
		strategies.add(new SetMarkInputStrategy(this));
		strategies.add(new MarkSegmentInputStrategy(this));
		strategies.add(new MarkCircleInputStrategy(this));
		strategies.add(new FindMidpointInputStrategy(this));
		strategies.add(new TrisectLineInputStrategy(this));
		strategies.add(new PerpendicularBisectorInputStrategy(this));
		strategies.add(new AngleBisectorInputStrategy(this));
		strategies.add(new TangentLineInputStrategy(this));
		strategies.add(new ParallelLineInputStrategy(this));
		strategies.add(new CircumscribeTriangleInputStrategy(this));
		strategies.add(new DeleteSegmentInputStrategy(this));
		strategies.add(new DeleteRayInputStrategy(this));
		strategies.add(new DeleteLineInputStrategy(this));
		strategies.add(new DeleteArcInputStrategy(this));
		strategies.add(new DeleteCircleInputStrategy(this));
		strategies.add(new DeletePointInputStrategy(this));
		strategies.add(new FillColorInputStrategy(this));
		strategies.add(new EraseColorInputStrategy(this));
		strategies.add(new PickColorInputStrategy(this));
		strategies.add(new CopyInputStrategy(this));
		strategies.add(new PasteInputStrategy(this));
		strategies.add(new SelectSegmentInputStrategy(this));
		strategies.add(new SelectRayInputStrategy(this));
		strategies.add(new SelectLineInputStrategy(this));
		strategies.add(new SelectArcInputStrategy(this));
		strategies.add(new SelectCircleInputStrategy(this));
		strategies.add(new SelectPointInputStrategy(this));
	}

	public AlDrawState getState(){
		return state;
	}
	
	public CoordinateConverter getConverter(){
		return converter;
	}
	
	public double getMark(){
		return state.getMark();
	}

	public ArrayList<DPoint> getSelPoints(){
		return selPoints;
	}
	
	public int getNumSelPoints(){
		return selPoints.size();
	}
	
	public DPoint getSelPoint(int index){
		return selPoints.get(index);
	}
	
	public void selPoint(DPoint p){
		if (p == null)
			return;
		boolean isNew = true;
		for (DPoint q : selPoints){
			if (p.equals(q)){
				isNew = false;
				break;
			}
		}
		if (isNew){
			selPoints.add(new DPoint(p, Color.RED));
		}
		view.invalidate();
	}

	public void selNearestPoint(DPoint p){
		selPoint(state.nearestPoint(converter.screenToAbstractCoord(p)));
	}

	public void clearSelPoints(){
		selPoints.clear();
	}

	public DPoint getTouchPoint(){
		return touchPoint;
	}

	public void setTouchPoint(DPoint p){
		DPoint nearest = state.nearestPoint(converter.screenToAbstractCoord(p));
		if (nearest != null)
			touchPoint = new DPoint(nearest, Color.RED);
	}

	public void clearTouchPoint(){
		touchPoint = null;
	}

	public void setCopyPoints(DPoint[] copy)
	{
		copyPoints = copy;
		for (DPoint p : copyPoints){
			p.setColor(Color.LTGRAY);
		}
	}

	public DPoint getCopyPoint(int index){
		return copyPoints[index];
	}

	public int getNumCopy()
	{
		return copyPoints.length;
	}

	public ArrayList<Drawable> getShadows(){
		return shadows;
	}

	public void setShadows(ArrayList<Drawable> shadows){
		this.shadows = shadows;
	}
	
	public void addSelected(Selectable select)
	{
		if (select instanceof DPoint){
			((DPoint) select).setColor(Color.RED);
		}
		selected.add(select);
	}
	
	void clearSelected()
	{
		selected = new ArrayList<Selectable>();
		view.invalidate();
	}

	public ArrayList<Selectable> getSelected()
	{
		return selected;
	}
	
	public void setStrategy(String name){
		for (InputStrategy s : strategies){
			if (s.getName().equals(name)){
				strategy = s;
				break;
			}
		}
	}
	
	public InputStrategy getStrategy(){
		return strategy;
	}
	
	public boolean onTouchEvent(MotionEvent e) {
		if (multiTouchMode){
			multiTouch.onTouchEvent(e);
		} else if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
			startMultiTouch(e);
		} else {
			strategy.onTouchEvent(e);
		}
		view.invalidate();
		return true;
    }
	
	public void startMultiTouch(MotionEvent e){
		clearSelPoints();
		clearTouchPoint();
		shadows = new ArrayList<Drawable>();
		multiTouchMode = true;
		multiTouch.actionPointerDown(e);
	}
	
	public void endMultiTouch(){
		multiTouchMode = false;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public void pickColor(DPoint sel)
	{
		Enclosure e = state.getTopEnclosure(sel);
		if (e != null)
			color = e.getColor();
	}
	
	public boolean getShowLines(){
		return showLines;
	}
	
	public void setShowLines(boolean b){
		showLines = b;
		view.invalidate();
	}
	
	public boolean getShowPoints(){
		return showPoints;
	}
	
	public void setShowPoints(boolean b){
		showPoints = b;
		view.invalidate();
	}
	
	public boolean getLockRotation(){
		return lockRotation;
	}
	
	public void setLockRotation(boolean b){
		lockRotation = b;
	}
	
	public void addState(AlDrawState newState){
		if (state != null){
			state.setNext(newState);
			newState.setPrev(state);
		}
		state = newState;
		autosave();
		view.invalidate();
	}
	
	private void autosave(){
		File dir = view.context.getExternalFilesDir(null);
		File file = new File(dir, "autosave.dra");
		try {
			DraSaver.save(file, state, converter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startNew(){
		clearSelPoints();
		clearSelected();
		AlDrawState newState = new AlDrawState(this);
		newState.start();
		addState(newState);
		converter.setDefaults();
	}
	
	public void undo(){
		clearSelPoints();
		if (state.getPrev() != null)
		{
			state = state.getPrev();
		}
	}

	public void redo(){
		clearSelPoints();
		if (state.getNext() != null)
		{
			state = state.getNext();
		}
	}
	
	public void autoZoom(){
		converter.autoZoom(state);
		view.invalidate();
	}
	
	public void defaultView(){
		converter.setDefaults();
		view.invalidate();
	}
	
	public void defaultRotation(){
		converter.setDefaultAngle();
		view.invalidate();
	}
	
	//d is in radians
	public void rotateBy(double d){
		converter.setAngle(converter.getAngle() + d);
		view.invalidate();
	}
	
	public void copy(DPoint sel1, DPoint sel2){
		if (this.getNumCopy() > 1)
		{
			AlDrawState newState = new AlDrawState(state);
			for (int i = 0; i < selected.size(); i++)
			{
				Selectable s = selected.get(i);
				s = s.copy(copyPoints[0], copyPoints[1] ,sel1, sel2);
				s.addToState(newState);
			}
			addState(newState);
		}
	}

	public void addSegment(Segment s){
		AlDrawState newState = new AlDrawState(state);
		newState.addSegmentWithIntersections(s);
		addState(newState);
	}
	
	public void addRay(Ray r){
		AlDrawState newState = new AlDrawState(state);
		newState.addRayWithIntersections(r);
		addState(newState);
	}
	
	public void addLine(Line l){
		AlDrawState newState = new AlDrawState(state);
		newState.addLineWithIntersections(l);
		addState(newState);
	}
	
	public void addArc(Arc a){
		AlDrawState newState = new AlDrawState(state);
		newState.addArcWithIntersections(a);
		addState(newState);
	}
	
	public void addCircle(Circle c){
		AlDrawState newState = new AlDrawState(state);
		newState.addCircleWithIntersections(c);
		addState(newState);
	}
	
	public void setMarkDistance(double dist)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		newState.setMark(dist);
		addState(newState);
	}

	public void addMarkLine(Segment newLine, DPoint newPoint)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		newState.addPoint(newPoint);
		newState.addSegmentWithIntersections(newLine);
		addState(newState);
	}
	
	public void addPoint(DPoint point)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		newState.addPoint(point);
		addState(newState);
	}

	public void addPoints(DPoint[] points)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		for (DPoint newPoint : points)
		{
			newState.addPoint(newPoint);
		}
		addState(newState);
	}

	public void addPerpendicularBisector(DPoint mid, Line line)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		newState.addPoint(mid);
		newState.addLineWithIntersections(line);
		addState(newState);
	}
	
	public void addPointAndCircle(DPoint p, Circle circle)
	{
		clearSelPoints();
		AlDrawState newState = new AlDrawState(state);
		newState.addPoint(p);
		newState.addCircleWithIntersections(circle);
		addState(newState);
	}
	
	public void removeSegment(Segment s){
		AlDrawState newState = new AlDrawState(state);
		newState.removeSegment(s);
		addState(newState);
	}
	
	public void removeRay(Ray r){
		AlDrawState newState = new AlDrawState(state);
		newState.removeRay(r);
		addState(newState);
	}
	
	public void removeLine(Line l){
		AlDrawState newState = new AlDrawState(state);
		newState.removeLine(l);
		addState(newState);
	}
	
	public void removeArc(Arc a){
		AlDrawState newState = new AlDrawState(state);
		newState.removeArc(a);
		addState(newState);
	}
	
	public void removeCircle(Circle c){
		AlDrawState newState = new AlDrawState(state);
		newState.removeCircle(c);
		addState(newState);
	}
	
	public void removePoint(DPoint p){
		AlDrawState newState = new AlDrawState(state);
		newState.removePoint(p);
		addState(newState);
	}
	
	public void addEnclosure(Enclosure enclosure)
	{
		AlDrawState newState = new AlDrawState(state);
		newState.addEnclosure(enclosure);
		addState(newState);
	}
	
	public void removeEnclosure(DPoint p)
	{
		AlDrawState newState = new AlDrawState(state);
		newState.removeEnclosure(p);
		addState(newState);
	}
}
