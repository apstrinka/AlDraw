package strinka.aldrawandroid;

public interface Selectable extends Drawable
{
	Selectable copy(DPoint copy, DPoint paste);
	Selectable copy(DPoint copy1, DPoint copy2, DPoint paste1, DPoint paste2);
	void addToState(AlDrawState state);
}
