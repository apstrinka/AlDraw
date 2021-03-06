package strinka.aldrawandroid;

import java.util.ArrayList;

public class DrawRayInputStrategy extends InputStrategy
{
	DrawRayInputStrategy(AlDrawController controller){
		super("Draw Ray", 2, controller);
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Ray(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	void endHook()
	{
		controller.addRay(new Ray(controller.getSelPoint(0), controller.getSelPoint(1)));
	}
}
