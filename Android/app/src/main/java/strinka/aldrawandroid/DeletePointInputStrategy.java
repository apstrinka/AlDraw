package strinka.aldrawandroid;

public class DeletePointInputStrategy extends InputStrategy
{
	DeletePointInputStrategy(AlDrawController controller){
		super("Delete Point", 1, controller);
	}

	@Override
	void endHook()
	{
		controller.removePoint(controller.getSelPoint(0));
	}
}
