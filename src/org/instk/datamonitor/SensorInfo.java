package org.instk.datamonitor;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SensorInfo extends Activity implements OnItemSelectedListener{
	
	List <Sensor> mSenList=null;
	TextView mName;
	TextView mMaxRange;
	TextView mMinDelay;
	TextView mPower;
	TextView mResolution;
	TextView mType;
	TextView mVendor;
	TextView mVersion;
	int mAPILevel=8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Spinner lSpinner=null;
		CSensorStates lSenName=null;
		
		//Determine the API level
		mAPILevel=Build.VERSION.SDK_INT;
		
		//Get a ref to the sensor manager
		SensorManager lSenMan=(SensorManager) getSystemService(SENSOR_SERVICE);
		mSenList=lSenMan.getSensorList(Sensor.TYPE_ALL);
		lSenName=new CSensorStates(mSenList);
		
		//Set layout
		setContentView(R.layout.sensorinfo);
		lSpinner=(Spinner) findViewById(R.id.SIspinner1);
		mName=(TextView) findViewById(R.id.SIName);
		mMaxRange=(TextView) findViewById(R.id.SIMaximumRange);
		mMinDelay=(TextView) findViewById(R.id.SIMinDelay);
		mPower=(TextView) findViewById(R.id.SIPower);
		mResolution=(TextView) findViewById(R.id.SIResolution);
		mType=(TextView) findViewById(R.id.SIType);
		mVendor=(TextView) findViewById(R.id.SIVendor);
		mVersion=(TextView) findViewById(R.id.SIVersion);
		
		if (lSenName.getNum()>0) {
			//Set the spinner adapter
			ArrayAdapter<CharSequence> lAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, lSenName.getNames());
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

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		List <Sensor> lSenList=mSenList;
		
		mName.setText(lSenList.get(arg2).getName());
		mMaxRange.setText(Float.toString(lSenList.get(arg2).getMaximumRange()));
		if (mAPILevel>8)
			mMinDelay.setText(Integer.toString(lSenList.get(arg2).getMinDelay()));
		else
			mMinDelay.setText("Not defined for Level<9");
		mPower.setText(Float.toString(lSenList.get(arg2).getPower()));
		mResolution.setText(Float.toString(lSenList.get(arg2).getResolution()));
		mType.setText(Integer.toString(lSenList.get(arg2).getType()));
		mVendor.setText(lSenList.get(arg2).getVendor());
		mVersion.setText(Integer.toString(lSenList.get(arg2).getVersion()));
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
		
	}

}
