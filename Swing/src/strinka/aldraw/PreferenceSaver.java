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

package strinka.aldraw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PreferenceSaver
{
	String filename;

	public PreferenceSaver(String filename)
	{
		this.filename = filename;
	}

	public void save(PreferenceOptions prefOpts) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeInt(2);
		out.writeDouble(prefOpts.getLineWidth());
		out.writeDouble(prefOpts.getDotRadius());
		out.writeObject(prefOpts.getAntialias());
		out.writeObject(prefOpts.getDefaultDraDir());
		out.writeObject(prefOpts.getDefaultSvgDir());
		out.writeObject(prefOpts.getDefaultPngDir());
	}

	public PreferenceOptions open()
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			PreferenceOptions ret = new PreferenceOptions();
			int i = in.readInt();
			if (i == 1){
				ret.setLineWidth(in.readDouble());
				ret.setDotRadius(in.readDouble());
				ret.setAntialias((AntialiasPreference) in.readObject());
				ret.setDefaultDraDir((File) in.readObject());
				File f = (File) in.readObject();
				ret.setDefaultSvgDir(f);
				ret.setDefaultPngDir(f);
				return ret;
			} else if (i == 2){
				ret.setLineWidth(in.readDouble());
				ret.setDotRadius(in.readDouble());
				ret.setAntialias((AntialiasPreference) in.readObject());
				ret.setDefaultDraDir((File) in.readObject());
				ret.setDefaultSvgDir((File) in.readObject());
				ret.setDefaultPngDir((File) in.readObject());
				return ret;
			} else
				return null;
		} catch (Exception e)
		{
			return null;
		}
	}
}
