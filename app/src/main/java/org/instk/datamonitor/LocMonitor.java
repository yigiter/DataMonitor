package org.instk.datamonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.TextView;

public class LocMonitor extends Activity implements LocationListener {
	LocationManager mLocMan=null;
	CLocProvStates mLPStates=null;
	TextView[] mTv=new TextView[10];
	CLogView mLTv=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//Location providers
		mLocMan=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLPStates = new CLocProvStates(mLocMan.getAllProviders());
		
		CLocProvStates lLPStates=mLPStates;
		LocationManager lLocMan=mLocMan;
		
		setContentView(R.layout.locmonitor);
		
		//Text views
		mLTv = (CLogView) findViewById(R.id.LMMltv);
		mTv[0]=(TextView) findViewById(R.id.LMMName);
		mTv[1]=(TextView) findViewById(R.id.LMMaccuracy);
		mTv[2]=(TextView) findViewById(R.id.LMMaltitude);
		mTv[3]=(TextView) findViewById(R.id.LMMbearing);
		mTv[4]=(TextView) findViewById(R.id.LMMlatitude);
		mTv[5]=(TextView) findViewById(R.id.LMMlongtitude);
		mTv[6]=(TextView) findViewById(R.id.LMMspeed);
		mTv[7]=(TextView) findViewById(R.id.LMMTime);
		mTv[8]=(TextView) findViewById(R.id.LMMextras);
		
		CLogView lLTv=mLTv;
		//Read the pref
		read_prefs();
		
		//Show whether or not  the providers are enabled
		for (int i=0; i<lLPStates.getNum();i++){
			if (lLocMan.isProviderEnabled(lLPStates.getName(i)))
				lLTv.addtext(lLPStates.getName(i) + " is enabled");
			else
				lLTv.addtext(lLPStates.getName(i) + " is disabled. You should enable it from the settings.");
		}
		
		
		//Show the last position of each provider
		Location lLoc;
		for (int i=0;i<lLPStates.getNum();i++){
			lLoc=lLocMan.getLastKnownLocation(lLPStates.getName(i));
			if (lLoc!=null)
				lLTv.addtext(lLoc.getProvider() + "\nLat :" + Double.toString(lLoc.getLatitude()) + "\nLong :" + Double.toString(lLoc.getLongitude()));
		}
	}

	@Override
	protected void onPause() {
		
		//Unregister all sensors
		mLocMan.removeUpdates(this);
		
		//Save the states as prefs
		CLocProvStates lLPStates=mLPStates;
		
		//Write the new values to the pref file and return to the previous view
		SharedPreferences lPrefs = getSharedPreferences("LocMonPrefs", MODE_PRIVATE);
		SharedPreferences.Editor lPrefEd=lPrefs.edit();
		
		for (int i=0; i<lLPStates.getNum(); i++) {
			lPrefEd.putBoolean(lLPStates.getName(i), lLPStates.getActive(i));
			lPrefEd.putFloat(lLPStates.getName(i)+"_mindist", lLPStates.getMinDist(i));
			lPrefEd.putLong(lLPStates.getName(i)+"_mintime", lLPStates.getMinTime(i));
		}
		lPrefEd.commit();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		//Register listener for the selected provides
		CLocProvStates lLPStates=mLPStates;
		for (int i=0;i<lLPStates.getNum();i++) {
			if (lLPStates.getActive(i)) 
				mLocMan.requestLocationUpdates(lLPStates.getName(i) , lLPStates.getMinTime(i), lLPStates.getMinDist(i), this);
		}

	}
	
	private void read_prefs() {
		SharedPreferences lPrefs = getSharedPreferences("LocMonPrefs", MODE_PRIVATE);
		CLocProvStates lLPStates=mLPStates;
		
		//Read the preferences
		boolean val;
		float mindist;
		long mintime;
		for (int i=0; i<lLPStates.getNum(); i++) {
			val=lPrefs.getBoolean(lLPStates.getName(i), false);
			lLPStates.setActive(i,val);
			mindist=lPrefs.getFloat(lLPStates.getName(i)+"_mindist", 0);
			mintime=lPrefs.getLong(lLPStates.getName(i)+"_mintime", 0);
			lLPStates.setCriterion(i,mindist, mintime);
		}
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {      
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.locmonitormenu, menu);
        
        //Fill the content of the "Choose Provider" Group
        CLocProvStates lLPStates=mLPStates;
        
        SubMenu lsubmenu=menu.findItem(R.id.LMMitem1).getSubMenu();
        for (int i=0;i<lLPStates.getNum();i++) {
        	lsubmenu.add(R.id.LMMgroup1, 0x33+i, Menu.NONE, lLPStates.getName(i)); //How to generate a unique id?
        }
        lsubmenu.setGroupCheckable(R.id.LMMgroup1, true, false);
        return true;
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//Set the current active provider(s) for the menu
		CLocProvStates lLPStates=mLPStates;
        for (int i=0;i<lLPStates.getNum();i++) {
        	menu.findItem(0x33+i).setChecked(lLPStates.getActive(i));
        }
		
		return true;
	}
	
	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		if (item.hasSubMenu())
			return true;	//Do nothing
		
		CLocProvStates lLPStates=mLPStates;
		int id=item.getItemId();
		if (id>=0x33 && id<0x33+lLPStates.getNum())
			lLPStates.setToggle(id-0x33);
		else if (item.getItemId()==R.id.LMMitem2){
			if (lLPStates.getNum()>0) {
				Intent lintent=new Intent(this, LocProvRate.class);
				lintent.putExtra("mindist", lLPStates.getMinDist(0));	//Assumes all providers uses the same criterion
				lintent.putExtra("mintime", lLPStates.getMinTime(0));
				startActivityForResult(lintent, 0);
			}
		}
		return true;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode==0) //Rate results
			if (resultCode==RESULT_OK) { //User prefers to save the results
				float lmd=data.getFloatExtra("mindist", 0);
				long lmt=data.getLongExtra("mintime", 0);
				mLPStates.setCriterion(lmd, lmt);
				mLTv.addtext("MinDist :"+Float.toString(lmd)+" - MinTime" + Long.toString(lmt));
			}		
	}

	/////////////////Location Listener Functions
	public void onLocationChanged(Location loc) {
		// TODO Auto-generated method stub
		
		TextView[] lTv=mTv;
		lTv[0].setText(loc.getProvider());
		lTv[1].setText(Float.toString(loc.getAccuracy()));
		lTv[2].setText(Double.toString(loc.getAltitude()));
		lTv[3].setText(Float.toString(loc.getBearing()));
		lTv[4].setText(Double.toString(loc.getLatitude()));
		lTv[5].setText(Double.toString(loc.getLongitude()));
		lTv[6].setText(Float.toString(loc.getSpeed()));
		lTv[7].setText(Long.toString(loc.getTime()));
		//TODO change this.
		lTv[8].setText(loc.getExtras().toString());
		
	}


	public void onProviderDisabled(String arg0) {
		mLTv.addtext(arg0 + "is Disabled");
	}


	public void onProviderEnabled(String arg0) {
		mLTv.addtext(arg0 + "is Enabled");
	}


	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		mLTv.addtext(arg0 + " Status Changed");
	}

}
