package strinka.aldrawandroid;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
	
	private int maxValue = 100;
	private int minValue = 0;
	private int interval = 1;
	private int currentValue;
	private String units = "";
	private SeekBar seekBar;
	private TextView statusText;
	
	public SeekBarPreference(Context context, AttributeSet attrs){
		super(context, attrs);
		initPreference(context, attrs);
	}
	
	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}
	
	private void initPreference(Context context, AttributeSet attrs){
		setValuesFromXml(attrs);
		seekBar = new SeekBar(context, attrs);
		seekBar.setMax(maxValue - minValue);
		seekBar.setOnSeekBarChangeListener(this);
	}
	
	private void setValuesFromXml(AttributeSet attrs){
		maxValue = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "max", 100);
		minValue = attrs.getAttributeIntValue("strinka", "min", 0);
		units = getAttributeStringValue(attrs, "strinka", "units", "");
		interval = attrs.getAttributeIntValue("strinka", "interval", 1);
	}
	
	private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue){
		String value = attrs.getAttributeValue(namespace, name);
		if (value == null)
			value = defaultValue;
		return value;
	}
	
	@Override
	protected View onCreateView(ViewGroup parent){
		RelativeLayout layout = null;
		try {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (RelativeLayout)inflater.inflate(R.layout.seek_bar_preference, parent, false);
		} catch (Exception e) {
			System.out.println("Oh god, we're all going to die!");
		}
		return layout;
	}
	
	@Override
	protected void onBindView(View view){
		super.onBindView(view);
		try{
			ViewParent oldContainer = seekBar.getParent();
			ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);
			
			if (oldContainer != newContainer){
				if (oldContainer != null){
					((ViewGroup)oldContainer).removeView(seekBar);
				}
				newContainer.removeAllViews();
				newContainer.addView(seekBar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		} catch (Exception e){
			System.out.println("Oh god, we're all going to die!");
		}
		
		updateView(view);
	}
	
	protected void updateView(View view){
		try{
			RelativeLayout layout = (RelativeLayout)view;
			statusText = (TextView)layout.findViewById(R.id.seekBarPrefValue);
			statusText.setText(String.valueOf(currentValue));
			statusText.setMinimumWidth(30);
			seekBar.setProgress(currentValue - minValue);
			TextView unitsView = (TextView)layout.findViewById(R.id.seekBarPrefUnits);
			unitsView.setText(units);
		} catch (Exception e){
			System.out.println("Oh god, we're all going to die!");
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int newValue = progress + minValue;
		if (newValue > maxValue)
			newValue = maxValue;
		else if (newValue < minValue)
			newValue = minValue;
		else if (newValue % interval != 0)
			newValue = (newValue/interval)*interval;
		
		if (!callChangeListener(newValue)){
			seekBar.setProgress(currentValue - minValue);
		} else {
			currentValue = newValue;
			statusText.setText(String.valueOf(newValue));
			persistInt(newValue);
		}
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// Do nothing		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notifyChanged();
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue){
		if (restoreValue){
			currentValue = getPersistedInt(currentValue);
		} else {
			int temp = 0;
			try {
				temp = (Integer)defaultValue;
			} catch (Exception e){
				System.out.println("We're hopefully not going to die");
			}
			
			persistInt(temp);
			currentValue = temp;
		}
	}
	
}
