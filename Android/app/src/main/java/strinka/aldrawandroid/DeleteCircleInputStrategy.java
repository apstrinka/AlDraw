package strinka.aldrawandroid;

import java.util.ArrayList;

public class DeleteCircleInputStrategy extends InputStrategy
{
	DeleteCircleInputStrategy(AlDrawController controller){
		super("Delete Circle", 2, controller);
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Circle(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	void endHook()
	{
		controller.removeCircle(new Circle(controller.getSelPoint(0), controller.getSelPoint(1)));
	}
}
