package strinka.aldrawandroid;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AlDrawView extends View {
	Context context;
	AlDrawController controller;
	Paint paint;
	CoordinateConverter converter;
	boolean setDefaults = true;
	
	public AlDrawController getController() {
		return controller;
	}
	
	public AlDrawView(Context context) {
		super(context);
		init(context);
	}
	
	public AlDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public AlDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		this.context = context;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setStrokeCap(Paint.Cap.ROUND);
		setPadding(3, 3, 3, 3);
		controller = new AlDrawController(this);
		converter = controller.getConverter();
		converter.setWidth(getMeasuredWidth());
		converter.setHeight(getMeasuredHeight());
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		converter.setWidth(width);
		converter.setHeight(height);
		if (setDefaults) {
			converter.setDefaults();
			setDefaults = false;
		}
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		paint.setColor(0xff000000);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		float width = pref.getInt("pref_line_width_int", 6);
		paint.setStrokeWidth(width);
		controller.getState().paintState(canvas, paint, converter, context, controller.getShowLines(), controller.getShowPoints());
		paint.setColor(Color.LTGRAY);
		for (Drawable d : controller.getShadows()) {
			d.draw(canvas, paint, converter, context);
		}
		for (DPoint p : controller.getSelPoints()) {
			p.draw(canvas, paint, converter, context);
		}
		if (controller.getStrategy() != null && (controller.getStrategy().getName().startsWith("Select") || controller.getStrategy().getName().equals("Copy") || controller.getStrategy().getName().equals("Paste"))) {
			paint.setColor(0xffff0000);
			ArrayList<Selectable> selected = controller.getSelected();
			for (int i = 0; i < selected.size(); i++) {
				Selectable s = selected.get(i);
				s.draw(canvas, paint, converter, context);
			}
			for (int i = 0; i < controller.getNumCopy(); i++) {
				DPoint p = controller.getCopyPoint(i);
				p.draw(canvas, paint, converter, context);
			}
		}
		if (controller.getTouchPoint() != null) {
			controller.getTouchPoint().draw(canvas, paint, converter, context);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return controller.onTouchEvent(e);
	}
}
