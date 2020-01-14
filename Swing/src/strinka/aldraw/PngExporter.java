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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PngExporter extends JDialog
{
	private static final long serialVersionUID = 5929795002142841697L;
	int y;
	AlDrawController controller;
	JFileChooser exportPngFileChooser;
	JRadioButton whiteBackground;
	JRadioButton transparentBackground;
	JSpinner sizeSpinner;
	JLabel sizeLabel;
	
	public PngExporter(JFileChooser exportPngFileChooser, AlDrawController controller)
	{
		super();
		this.setTitle("Export PNG Options");
		this.setLayout(new GridBagLayout());
		this.exportPngFileChooser = exportPngFileChooser;
		this.controller = controller;
		
		y = 0;
		makeBackgroundButtons();
		makeSizeSpinner();
		makeBottomButtons(controller);
		this.pack();
	}
	
	private void makeBackgroundButtons(){
		JLabel backgroundLabel = new JLabel("Background:");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(backgroundLabel, c);
		
		whiteBackground = new JRadioButton("White");
		whiteBackground.setSelected(true);
		transparentBackground = new JRadioButton("Transparent");
		ButtonGroup group = new ButtonGroup();
		group.add(whiteBackground);
		group.add(transparentBackground);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(whiteBackground, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(transparentBackground, c);
		
		y++;
	}
	
	private void makeSizeSpinner(){
		JLabel label = new JLabel("Size %");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(label, c);
		
		SpinnerModel model = new SpinnerNumberModel(100, 1, Integer.MAX_VALUE, 1);
		sizeSpinner = new JSpinner(model);
		((JSpinner.DefaultEditor)sizeSpinner.getEditor()).getTextField().setColumns(3);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(sizeSpinner, c);
		
		sizeLabel = new JLabel("" + controller.getConverter().getWidth() + " by " + controller.getConverter().getHeight() + " px");
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(sizeLabel, c);
		
		sizeSpinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				updateSizeLabel();
			}
		});
		
		y++;
	}
	
	private void makeBottomButtons(final AlDrawController controller)
	{
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(cancel, c);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				setVisible(false);
				int returnVal = exportPngFileChooser.showDialog(controller.getView(), "Export");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = exportPngFileChooser.getSelectedFile();
					String filename = file.getAbsolutePath();
					if (!filename.endsWith(".png"))
					{
						filename = filename + ".png";
						file = new File(filename);
					}
					try
					{
						exportPNG(file, controller, whiteBackground.isSelected(), (Integer)sizeSpinner.getValue());
					} catch (IOException e)
					{
						// TODO: Make this a more user friendly error message. See
						// save()
						System.err.println("There was an error exporting the file");
					}
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(ok, c);
		y++;
	}
	
	public void updateSizeLabel(){
		int width = controller.getConverter().getWidth() * (Integer)sizeSpinner.getValue() / 100;
		int height = controller.getConverter().getHeight() * (Integer)sizeSpinner.getValue() / 100;
		String s = "" + width + " by " + height + " px";
		sizeLabel.setText(s);
	}

	public void exportPNG(File file, AlDrawController controller, boolean whiteBackground, int size) throws IOException
	{
		AlDrawState state = controller.getCurrentState();
		CoordinateConverter converter = new CoordinateConverter(controller.getConverter());
		converter.setConversionRatio(converter.getConversionRatio()*size/100);
		converter.setWidth(converter.getWidth()*size/100);
		converter.setHeight(converter.getHeight()*size/100);
		int width = converter.getWidth();
		int height = converter.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setStroke(new BasicStroke((float) controller.getPrefOpts().getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		Object value_antialias = RenderingHints.VALUE_ANTIALIAS_OFF;
		if (controller.getPrefOpts().getAntialias() == AntialiasPreference.ON || (controller.getPrefOpts().getAntialias() == AntialiasPreference.OFFWHENDRAGGING && !controller.getIsDragging()))
		{
			value_antialias = RenderingHints.VALUE_ANTIALIAS_ON;
		}
		g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, value_antialias));
		if (whiteBackground){
			g.setColor(Color.white);
			g.fillRect(0, 0, converter.getWidth(), converter.getHeight());
		}
		g.setColor(Color.black);
		state.paintState(g, converter, controller.getPrefOpts(), false);
		ImageIO.write(img, "png", file);
	}
}
