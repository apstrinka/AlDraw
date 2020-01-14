package strinka.aldrawandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class HSVWheelView extends ImageView {
	private float hue;
	private float saturation;
	private Paint paint;

	public HSVWheelView(Context context) {
		super(context);
		setPaint();
	}
	
	public HSVWheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPaint();
	}
	
	public HSVWheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setPaint();
	}
	
	private void setPaint(){
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(0xff000000);
	}

	public void update(float hue, float saturation){
		this.hue = hue;
		this.saturation = saturation;
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int wheelSize = this.getWidth();
    	double angle = hue*Math.PI/180;
    	double dist = saturation*wheelSize/2;
    	double x = wheelSize/2 + dist*Math.cos(angle);
    	double y = wheelSize/2 + dist*Math.sin(angle);
		canvas.drawCircle((float)x, (float)y, 20, paint);
	}
	
}
