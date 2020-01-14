package strinka.aldrawandroid;

import java.util.ArrayList;

import android.view.MotionEvent;

public abstract class InputStrategy
{
	String name;
	int pointsNeeded;
	AlDrawController controller;
	CoordinateConverter converter;
	
	public InputStrategy(String name, int pointsNeeded, AlDrawController controller){
		setName(name);
		setPointsNeeded(pointsNeeded);
		setAlDrawController(controller);
		converter = controller.getConverter();
	}
	
	private void setName(String name){
		this.name = name;
	}

	private void setPointsNeeded(int pointsNeeded){
		this.pointsNeeded = pointsNeeded;
	}

	private void setAlDrawController(AlDrawController controller){
		this.controller = controller;
	}

	public String getName(){
		return name;
	}

	public int getPointsNeeded(){
		return pointsNeeded;
	}
	
	abstract void endHook();

	ArrayList<Drawable> shadowHook(DPoint p1){
		return new ArrayList<Drawable>();
	}

	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		return shadowHook(p1);
	}

	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3){
		return shadowHook(p1, p2);
	}
	
	void addShadows(DPoint touch){
		touch = converter.screenToAbstractCoord(touch);
		if (controller.getNumSelPoints() == 0){
			controller.setShadows(shadowHook(touch));
		} else if (controller.getNumSelPoints() == 1){
			controller.setShadows(shadowHook(controller.getSelPoint(0), touch));
		} else if (controller.getNumSelPoints() == 2){
			controller.setShadows(shadowHook(controller.getSelPoint(0), controller.getSelPoint(1), touch));
		}
	}
	
	public boolean onTouchEvent(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN ){
			pointerDown(e);
		} else if (e.getAction() == MotionEvent.ACTION_MOVE){
			pointerMove(e);
		} else if (e.getAction() == MotionEvent.ACTION_UP){
			pointerUp(e);
		}
		return true;
	}
	
	void pointerDown(MotionEvent e) {
		DPoint touch = new DPoint(e.getX(), e.getY());
		if (controller.getNumSelPoints() == 0 && pointsNeeded > 1){
			controller.selNearestPoint(touch);
		}
		controller.setTouchPoint(touch);
		addShadows(touch);
	}
	
	void pointerMove(MotionEvent e) {
		DPoint touch = new DPoint(e.getX(), e.getY());
		controller.setTouchPoint(touch);
		addShadows(touch);
	}
	
	void pointerUp(MotionEvent e){
		DPoint touch = new DPoint(e.getX(), e.getY());
		controller.selNearestPoint(touch);
		if (controller.getNumSelPoints() >= pointsNeeded){
			endHook();
			controller.clearSelPoints();
		}
		controller.setShadows(new ArrayList<Drawable>());
		controller.clearTouchPoint();
	}
}
