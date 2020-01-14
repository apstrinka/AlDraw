package strinka.aldrawandroid;

import java.util.ArrayList;

public class DeleteArcInputStrategy extends InputStrategy
{
	DeleteArcInputStrategy(AlDrawController controller){
		super("Delete Arc", 3, controller);
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Circle(p1, p2);
		shadows.add(shadow);
		return shadows;
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2, DPoint p3){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Arc(p1, p2, p3);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	void endHook()
	{
		controller.removeArc(new Arc(controller.getSelPoint(0), controller.getSelPoint(1), controller.getSelPoint(2)));
	}
}
