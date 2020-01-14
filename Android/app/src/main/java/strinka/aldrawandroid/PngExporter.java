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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.preference.PreferenceManager;

public class PngExporter implements FileChooserDialog.OnFileChosenListener
{
	Context context;
	AlDrawController controller;
	boolean whiteBackground;
	int size;
	
	public PngExporter(Context context, AlDrawController controller, boolean whiteBackground, int size)
	{
		this.context = context;
		this.controller = controller;
		this.whiteBackground = whiteBackground;
		this.size = size;
	}
	
	@Override
	public void fileChosen(File file) {
		try {
			exportPNG(file, controller);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exportPNG(File file, AlDrawController controller) throws IOException
	{
		AlDrawState state = controller.getState();
		CoordinateConverter converter = new CoordinateConverter(controller.getConverter());
		converter.setConversionRatio(converter.getConversionRatio()*size/100);
		converter.setWidth(converter.getWidth()*size/100);
		converter.setHeight(converter.getHeight()*size/100);
		int width = converter.getWidth();
		int height = converter.getHeight();
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeCap(Paint.Cap.ROUND);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		paint.setStrokeWidth(pref.getInt("pref_line_width_int", 6));
		if (whiteBackground){
			paint.setColor(0xffffffff);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawRect(0, 0, width, height, paint);
		}
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xff000000);
		state.paintState(canvas, paint, converter, context, controller.getShowLines(), false);
		
		OutputStream stream = new FileOutputStream(file);
		bmp.compress(CompressFormat.PNG, 100, stream);
		stream.flush();
		stream.close();
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		context.sendBroadcast(mediaScanIntent);
	}
}
