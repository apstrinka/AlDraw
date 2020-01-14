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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PreferenceDialog extends JDialog
{
	private static final long serialVersionUID = 1L;
	private PreferenceOptions tempPrefOpts;
	private PreferenceOptions permPrefOpts;
	int y;
	JSlider lineWidthSlider;
	JSlider dotRadiusSlider;
	JComboBox antialiasComboBox;
	JTextField draTextField;
	JFileChooser draFileChooser;
	JTextField svgTextField;
	JFileChooser svgFileChooser;
	JTextField pngTextField;
	JFileChooser pngFileChooser;

	public PreferenceDialog(AlDrawController controller)
	{
		super();
		permPrefOpts = controller.getPrefOpts();
		tempPrefOpts = new PreferenceOptions(permPrefOpts);
		this.setTitle("Preferences");
		this.setLayout(new GridBagLayout());

		y = 0;
		makeLineWidthSlider();
		makeDotRadiusSlider();
		makeAntialiasComboBox();
		makeDraFileChooser();
		makeSvgFileChooser();
		makePngFileChooser();
		makeBottomButtons(controller);
		this.pack();
		updateFields();
	}

	private void makeLineWidthSlider()
	{
		JLabel lineWidthLabel = new JLabel("Line Width");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(lineWidthLabel, c);

		lineWidthSlider = new JSlider(0, 40);
		lineWidthSlider.setMajorTickSpacing(10);
		lineWidthSlider.setMinorTickSpacing(1);
		lineWidthSlider.setPaintTicks(true);
		lineWidthSlider.setPaintLabels(true);
		lineWidthSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting())
				{
					tempPrefOpts.setLineWidth(source.getValue());
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(lineWidthSlider, c);

		y++;
	}

	private void makeDotRadiusSlider()
	{
		JLabel dotRadiusLabel = new JLabel("Dot Radius");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(dotRadiusLabel, c);

		dotRadiusSlider = new JSlider(20, 100);
		dotRadiusSlider.setMajorTickSpacing(10);
		dotRadiusSlider.setMinorTickSpacing(1);
		dotRadiusSlider.setPaintTicks(true);
		dotRadiusSlider.setPaintLabels(true);
		dotRadiusSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting())
				{
					tempPrefOpts.setDotRadius(source.getValue());
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(dotRadiusSlider, c);

		y++;
	}

	private void makeAntialiasComboBox()
	{
		JLabel antialiasLabel = new JLabel("Antialias Option");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(antialiasLabel, c);

		antialiasComboBox = new JComboBox(AntialiasPreference.values());
		antialiasComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				tempPrefOpts.setAntialias((AntialiasPreference) antialiasComboBox.getSelectedItem());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(antialiasComboBox, c);

		y++;
	}

	private void makeDraFileChooser()
	{
		draFileChooser = new JFileChooser();
		draFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		draFileChooser.setDialogTitle("Select default dra folder");

		JLabel draLabel = new JLabel("Default directory for .dra files");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(draLabel, c);

		draTextField = new JTextField();
		draTextField.setEditable(false);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(draTextField, c);

		JButton draButton = new JButton("Browse");
		draButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int returnVal = draFileChooser.showDialog(null, "Select");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = draFileChooser.getSelectedFile();
					tempPrefOpts.setDefaultDraDir(file);
					draTextField.setText(file.getPath());
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(draButton, c);

		y++;
	}

	private void makeSvgFileChooser()
	{
		svgFileChooser = new JFileChooser();
		svgFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		svgFileChooser.setDialogTitle("Select default svg folder");

		JLabel svgLabel = new JLabel("Default directory for .svg files");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(svgLabel, c);

		svgTextField = new JTextField();
		svgTextField.setEditable(false);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(svgTextField, c);

		JButton svgButton = new JButton("Browse");
		svgButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int returnVal = svgFileChooser.showDialog(null, "Select");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = svgFileChooser.getSelectedFile();
					tempPrefOpts.setDefaultSvgDir(file);
					svgTextField.setText(file.getPath());
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(svgButton, c);

		y++;
	}
	
	private void makePngFileChooser()
	{
		pngFileChooser = new JFileChooser();
		pngFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		pngFileChooser.setDialogTitle("Select default png folder");

		JLabel pngLabel = new JLabel("Default directory for .png files");
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(pngLabel, c);

		pngTextField = new JTextField();
		pngTextField.setEditable(false);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(pngTextField, c);

		JButton pngButton = new JButton("Browse");
		pngButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int returnVal = pngFileChooser.showDialog(null, "Select");
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					File file = pngFileChooser.getSelectedFile();
					tempPrefOpts.setDefaultPngDir(file);
					pngTextField.setText(file.getPath());
				}
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(pngButton, c);

		y++;
	}

	private void makeBottomButtons(final AlDrawController controller)
	{
		JButton revert = new JButton("Revert to defaults");
		revert.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.setDefaultPrefOpts(tempPrefOpts);
				updateFields();
			}
		});
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(revert, c);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				setVisible(false);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(cancel, c);

		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				permPrefOpts.setLineWidth(lineWidthSlider.getValue() / 10.0);
				permPrefOpts.setDotRadius(dotRadiusSlider.getValue() / 10.0);
				permPrefOpts.setAntialias((AntialiasPreference) antialiasComboBox.getSelectedItem());
				File file = draFileChooser.getSelectedFile();
				if (file == null)
					file = tempPrefOpts.getDefaultDraDir();
				permPrefOpts.setDefaultDraDir(file);
				controller.getOpenFileChooser().setCurrentDirectory(file);
				controller.getSaveFileChooser().setCurrentDirectory(file);
				file = svgFileChooser.getSelectedFile();
				if (file == null)
					file = tempPrefOpts.getDefaultSvgDir();
				permPrefOpts.setDefaultSvgDir(file);
				controller.getExportSvgFileChooser().setCurrentDirectory(file);
				file = pngFileChooser.getSelectedFile();
				if (file == null)
					file = tempPrefOpts.getDefaultPngDir();
				permPrefOpts.setDefaultPngDir(file);
				controller.getExportPngFileChooser().setCurrentDirectory(file);
				controller.savePreferences();
				controller.updateView();
				setVisible(false);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = y;
		c.weightx = .5;
		c.weighty = .5;
		c.insets = new Insets(10, 10, 10, 10);
		this.add(apply, c);
		y++;
	}

	public void setPrefOpts(PreferenceOptions preferenceOptions)
	{
		permPrefOpts = preferenceOptions;
		tempPrefOpts = new PreferenceOptions(preferenceOptions);
		updateFields();
		this.pack();
	}

	private void updateFields()
	{
		lineWidthSlider.setValue((int) (tempPrefOpts.getLineWidth() * 10));
		dotRadiusSlider.setValue((int) (tempPrefOpts.getDotRadius() * 10));
		antialiasComboBox.setSelectedItem(tempPrefOpts.getAntialias());
		draFileChooser.setCurrentDirectory(tempPrefOpts.getDefaultDraDir());
		draTextField.setText(tempPrefOpts.getDefaultDraDir().getPath());
		File f = tempPrefOpts.getDefaultSvgDir();
		svgFileChooser.setCurrentDirectory(f);
		if (f != null)
			svgTextField.setText(f.getPath());
		f = tempPrefOpts.getDefaultPngDir();
		pngFileChooser.setCurrentDirectory(f);
		if (f != null)
			pngTextField.setText(f.getPath());
	}
}
