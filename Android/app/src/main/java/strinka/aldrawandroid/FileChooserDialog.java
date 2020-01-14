package strinka.aldrawandroid;

import java.io.File;
import java.io.FilenameFilter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FileChooserDialog extends Dialog{
	public interface OnFileChosenListener {
        void fileChosen(File file);
    }
	
	File directory;
	String filter;
	OnFileChosenListener listener;
	boolean confirm;
	String[] names;
	
	public FileChooserDialog(Context context, File directory, OnFileChosenListener listener, boolean confirm) {
		super(context);
		
		this.directory = directory;
		this.filter = "";
		this.listener = listener;
		this.confirm = confirm;
		
		names = directory.list();
	}

	public FileChooserDialog(Context context, File directory, final String filter, OnFileChosenListener listener, boolean confirm) {
		super(context);
		
		this.directory = directory;
		this.filter = filter;
		this.listener = listener;
		this.confirm = confirm;
		
		names = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(filter);
			}
		});
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutInflater().inflate(R.layout.file_chooser, null));
		setTitle("Choose File");
		
		final EditText filename = (EditText)findViewById(R.id.filename);
		
		ListView list = (ListView)findViewById(R.id.filelist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, names);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = names[position];
				filename.setText(name);
			}
		});
		
		Button ok = (Button)findViewById(R.id.okFile);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = filename.getText().toString();
				if (!name.endsWith(filter))
					name = name + filter;
				final File f = new File(directory, name);
				if (confirm && f.exists()){
					AlertDialog.Builder builder = new Builder(getContext());
					builder.setMessage("File already exists. Continue?")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								listener.fileChosen(f);
								dismiss();
							}
						})
						.setNegativeButton("Cancel", null);
					Dialog e = builder.create();
					e.show();
				} else {
					listener.fileChosen(f);
					dismiss();
				}
			}
		});
		
		Button cancel = (Button)findViewById(R.id.cancelFile);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
}
