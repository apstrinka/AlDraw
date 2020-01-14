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

public class PreferenceOptions
{
	private double lineWidth;
	private double dotRadius;
	private AntialiasPreference antialias;
	private File defaultDraDir;
	private File defaultSvgDir;
	private File defaultPngDir;

	public PreferenceOptions()
	{
		// leave everything at default values
	}

	public PreferenceOptions(PreferenceOptions other)
	{
		setLineWidth(other.getLineWidth());
		setDotRadius(other.getDotRadius());
		setAntialias(other.getAntialias());
		setDefaultDraDir(other.getDefaultDraDir());
		setDefaultSvgDir(other.getDefaultSvgDir());
		setDefaultPngDir(other.getDefaultPngDir());
	}

	public void setLineWidth(double lineWidth)
	{
		this.lineWidth = lineWidth;
	}

	public void setDotRadius(double dotRadius)
	{
		this.dotRadius = dotRadius;
	}

	public void setAntialias(AntialiasPreference antialias)
	{
		this.antialias = antialias;
	}

	public void setDefaultDraDir(File defaultDraDir)
	{
		this.defaultDraDir = defaultDraDir;
	}

	public void setDefaultSvgDir(File defaultSvgDir)
	{
		this.defaultSvgDir = defaultSvgDir;
	}
	
	public void setDefaultPngDir(File defaultPngDir)
	{
		this.defaultPngDir = defaultPngDir;
	}

	public double getLineWidth()
	{
		return lineWidth;
	}

	public double getDotRadius()
	{
		return dotRadius;
	}

	public AntialiasPreference getAntialias()
	{
		return antialias;
	}

	public File getDefaultDraDir()
	{
		return defaultDraDir;
	}

	public File getDefaultSvgDir()
	{
		return defaultSvgDir;
	}
	
	public File getDefaultPngDir()
	{
		return defaultPngDir;
	}
}
