package org.instk.datamonitor;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;

public class DatLog extends TabActivity implements OnClickListener, SensorEventListener, LocationListener, Listener {
	CSensorStates mSenStates;
	CLocProvStates mLPStates;
	boolean mGPSState;
	CLogView mLV;
	int evno=0;
	
	SensorManager mSenMan;
	LocationManager mLocMan;
	
	Button mbtn_start,mbtn_stop;
	TabWidget mTabWidget;
	
	private DataOutputStream[] fout=new DataOutputStream[3];
	private SimpleDateFormat dtf= new SimpleDateFormat("dd.HH.mm.ss");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SensorManager lSenMan=(SensorManager) getSystemService(SENSOR_SERVICE);
		LocationManager lLocMan=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mSenMan=lSenMan;
		mLocMan=lLocMan;
		
		//Get the names of all sources
		mSenStates = new CSensorStates(lSenMan.getSensorList(Sensor.TYPE_ALL));
		mLPStates = new CLocProvStates(lLocMan.getAllProviders());
		mGPSState= false;
		mSenStates.setRate(SensorManager.SENSOR_DELAY_FASTEST);	//Set the sensor rate to the maximum (default is UI)
		CSensorStates lSenStates=mSenStates;
		CLocProvStates lLPStates=mLPStates;
		
		
		//Read the prefs
		read_prefs();
		
		//Construct the view
		setContentView(R.layout.datlog);
		//Main Tab (tab0)
		mLV=(CLogView) findViewById(R.id.DLtv1);
		Button lbtn_start=(Button) findViewById(R.id.DLbtn0);
		Button lbtn_event=(Button) findViewById(R.id.DLbtn1);
		Button lbtn_stop=(Button) findViewById(R.id.DLbtn2);
		Button lbtn_erec=(Button) findViewById(R.id.DLbtn3);
		Button lbtn_show=(Button) findViewById(R.id.DLbtn4);
		
		lbtn_start.setOnClickListener(this);
		lbtn_event.setOnClickListener(this);
		lbtn_stop.setOnClickListener(this);
		lbtn_erec.setOnClickListener(this);
		lbtn_show.setOnClickListener(this);
		
		lbtn_stop.setEnabled(false);
		mbtn_start=lbtn_start;
		mbtn_stop=lbtn_stop;
		
		
		//The sensor selection tab
		ListView llistview1=(ListView) findViewById(R.id.DLtab2);
		ArrayAdapter<String> ladapter1=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lSenStates.getNames());
		llistview1.setAdapter(ladapter1);
		//Show the current sensor selection
        for (int i=0;i<lSenStates.getNum();i++){
        	llistview1.setItemChecked(i, lSenStates.getActive(i));
        }
        llistview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View aview, int position,
					long arg3) {
				mSenStates.setActToggle(position);
			}
        });
        
        
        
		//Location Provider Selection Tab
        ListView llistview2=(ListView) findViewById(R.id.DLtab3);
		llistview2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, lLPStates.getNames()));
		//Show the current sensor selection
        for (int i=0;i<lLPStates.getNum();i++){
        	llistview2.setItemChecked(i, lLPStates.getActive(i));
        }
        llistview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View aview, int position,
					long arg3) {
				mLPStates.setActToggle(position);
			}
        });
        
        
        //Gps Status selection tab
		CheckedTextView lcheckview=(CheckedTextView) findViewById(R.id.DLck1);
		lcheckview.setChecked(mGPSState);
		lcheckview.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mGPSState = !mGPSState;
				((CheckedTextView) v).setChecked(mGPSState);
			};
		}
		);
		
		
		//Create the tabs
		TabHost lTabHost = getTabHost();
	    lTabHost.addTab(lTabHost.newTabSpec("tab1").setIndicator("Control").setContent(R.id.DLtab1));
	    lTabHost.addTab(lTabHost.newTabSpec("tab2").setIndicator("Sensors").setContent(R.id.DLtab2));
	    lTabHost.addTab(lTabHost.newTabSpec("tab3").setIndicator("Providers").setContent(R.id.DLtab3));
	    if (mLPStates.isExist(LocationManager.GPS_PROVIDER)) 
	    	lTabHost.addTab(lTabHost.newTabSpec("tab4").setIndicator("GPS Status").setContent(R.id.DLtab4));
	    
	    lTabHost.setCurrentTab(0);
	    mTabWidget=lTabHost.getTabWidget();	    
	}
	
	private void read_prefs() {
		SharedPreferences lPrefs = getSharedPreferences("DatLogPrefs", MODE_PRIVATE);
		CLocProvStates lLPStates=mLPStates;
		CSensorStates lSenNames=mSenStates;
		float mindist;
		long mintime;
		boolean val;
		int rate;
		
		//Read the sensor preferences
		for (int i=0; i<lSenNames.getNum(); i++) {
			val=lPrefs.getBoolean(lSenNames.getName(i), false);
			lSenNames.setActive(i,val);
			rate=lPrefs.getInt(lSenNames.getName(i)+"_rate", SensorManager.SENSOR_DELAY_FASTEST);
			lSenNames.setRate(i,rate);
		}
		
		//Read the location provider preferences
		for (int i=0; i<lLPStates.getNum(); i++) {
			val=lPrefs.getBoolean(lLPStates.getName(i), false);
			lLPStates.setActive(i,val);
			mindist=lPrefs.getFloat(lLPStates.getName(i)+"_mindist", 0);
			mintime=lPrefs.getLong(lLPStates.getName(i)+"_mintime", 0);
			lLPStates.setCriterion(i,mindist, mintime);
		}
		
		//Read the GPS Status preference
		val=lPrefs.getBoolean("gps_status", false);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		CSensorStates lSenStates=mSenStates;
		CLocProvStates lLPStates=mLPStates;
		
		//Write the preferences
		SharedPreferences lPrefs = getSharedPreferences("DatLogPrefs", MODE_PRIVATE);
		SharedPreferences.Editor lPrefEd=lPrefs.edit();
		
		//Gps prefs
		for (int i=0; i<lLPStates.getNum(); i++) {
			lPrefEd.putBoolean(lLPStates.getName(i), lLPStates.getActive(i));
			lPrefEd.putFloat(lLPStates.getName(i)+"_mindist", lLPStates.getMinDist(i));
			lPrefEd.putLong(lLPStates.getName(i)+"_mintime", lLPStates.getMinTime(i));
		}
		lPrefEd.putBoolean("gps_status", mGPSState);
		
		//Sensor prefs
		for (int i=0; i<lSenStates.getNum(); i++) {
			lPrefEd.putBoolean(lSenStates.getName(i), lSenStates.getActive(i));
			lPrefEd.putInt(lSenStates.getName(i)+"_rate", lSenStates.getRate(i));
		}
		
		lPrefEd.commit();
		
		super.onDestroy();
	}

	public void onClick(View arg0) {
		
		if (arg0.getId()==R.id.DLbtn0) { //Start Recording
			try {
				open_files();
				register_listeners();
				mLV.addtext("Started Logging");
			} catch (FileNotFoundException e) {
				mLV.addtext("File open error: Probably you do not have require permissions.");
				stop_recording();
			}
		}
		
		else if (arg0.getId()==R.id.DLbtn1) { //Put an event marker
			mLV.addtext("Event No:"+ evno +" Time :" + System.currentTimeMillis());
			evno++; 
		}
		else if (arg0.getId()==R.id.DLbtn2) { //Stop Recording
			stop_recording();
			mLV.addtext("Stopped Logging");
		}
		else if (arg0.getId()==R.id.DLbtn3) { //Dump console to the file
			dump_console();
			mLV.addtext("Console Dumped");
		}
		else if (arg0.getId()==R.id.DLbtn4) { //Dump console to the file
			show_registered();
		}
	}
	
	private void show_registered() {
		CSensorStates lSenStates=mSenStates;
		CLocProvStates lLPStates=mLPStates;
					
		String nt="Registered Sources:";
		int n=0;
		for (int i=0;i<lSenStates.getNum();i++) {
			if (lSenStates.getActive(i)) {
				nt=nt+"\n\t" + lSenStates.getName(i);
				n++;
			}
		}
		
		for (int i=0;i<lLPStates.getNum();i++) {
			if (lLPStates.getActive(i)) {
				nt=nt+"\n\t" + lLPStates.getName(i);
				n++;
			}
		}
		
		if (mGPSState) {
			nt=nt+"\n\tGPS Status";
			n++;
		}
		
		if (n==0) {
			nt="No Registered Source.";
		}
		
		mLV.addtext(nt);
	}
	
	private void close_files() {
		DataOutputStream[] lfout=fout;
		
		for (int i=0;i<3;i++) {
			if (lfout[i]!=null)
				try {
					lfout[i].close();
				} catch (IOException e) {
					mLV.addtext("File close error :" + i);
				}
		}
	}
	
	private void open_files() throws FileNotFoundException {
		//Adjust view
		mbtn_start.setEnabled(false);
		mbtn_stop.setEnabled(true);
		mTabWidget.setEnabled(false);
		
		
		//Refs
		CSensorStates lSenStates=mSenStates;
		CLocProvStates lLPStates=mLPStates;
		DataOutputStream[] lfout=fout;
		
				
		//Open the files and register the listeners
		if (lSenStates.getNumAct()>0) {
			lfout[0]=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file_location("_sensors.bin"))));
		}
		else
			lfout[0]=null;
		
		if (lLPStates.getNumAct()>0) {
			lfout[1]=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file_location("_locprovider.bin"))));
		}
		else
			lfout[1]=null;
		
		if (mGPSState) {
			lfout[2]=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file_location("_gpsstate.bin"))));
		}
		else
			lfout[2]=null;
	}
	
	private File file_location(String ntag) {
		
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (mExternalStorageAvailable && mExternalStorageWriteable) {
			String ftag=dtf.format(new Date());
			//return new File(Environment.getExternalStorageDirectory(), ftag+ntag);
			return new File(getExternalFilesDir(null), ftag+ntag);
		}
		else {
			mLV.addtext("No external Storage.");
			return null;
		}
	}
	
	private void register_listeners() {
		
		CSensorStates lSenStates=mSenStates;
		CLocProvStates lLPStates=mLPStates;
		DataOutputStream[] lfout=fout;
		SensorManager lSenMan=mSenMan;
		LocationManager lLocMan=mLocMan;
		
		//Register the sensors
		if (lfout[0]!=null) {
			for (int i=0;i<lSenStates.getNum();i++) {
				if (lSenStates.getActive(i))
					lSenMan.registerListener(this, lSenMan.getDefaultSensor(lSenStates.getType(i)), lSenStates.getRate(i));
			}
		}
		
		//Register listeners for active location providers
		if (lfout[1]!=null) {	
			for (int i=0;i<lLPStates.getNum();i++) {
				if (lLPStates.getActive(i))
					lLocMan.requestLocationUpdates(lLPStates.getName(i), lLPStates.getMinTime(i), lLPStates.getMinDist(i), this);
			}
		}
		
		if (lfout[2]!=null) {
			lLocMan.addGpsStatusListener(this);
		}
	}
	
	private void stop_recording() {
		//Stop Recording
		mSenMan.unregisterListener(this);
		mLocMan.removeGpsStatusListener(this);
		mLocMan.removeUpdates(this);
		
		//Close files
		close_files();
		
		
		//Adjust view
		mbtn_start.setEnabled(true);
		mbtn_stop.setEnabled(false);
		mTabWidget.setEnabled(true);
	}
	
	private void dump_console() {
		try {
			DataOutputStream file=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file_location("_console.bin"))));
			file.writeChars(mLV.getText().toString());
			file.close();
		}
		catch (FileNotFoundException e) {
			mLV.addtext("Could open file for dumping");
			}
		catch (IOException e1) {
			mLV.addtext("Could not dump the console");
		}
	}

	///////////Sensor Listener Callbacks
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//mLV.addtext(arg0.getName() + ":Accuracy changed");
	}

	public void onSensorChanged(SensorEvent ev) {
		DataOutputStream file=fout[0];
		if (file==null)
			//Something is wrong
			return;
		
		long tim=System.currentTimeMillis();
		int len=ev.values.length;
		try {
			file.writeInt(ev.sensor.getType());
			file.writeLong(tim);
			file.writeLong(ev.timestamp);
			file.writeInt(len);
			for (int i=0;i<len;i++)
				file.writeFloat(ev.values[i]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/////////Location provider callbacks
	public void onLocationChanged(Location loc) {
		DataOutputStream file=fout[1];
		if (file==null)
			//Something is wrong
			return;
		long tim=System.currentTimeMillis();
		int typ=loc.getProvider().length();		//Seems a good identifier
		try {
			file.writeInt(typ);
			file.writeLong(tim);
			file.writeLong(loc.getTime());
			file.writeFloat(loc.getAccuracy());
			file.writeDouble(loc.getAltitude());
			file.writeDouble(loc.getLatitude());
			file.writeDouble(loc.getLongitude());
			file.writeFloat(loc.getBearing());
			file.writeFloat(loc.getSpeed());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onProviderDisabled(String arg0) {
		mLV.addtext(arg0 + " provider disabled");
	}

	public void onProviderEnabled(String arg0) {
		mLV.addtext(arg0 + " provider enabled");
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		mLV.addtext(arg0 + " status changed :" + arg1);
	}

	///////GPS status callback
	public void onGpsStatusChanged(int status) {
		DataOutputStream file=fout[2];
		long tim=System.currentTimeMillis();
		
		//Get the status
		GpsStatus lStatus=null;
		lStatus=mLocMan.getGpsStatus(null);
		
		if (status==GpsStatus.GPS_EVENT_FIRST_FIX) {
			mLV.addtext("GPS_EVENT_FIRST_FIX - TTFX ="+  lStatus.getTimeToFirstFix());
		}
		else if (status==GpsStatus.GPS_EVENT_STARTED) {
			mLV.addtext("GPS_EVENT_STARTED "+tim);
		}
		else if (status==GpsStatus.GPS_EVENT_STOPPED) {
			mLV.addtext("GPS_EVENT_STOPPED "+tim);
		}
		
		if (lStatus!=null) {
			if (file!=null) {
				try {
					file.writeLong(tim);
					Iterable<GpsSatellite> satlist=lStatus.getSatellites();
					for (GpsSatellite sat:satlist) {
						file.writeInt(sat.getPrn());
						file.writeFloat(sat.getAzimuth());
						file.writeFloat(sat.getElevation());
						file.writeFloat(sat.getSnr());
						file.writeBoolean(sat.hasAlmanac());
						file.writeBoolean(sat.hasEphemeris());
						file.writeBoolean(sat.usedInFix());
						if (satlist.iterator().hasNext())
							file.writeChar('@');
						else
							file.writeChar('#');
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
