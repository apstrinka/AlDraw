package strinka.aldrawandroid;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import strinka.aldrawandroid.ColorPickerDialog.OnColorChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnItemSelectedListener, OnColorChangedListener {
	AlDrawView alDrawView;
	AlDrawController controller;
	Spinner modeGroups;
	Map<String, Spinner> spinnerMap;
	Spinner selectedSpinner;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        spinnerMap = new HashMap<String, Spinner>();
        
        modeGroups = new Spinner(this);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.modegroups, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeGroups.setAdapter(spinnerAdapter);
        modeGroups.setOnItemSelectedListener(this);
        modeGroups.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        
        Spinner basicSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.basicmodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        basicSpinner.setAdapter(spinnerAdapter);
        basicSpinner.setOnItemSelectedListener(this);
        basicSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Basic", basicSpinner);
        selectedSpinner = basicSpinner;
        
        Spinner editSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.editmodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSpinner.setAdapter(spinnerAdapter);
        editSpinner.setOnItemSelectedListener(this);
        editSpinner.setVisibility(View.GONE);
        editSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Edit", editSpinner);
        
        Spinner extendedSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.extendedmodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        extendedSpinner.setAdapter(spinnerAdapter);
        extendedSpinner.setOnItemSelectedListener(this);
        extendedSpinner.setVisibility(View.GONE);
        extendedSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Extended", extendedSpinner);
        
        Spinner shortcutSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.shortcutmodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shortcutSpinner.setAdapter(spinnerAdapter);
        shortcutSpinner.setOnItemSelectedListener(this);
        shortcutSpinner.setVisibility(View.GONE);
        shortcutSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Shortcuts", shortcutSpinner);
        
        Spinner cleanupSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.cleanupmodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cleanupSpinner.setAdapter(spinnerAdapter);
        cleanupSpinner.setOnItemSelectedListener(this);
        cleanupSpinner.setVisibility(View.GONE);
        cleanupSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Clean Up", cleanupSpinner);
        
        Spinner colorSpinner = new Spinner(this);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.colormodes, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(spinnerAdapter);
        colorSpinner.setOnItemSelectedListener(this);
        colorSpinner.setVisibility(View.GONE);
        colorSpinner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        spinnerMap.put("Color", colorSpinner);
        
        LinearLayout spinners = new LinearLayout(this);
        spinners.setOrientation(LinearLayout.HORIZONTAL);
        spinners.addView(modeGroups);
        spinners.addView(basicSpinner);
        spinners.addView(editSpinner);
        spinners.addView(extendedSpinner);
        spinners.addView(shortcutSpinner);
        spinners.addView(cleanupSpinner);
        spinners.addView(colorSpinner);
        
        alDrawView = new AlDrawView(this);
        controller = alDrawView.getController();
        
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(spinners);
        linearLayout.addView(alDrawView);
        setContentView(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	if (item.getItemId() == R.id.menu_clear){
    		controller.startNew();
    	} else if (item.getItemId() == R.id.menu_undo){
    		controller.undo();
    	} else if (item.getItemId() == R.id.menu_redo){
    		controller.redo();
    	} else if (item.getItemId() == R.id.menu_autozoom){
    		controller.autoZoom();
    	} else if (item.getItemId() == R.id.menu_open){
    		File dir = getExternalFilesDir(null);
			Dialog d = new FileChooserDialog(this, dir, ".dra", new DraOpener(controller), false);
			d.show();
    	} else if (item.getItemId() == R.id.menu_save){
    		File dir = getExternalFilesDir(null);
    		Dialog d = new FileChooserDialog(this, dir, ".dra", new DraSaver(controller), true);
    		d.show();
    	} else if (item.getItemId() == R.id.menu_export){
    		Dialog d = new ExportDialog(this, controller);
    		d.show();
    	} else if (item.getItemId() == R.id.menu_share){
    		PngExporter pngExporter = new PngExporter(this, controller, true, 100);
    		File f = new File(getExternalFilesDir(null), "temp.png");
    		pngExporter.fileChosen(f);
    		
    		Intent intent = new Intent(Intent.ACTION_SEND);
    		intent.setType("image/png");
    		
    		Uri uri = Uri.fromFile(f);
    		intent.putExtra(Intent.EXTRA_STREAM, uri);
    		
    		//This only works on IceCreamSandwich
    		//ShareActionProvider actionProvider = (ShareActionProvider)item.getActionProvider();
    		//actionProvider.setShareIntent(intent);
    		startActivity(Intent.createChooser(intent, "How do you want to share?"));
    	} else if (item.getItemId() == R.id.menu_clearselections){
    		controller.clearSelected();
    	} else if (item.getItemId() == R.id.menu_choosecolor){
    		Dialog d = new ColorPickerDialog(this, this, controller.getColor());
    		d.show();
    	} else if (item.getItemId() == R.id.menu_view){
    		Dialog d = new ViewDialog(this, controller);
    		d.show();
    	} else if (item.getItemId() == R.id.menu_settings){
    		Intent intent = new Intent(this, SettingsActivity.class);
    		startActivity(intent);
    	}
    	alDrawView.invalidate();
    	return true;
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		String s = parent.getSelectedItem().toString();
		Spinner spinner = spinnerMap.get(s);
		if (spinner != null){
	        selectedSpinner.setVisibility(View.GONE);
	        selectedSpinner.invalidate();
	        selectedSpinner = spinner;
	        selectedSpinner.setVisibility(View.VISIBLE);
	        selectedSpinner.invalidate();
	        s = selectedSpinner.getSelectedItem().toString();
		}
		
		controller.clearSelPoints();
		controller.setStrategy(s);
		alDrawView.invalidate();	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		//Do nothing
	}

	@Override
	public void colorChanged(int color) {
		controller.setColor(color);
	}
}
