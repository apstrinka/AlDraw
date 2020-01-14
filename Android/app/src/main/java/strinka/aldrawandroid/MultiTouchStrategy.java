package strinka.aldrawandroid;

import java.util.ArrayList;

import android.view.MotionEvent;

public class MultiTouchStrategy
{
	AlDrawController controller;
	CoordinateConverter converter;
	ArrayList<DPoint> screenPoints;
	ArrayList<DPoint> abstractPoints;
	
	public MultiTouchStrategy(AlDrawController controller){
		this.controller = controller;
		converter = controller.getConverter();
		screenPoints = new ArrayList<DPoint>();
		abstractPoints = new ArrayList<DPoint>();
	}
	
	public void onTouchEvent(MotionEvent e){
		if (e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
			actionPointerDown(e);
		} else if (e.getActionMasked() == MotionEvent.ACTION_MOVE){
			actionMove(e);
		} else if (e.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
			actionPointerUp(e);
		} else if (e.getActionMasked() == MotionEvent.ACTION_UP){
			actionUp(e);
		} 
	}
	
	public void actionPointerDown(MotionEvent e){
		abstractPoints.clear();
		for (int i = 0; i < e.getPointerCount(); i++){
			DPoint p = new DPoint(e.getX(i), e.getY(i));
			abstractPoints.add(converter.screenToAbstractCoord(p));
		}
	}
	
	public void actionMove(MotionEvent e){
		screenPoints.clear();
		for (int i = 0; i < e.getPointerCount(); i++){
			DPoint p = new DPoint(e.getX(i), e.getY(i));
			screenPoints.add(p);
		}
		if (abstractPoints.size() > 1 && screenPoints.size() > 1)
			converter.zoom(abstractPoints.get(0), abstractPoints.get(1), screenPoints.get(0), screenPoints.get(1), controller.getLockRotation());
		else if (abstractPoints.size() > 0 && screenPoints.size() > 0){
			DPoint old = abstractPoints.get(0);
			DPoint diff = converter.screenToAbstractCoord(screenPoints.get(0));
			double xDiff = diff.getX() - old.getX();
			double yDiff = diff.getY() - old.getY();
			converter.setCX(converter.getCX() - xDiff);
			converter.setCY(converter.getCY() - yDiff);
		}
	}

	public void actionPointerUp(MotionEvent e){
		int index = e.getActionIndex();
		abstractPoints.clear();
		for (int i = 0; i < e.getPointerCount(); i++){
			if (i != index){
				DPoint p = new DPoint(e.getX(i), e.getY(i));
				abstractPoints.add(converter.screenToAbstractCoord(p));
			}
		}
	}

	public void actionUp(MotionEvent e){
		abstractPoints.clear();
		screenPoints.clear();
		controller.endMultiTouch();
	}
}
