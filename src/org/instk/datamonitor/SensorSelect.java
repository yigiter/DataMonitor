package org.instk.datamonitor;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SensorSelect extends ListActivity implements View.OnClickListener {

	CSensorStates mSenStates=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Set layout and buttons
		setContentView(R.layout.sensorselect);
		Button btndiscard=(Button) findViewById(R.id.SSbutton1);
		Button btnsave=(Button) findViewById(R.id.SSbutton2);
		
		btndiscard.setOnClickListener(new View.OnClickListener() {
			public void onClick (View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		btnsave.setOnClickListener(this);
		
		
		Intent lintent=getIntent();
		CSensorStates lSenStates=(CSensorStates) lintent.getParcelableExtra("sensor_states");
		mSenStates=lSenStates;
		
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lSenStates.getNames()));
		
		final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        //Show the current selection
        for (int i=0;i<lSenStates.getNum();i++){
        	listView.setItemChecked(i, lSenStates.getActive(i));
        }
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mSenStates.setActive(position, l.isItemChecked(position));
	}
	

	public void onClick(View v) {
		CSensorStates lSenStates=mSenStates;
		
		//Write the new values to the pref file and return to the previous view
		SharedPreferences lPrefs = getSharedPreferences("SensorMonPrefs", MODE_PRIVATE);
		SharedPreferences.Editor lPrefEd=lPrefs.edit();
		
		for (int i=0; i<lSenStates.getNum(); i++) {
			lPrefEd.putBoolean(lSenStates.getName(i), lSenStates.getActive(i));
		}
		lPrefEd.commit();
		
		//Quit
		setResult(RESULT_OK);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		setResult(RESULT_CANCELED);
		finish();
	}
	
	
}
