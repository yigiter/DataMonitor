package org.instk.datamonitor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class LocInfo extends Activity implements OnItemSelectedListener{
	LocationManager mLocMan=null;
	TextView[] mTV=new TextView[10]; 
	CLocProvStates mLocManNames=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.locinfo);
		TextView[] lTV=mTV;
		lTV[0]=(TextView) findViewById(R.id.LMIName);
		lTV[1]=(TextView) findViewById(R.id.LMIAccuracy);
		lTV[2]=(TextView) findViewById(R.id.LMIReqPow);
		lTV[3]=(TextView) findViewById(R.id.LMIMoney);
		lTV[4]=(TextView) findViewById(R.id.LMIReqCell);
		lTV[5]=(TextView) findViewById(R.id.LMIReqNetwork);
		lTV[6]=(TextView) findViewById(R.id.LMIReqSatellite);
		lTV[7]=(TextView) findViewById(R.id.LMISupAltitude);
		lTV[8]=(TextView) findViewById(R.id.LMISupBearing);
		lTV[9]=(TextView) findViewById(R.id.LMISupSpeed);
		Spinner lSpinner=(Spinner) findViewById(R.id.LMIspinner1);
		
		//Get the reference to location manager
		LocationManager lLocMan=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocMan=lLocMan;
		//List of providers
		List<String> lLLoc=lLocMan.getAllProviders();
		CLocProvStates lLocManNames=new CLocProvStates(lLLoc);
		mLocManNames=lLocManNames;
		
		if (lLLoc.size()>0) {
			//Set the spinner adapter
			ArrayAdapter<String> lAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lLocManNames.getNames());
			lAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			lSpinner.setAdapter(lAdapter);
			
			lSpinner.setOnItemSelectedListener(this);
			
			//Show the info of the first sensor
			lSpinner.setSelection(0);
			//lSpinner.performClick();
		}
		else
			lSpinner.setEnabled(false);
	}


	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		CLocProvStates lLocManNames=mLocManNames;
		TextView[] lTV=mTV;
		
		//Get the provider
		LocationProvider lProvider=mLocMan.getProvider(lLocManNames.getName(pos));
		lTV[0].setText(lProvider.getName());
		lTV[1].setText(Integer.toString(lProvider.getAccuracy()));
		lTV[2].setText(Integer.toString(lProvider.getPowerRequirement()));
		lTV[3].setText(Boolean.toString(lProvider.hasMonetaryCost()));
		lTV[4].setText(Boolean.toString(lProvider.requiresCell()));
		lTV[5].setText(Boolean.toString(lProvider.requiresNetwork()));
		lTV[6].setText(Boolean.toString(lProvider.requiresSatellite()));
		lTV[7].setText(Boolean.toString(lProvider.supportsAltitude()));
		lTV[8].setText(Boolean.toString(lProvider.supportsBearing()));
		lTV[9].setText(Boolean.toString(lProvider.supportsSpeed()));
		lProvider=null; //For the GC
	}


	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
