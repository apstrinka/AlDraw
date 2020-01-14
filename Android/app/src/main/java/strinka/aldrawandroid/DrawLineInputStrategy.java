package strinka.aldrawandroid;

import java.util.ArrayList;

public class DrawLineInputStrategy extends InputStrategy
{
	DrawLineInputStrategy(AlDrawController controller){
		super("Draw Line", 2, controller);
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Line(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	void endHook()
	{
		controller.addLine(new Line(controller.getSelPoint(0), controller.getSelPoint(1)));
	}
}
