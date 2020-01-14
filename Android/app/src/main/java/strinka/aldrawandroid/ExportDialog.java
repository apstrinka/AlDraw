package strinka.aldrawandroid;

import java.io.File;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

public class ExportDialog extends Dialog {
	AlDrawController controller;

	public ExportDialog(Context context, AlDrawController controller) {
		super(context);
		
		this.controller = controller;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutInflater().inflate(R.layout.export_dialog, null));
	    setTitle("Export");
	     
	    final RadioButton svgButton = (RadioButton)findViewById(R.id.export_svg);
	    svgButton.setChecked(true);
	    
	    final RadioButton pngButton = (RadioButton)findViewById(R.id.export_png);
	    
	    final TextView backgroundLabel = (TextView)findViewById(R.id.background_label);
	    backgroundLabel.setVisibility(View.GONE);
	    
	    final RadioButton whiteButton = (RadioButton)findViewById(R.id.white_background);
	    whiteButton.setChecked(true);
	    whiteButton.setVisibility(View.GONE);
	    
	    final RadioButton transparentButton = (RadioButton)findViewById(R.id.transparent_background);
	    transparentButton.setVisibility(View.GONE);
	    
	    final TextView sizeLabel = (TextView)findViewById(R.id.size_label);
	    sizeLabel.setVisibility(View.GONE);
	    
	    final NumberPicker sizePicker = (NumberPicker)findViewById(R.id.export_size);
	    sizePicker.setFormatter(new NumberPicker.Formatter() {
			@Override
			public String format(int value) {
				return "" + value + "%";
			}
		});
	    sizePicker.setMinValue(1);
	    sizePicker.setMaxValue(10000);
	    sizePicker.setValue(100);
	    sizePicker.setWrapSelectorWheel(false);
	    sizePicker.setVisibility(View.GONE);
	    
	    pngButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					backgroundLabel.setVisibility(View.VISIBLE);
					whiteButton.setVisibility(View.VISIBLE);
					transparentButton.setVisibility(View.VISIBLE);
					sizeLabel.setVisibility(View.VISIBLE);
					sizePicker.setVisibility(View.VISIBLE);
				} else {
					backgroundLabel.setVisibility(View.GONE);
					whiteButton.setVisibility(View.GONE);
					transparentButton.setVisibility(View.GONE);
					sizeLabel.setVisibility(View.GONE);
					sizePicker.setVisibility(View.GONE);
				}
			}
		});
	    
	    Button cancel = (Button)findViewById(R.id.cancelExport);
	    cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	    
	    Button ok = (Button)findViewById(R.id.okExport);
	    ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AlDraw");
				if (!dir.exists())
					dir.mkdir();
				if (svgButton.isChecked()){
		    		Dialog d = new FileChooserDialog(getContext(), dir, ".svg", new SvgExporter(controller), true);
		    		d.show();
				} else if (pngButton.isChecked()){
					Dialog d = new FileChooserDialog(getContext(), dir, ".png", new PngExporter(getContext(), controller, whiteButton.isChecked(), sizePicker.getValue()), true);
					d.show();
				}
				dismiss();
			}
		});
	}
}
