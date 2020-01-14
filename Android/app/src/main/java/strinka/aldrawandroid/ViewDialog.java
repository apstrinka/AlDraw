package strinka.aldrawandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

public class ViewDialog extends Dialog {
	AlDrawController controller;

	public ViewDialog(Context context, AlDrawController controller) {
		super(context);
		
		this.controller = controller;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutInflater().inflate(R.layout.view_dialog, null));
	    setTitle("View Options");
	     
	    Button defaultView = (Button)findViewById(R.id.defaultView);
	    defaultView.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View v) {
				controller.defaultView();
			}
		});
	     
	    Button defaultRotation = (Button)findViewById(R.id.defaultRotation);
	    defaultRotation.setOnClickListener(new View.OnClickListener() {
	    	@Override
			public void onClick(View v) {
				controller.defaultRotation();
			}
		});
	     
	    CheckBox showLines = (CheckBox)findViewById(R.id.showLines);
	    showLines.setChecked(controller.getShowLines());
	    showLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	@Override
	    	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				controller.setShowLines(isChecked);
			}
	    });
	     
	    CheckBox showPoints = (CheckBox)findViewById(R.id.showPoints);
	    showPoints.setChecked(controller.getShowPoints());
	    showPoints.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	 @Override
	    	 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    		 controller.setShowPoints(isChecked);
	    	 }
	    });
	    
	    final EditText rotateAmount = (EditText)findViewById(R.id.rotateAmount);
	    rotateAmount.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_NUMBER_FLAG_SIGNED);
	    
	    final Spinner rotateUnits = (Spinner)findViewById(R.id.rotateUnits);
	    ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.rotationunits, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rotateUnits.setAdapter(spinnerAdapter);
	    
	    Button rotate = (Button)findViewById(R.id.rotateButton);
	    rotate.setOnClickListener(new View.OnClickListener(){
	    	@Override
			public void onClick(View v) {
				double val = 0;
				try{
					val = Double.parseDouble(rotateAmount.getText().toString());
				} catch (NumberFormatException e){
					//do nothing
				}
				String sel = rotateUnits.getSelectedItem().toString();
				if ("Degrees".equals(sel))
					val = val*2*Math.PI/360;
				else
					val = val*Math.PI;
				controller.rotateBy(val);
			}
	    });
	    
	    CheckBox lockRotation = (CheckBox)findViewById(R.id.lockRotation);
	    lockRotation.setChecked(controller.getLockRotation());
	    lockRotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    	 @Override
	    	 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    		 controller.setLockRotation(isChecked);
	    	 }
	    });
	     
	    Button done = (Button)findViewById(R.id.view_done);
	    done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
