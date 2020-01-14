/*
 *  AlDraw - A tool for creating geometrical constructions
 *  Copyright (C) 2010  Alex Strinka
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package strinka.aldrawandroid;

import android.view.MotionEvent;

//import java.util.ArrayList;

public class FillColorInputStrategy extends InputStrategy{

	public FillColorInputStrategy(AlDrawController controller){
		super("Fill Color", 0, controller);
	}

	@Override
	void pointerDown(MotionEvent e){
		// Do nothing
	}

	@Override
	void pointerUp(MotionEvent e){
		DPoint selPoint = converter.screenToAbstractCoord(new DPoint(e.getX(), e.getY()));
		AlDrawState state = controller.getState();
		Enclosure enclosure = Enclosure.findEnclosure(selPoint, state, false);
		if (enclosure != null)
		{
			enclosure.setColor(controller.getColor());
			controller.addEnclosure(enclosure);
		}
	}

	@Override
	void pointerMove(MotionEvent e){
//		CoordinateConverter converter = CoordinateConverter.getInstance();
//		DPoint selPoint = converter.screenToAbstractCoord(new DPoint(x, y));
//		AlDrawState state = alDrawController.getCurrentState();
//		Enclosure enclosure = Enclosure.findEnclosure(selPoint, state, false);
//		ArrayList<Drawable> shadows = new ArrayList<Drawable>();
//		if (enclosure != null)
//		{
//			enclosure.setColor(alDrawController.getColor());
//			shadows.add(enclosure);
//		}
//		alDrawController.setShadows(shadows);
//		alDrawController.updateView();
	}

	@Override
	void endHook(){
		// Do nothing
	}
}
