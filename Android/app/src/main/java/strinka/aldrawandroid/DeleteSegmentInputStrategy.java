package strinka.aldrawandroid;

import java.util.ArrayList;

public class DeleteSegmentInputStrategy extends InputStrategy
{
	DeleteSegmentInputStrategy(AlDrawController controller){
		super("Delete Segment", 2, controller);
	}
	
	@Override
	ArrayList<Drawable> shadowHook(DPoint p1, DPoint p2){
		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
		Drawable shadow = new Segment(p1, p2);
		shadows.add(shadow);
		return shadows;
	}

	@Override
	void endHook()
	{
		controller.removeSegment(new Segment(controller.getSelPoint(0), controller.getSelPoint(1)));
	}
}
