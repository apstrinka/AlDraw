package strinka.aldrawandroid;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ColorPickerDialog extends Dialog {

    public interface OnColorChangedListener {
        void colorChanged(int color);
    }

    private OnColorChangedListener listener;
    private int initialColor;
    private float[] hsv = new float[3];
    private Button okButton;
    private Button cancelButton;
    private HSVWheelView hsvWheel;
    private SeekBar valueBar;
    private EditText hueText;
    private EditText saturationText;
    private EditText valueText;
    private EditText redText;
    private EditText greenText;
    private EditText blueText;

    public ColorPickerDialog(Context context, OnColorChangedListener listener, int initialColor) {
        super(context);

        this.listener = listener;
        this.initialColor = initialColor;
        Color.colorToHSV(initialColor, hsv);
    }

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(getLayoutInflater().inflate(R.layout.color_picker, null));
        setTitle("Pick a Color");
        
        okButton = (Button)findViewById(R.id.okButton);
        okButton.setBackgroundColor(initialColor);
        okButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				listener.colorChanged(Color.HSVToColor(hsv));
				dismiss();
				
			}
        });
        
        cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setBackgroundColor(initialColor);
        cancelButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dismiss();
				
			}
        });
        
        hueText = (EditText)findViewById(R.id.hueText);
        hueText.setText(String.valueOf(Math.round(hsv[0])));
        
        saturationText = (EditText)findViewById(R.id.saturationText);
        saturationText.setText(String.valueOf(Math.round(hsv[1]*100)));
        
        valueText = (EditText)findViewById(R.id.valueText);
        valueText.setText(String.valueOf(Math.round(hsv[2]*100)));
        
        redText = (EditText)findViewById(R.id.redText);
        redText.setText(String.valueOf(Color.red(Color.HSVToColor(hsv))));
        
        greenText = (EditText)findViewById(R.id.greenText);
        greenText.setText(String.valueOf(Color.green(Color.HSVToColor(hsv))));
        
        blueText = (EditText)findViewById(R.id.blueText);
        blueText.setText(String.valueOf(Color.blue(Color.HSVToColor(hsv))));
        
        valueBar = (SeekBar)findViewById(R.id.valueBar);
        valueBar.setProgress(Math.round(hsv[2]*1000));
        //redBar.setBackgroundColor(0xffffbbbb);
        int[] colors = {0xff000000, Color.HSVToColor(hsv)};
        valueBar.setBackgroundDrawable(new GradientDrawable(Orientation.LEFT_RIGHT, colors));
        valueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				hsv[2] = (float)progress/1000;
				updateRGB();
				valueText.setText(String.valueOf(Math.round(hsv[2]*100)));
			}
			@Override public void onStartTrackingTouch(SeekBar seekBar) {} //do nothing
			@Override public void onStopTrackingTouch(SeekBar seekBar) {} //do nothing
		});
        
        hsvWheel = (HSVWheelView)findViewById(R.id.hsvWheel);
        hsvWheel.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float size = v.getWidth();
				float x = event.getX();
				float y = event.getY();
				Log.d("Debug", "(" + (x-size/2) + ", " + (y-size/2) + ")");
				
				double dist = Math.hypot(x-size/2, y-size/2);
				double angle = Math.atan2(y-size/2, x-size/2);
				double hue = 180*angle/Math.PI;
				if (hue < 0)
					hue = hue + 360;
				double saturation = Math.min(2*dist/size, 1);
				hsv[0] = (float)hue;
				hsv[1] = (float)saturation;
				hueText.setText(String.valueOf(Math.round(hsv[0])));
				saturationText.setText(String.valueOf(Math.round(hsv[1]*100)));
				updateGradient();
				updateRGB();
				return true;
			}
		});
        hsvWheel.update(hsv[0], hsv[1]);
        
        hueText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 359){
					val = 359;
					v.setText("359");
				}
				hsv[0] = val;
				updateRGB();
				updateGradient();
				return true;
			}
        });
        
        saturationText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 100){
					val = 100;
					v.setText("100");
				}
				hsv[1] = (float)val/100;
				updateRGB();
				updateGradient();
				return true;
			}
        });
        
        valueText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 100){
					val = 100;
					v.setText("100");
				}
				hsv[2] = (float)val/100;
				updateRGB();
				valueBar.setProgress(val*10);
				return true;
			}
        });
        
        redText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 255){
					val = 255;
					v.setText("255");
				}
				int color = Color.HSVToColor(hsv);
				int red = val;
				int green = Color.green(color);
				int blue = Color.blue(color);
				color = Color.rgb(red, green, blue);
				updateHSV(color);
				return true;
			}
        });
        
        greenText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 255){
					val = 255;
					v.setText("255");
				}
				int color = Color.HSVToColor(hsv);
				int red = Color.red(color);
				int green = val;
				int blue = Color.blue(color);
				color = Color.rgb(red, green, blue);
				updateHSV(color);
				return true;
			}
        });
        
        blueText.setOnEditorActionListener(new OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int val = 0;
				try{
					val = Integer.parseInt(v.getText().toString());
				} catch (Exception e){
					val = 0;
				}
				if (val < 0){
					val = 0;
					v.setText("0");
				} else if (val > 255){
					val = 255;
					v.setText("255");
				}
				int color = Color.HSVToColor(hsv);
				int red = Color.red(color);
				int green = Color.green(color);
				int blue = val;
				color = Color.rgb(red, green, blue);
				updateHSV(color);
				return true;
			}
        });
    }
    
    private void updateRGB(){
    	int color = Color.HSVToColor(hsv);
    	redText.setText(String.valueOf(Color.red(color)));
    	greenText.setText(String.valueOf(Color.green(color)));
    	blueText.setText(String.valueOf(Color.blue(color)));
    	okButton.setBackgroundColor(color);
    }
    
	private void updateHSV(int color){
    	Color.colorToHSV(color, hsv);
    	updateGradient();
		valueBar.setProgress(Math.round(hsv[2]*1000));
		hueText.setText(String.valueOf(Math.round(hsv[0])));
		saturationText.setText(String.valueOf(Math.round(hsv[1]*100)));
		valueText.setText(String.valueOf(Math.round(hsv[2]*100)));
		okButton.setBackgroundColor(color);
    }
    
    @SuppressWarnings("deprecation")
	private void updateGradient(){
    	float[] bright = new float[3];
    	bright[0] = hsv[0];
    	bright[1] = hsv[1];
    	bright[2] = 1;
    	
    	int[] colors = {0xff000000, Color.HSVToColor(bright)};
        valueBar.setBackgroundDrawable(new GradientDrawable(Orientation.LEFT_RIGHT, colors));
        
        hsvWheel.update(hsv[0], hsv[1]);
    }
}
