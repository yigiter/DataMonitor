package org.instk.datamonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SensorMonitor extends Activity implements SensorEventListener {
	private CSensorStates mSenStates;
	private SensorManager mSenMan;
	
	private TextView[] mTVh=new TextView[10];
	private TextView[] mTVd=new TextView[10];
	
	private int[] mTVIndex=new int[10];
	private int mNSen=0;

	//Get the list of active sensors from preferences
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sensormonitor);
		
		//Get the references to the textViews
		setTVRef();
		
		//Get the sensor list from the context
		mSenMan=(SensorManager) getSystemService(SENSOR_SERVICE);
		mSenStates = new CSensorStates(mSenMan.getSensorList(Sensor.TYPE_ALL));
		//mSenStates = new CSensorStates(true);
		
	}
	
	
	
	@Override
	protected void onPause() {
		//Unregister all sensors
		mSenMan.unregisterListener(this);
		super.onPause();
	}



	@Override
	protected void onResume() {
		super.onResume();
		
		//Get the active sensors from pref file
		read_prefs();
		
		//Adjust the view
		adjust_view();
		
		//Register listener for all active sensors
		CSensorStates lSenStates=mSenStates;
		SensorManager lSenMan=mSenMan;
		for (int i=0;i<lSenStates.getNum();i++) {
			if (lSenStates.getActive(i)) 
				lSenMan.registerListener(this, lSenMan.getDefaultSensor(lSenStates.getType(i)), lSenStates.getRate(i));
		}
	}



	private void setTVRef() {
		TextView[] lTVh=mTVh;
		TextView[] lTVd=mTVd;
		
		lTVh[0]=(TextView)findViewById(R.id.SMtvh1);
		lTVh[1]=(TextView)findViewById(R.id.SMtvh2);
		lTVh[2]=(TextView)findViewById(R.id.SMtvh3);
		lTVh[3]=(TextView)findViewById(R.id.SMtvh4);
		lTVh[4]=(TextView)findViewById(R.id.SMtvh5);
		lTVh[5]=(TextView)findViewById(R.id.SMtvh6);
		lTVh[6]=(TextView)findViewById(R.id.SMtvh7);
		lTVh[7]=(TextView)findViewById(R.id.SMtvh8);
		lTVh[8]=(TextView)findViewById(R.id.SMtvh9);
		lTVh[9]=(TextView)findViewById(R.id.SMtvh10);
		
		lTVd[0]=(TextView)findViewById(R.id.SMtvd1);
		lTVd[1]=(TextView)findViewById(R.id.SMtvd2);
		lTVd[2]=(TextView)findViewById(R.id.SMtvd3);
		lTVd[3]=(TextView)findViewById(R.id.SMtvd4);
		lTVd[4]=(TextView)findViewById(R.id.SMtvd5);
		lTVd[5]=(TextView)findViewById(R.id.SMtvd6);
		lTVd[6]=(TextView)findViewById(R.id.SMtvd7);
		lTVd[7]=(TextView)findViewById(R.id.SMtvd8);
		lTVd[8]=(TextView)findViewById(R.id.SMtvd9);
		lTVd[9]=(TextView)findViewById(R.id.SMtvd10);
	}
	
	private void read_prefs() {
		SharedPreferences lPrefs = getSharedPreferences("SensorMonPrefs", MODE_PRIVATE);
		CSensorStates lSenNames=mSenStates;
		
		//Read the preferences
		boolean val;
		int rate;
		for (int i=0; i<lSenNames.getNum(); i++) {
			val=lPrefs.getBoolean(lSenNames.getName(i), false);
			lSenNames.setActive(i,val);
			rate=lPrefs.getInt(lSenNames.getName(i)+"_rate", SensorManager.SENSOR_DELAY_NORMAL);
			lSenNames.setRate(i,rate);
		}
	}

	private void adjust_view() {
		int lnsen=0;
		CSensorStates lSenNames=mSenStates;
		TextView[] lTVh=mTVh;
		TextView[] lTVd=mTVd;
		
		for (int i=0;i<lSenNames.getNum() && lnsen<10 ;i++) {
			if (lSenNames.getActive(i)) {
				lTVh[lnsen].setText(lSenNames.getName(i));
				lTVh[lnsen].setVisibility(View.VISIBLE);
				lTVd[lnsen].setText("");
				lTVd[lnsen].setVisibility(View.VISIBLE);
				
				mTVIndex[lnsen]=lSenNames.getType(i);
				lnsen++;
			}	
		}
		mNSen=lnsen;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {      
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensurmonitormenu, menu);
        return true;
    }
	
	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		if (item.hasSubMenu())
			return true;	//Do nothing
		
		if (item.getItemId()==R.id.SMselect) {
			//Call sensor list view with current values
			Intent lIntent=new Intent(this, SensorSelect.class);
			lIntent.putExtra("sensor_states", mSenStates);
			startActivityForResult(lIntent, 1);
		}
		else if (item.getItemId()==R.id.SMrate){
			Intent lIntent=new Intent(this, SensorRateSelect.class);
			lIntent.putExtra("sensor_states", mSenStates);
			startActivityForResult(lIntent, 2);
		}
		
		return true;
	}



	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}



	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int[] lTVIndex=mTVIndex;
		float[] data;
		
		for (int i=0;i<mNSen;i++){
			if (lTVIndex[i]==event.sensor.getType()) {
				//Form the string to be written textview
				String dtext="";
				data=event.values;
				int m=0;
				for (;m<data.length-1;m++) {
					dtext=dtext.concat(String.valueOf(data[m]));
					dtext=dtext.concat("\t");
				}
				dtext=dtext.concat(String.valueOf(data[m]));
				
				//write it
				mTVd[i].setText(dtext);
			}
		}
	}
}
