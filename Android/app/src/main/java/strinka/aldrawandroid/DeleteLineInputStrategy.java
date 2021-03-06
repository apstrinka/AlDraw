package strinka.aldrawandroid;

import java.util.ArrayList;

public class DeleteLineInputStrategy extends InputStrategy
{
	DeleteLineInputStrategy(AlDrawController controller){
		super("Delete Line", 2, controller);
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
		controller.removeLine(new Line(controller.getSelPoint(0), controller.getSelPoint(1)));
	}
}
