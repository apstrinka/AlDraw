package strinka.aldrawandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public interface Drawable
{
	public void draw(Canvas canvas, Paint paint, CoordinateConverter c, Context context);
}
