package org.instk.datamonitor;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;
import android.location.GpsStatus;


public class SatMonitor extends Activity implements GpsStatus.Listener, GpsStatus.NmeaListener, LocationListener {
	LocationManager mLocMan=null;
	GpsStatus mStatus=null;
	
	TextView Az;
	TextView Prn;
	TextView El;
	TextView Snr;
	TextView Alma;
	TextView Eph;
	TextView Fix;
	CLogView tv;
	TextView[] Pos=new TextView[4];
	TextView Stat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.satmonitor);
		LocationManager lLocMan=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocMan=lLocMan;
		
		//Get the references to the views
		tv=(CLogView) findViewById(R.id.SaMtv1);
		Prn=(TextView) findViewById(R.id.SatPr);
		Az=(TextView) findViewById(R.id.SatAz);
		El=(TextView) findViewById(R.id.SatEl);
		Snr=(TextView) findViewById(R.id.SatSn);
		Alma=(TextView) findViewById(R.id.SatAl);
		Eph=(TextView) findViewById(R.id.SatEp);
		Fix=(TextView) findViewById(R.id.SatFx);
		Stat=(TextView) findViewById(R.id.SatStat);
		
		Pos[0]=(TextView) findViewById(R.id.SatLat);
		Pos[1]=(TextView) findViewById(R.id.SatLon);
		Pos[2]=(TextView) findViewById(R.id.SatAlt);
		Pos[3]=(TextView) findViewById(R.id.SatSatn);
	}

	@Override
	protected void onPause() {
		//Remove listener
		LocationManager lLocMan=mLocMan;
		lLocMan.removeGpsStatusListener(this);
		lLocMan.removeNmeaListener(this);
		super.onPause();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationManager lLocMan=mLocMan;
		//register for the GPS status listener
		lLocMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		lLocMan.addGpsStatusListener(this);
		lLocMan.addNmeaListener(this);
		
	}

	//GPS Listener implementations
	public void onGpsStatusChanged(int arg0) {
		//Print out the status
		if (arg0==GpsStatus.GPS_EVENT_FIRST_FIX)
			Stat.setText("GPS Status :GPS_EVENT_FIRST_FIX");
		else if (arg0==GpsStatus.GPS_EVENT_SATELLITE_STATUS)
			Stat.setText("GPS Status :GPS_EVENT_SATELLITE_STATUS");
		else if (arg0==GpsStatus.GPS_EVENT_STARTED)
			Stat.setText("GPS Status :GPS_EVENT_STARTED");
		else if (arg0==GpsStatus.GPS_EVENT_STOPPED)
			Stat.setText("GPS Status :GPS_EVENT_STOPPED");
		
		//Get the status
		GpsStatus lStatus=mStatus;
		tv.addtext("GPS Status changed\n");
		if (lStatus==null)
			lStatus=mLocMan.getGpsStatus(null);
		else	
			mLocMan.getGpsStatus(lStatus);
		
		String prn="",az="",el="",snr="",alma="",eph="",fix="";
		for (GpsSatellite sat:lStatus.getSatellites()) {
			prn=prn+sat.getPrn()+"\n";
			az=az+sat.getAzimuth()+"\n";
			el=el+sat.getElevation()+"\n";
			snr=snr+sat.getSnr()+"\n";
			if (sat.hasAlmanac())
				alma=alma+"Y"+"\n";
			else
				alma=alma+"N"+"\n";
			
			if (sat.hasEphemeris())
				eph=eph+"Y"+"\n";
			else
				eph=eph+"N"+"\n";
			
			if (sat.usedInFix())
				fix=fix+"Y"+"\n";
			else
				fix=fix+"N"+"\n";
		}
		
		
		Prn.setText(prn);
		Az.setText(az);
		El.setText(el);
		Snr.setText(snr);
		Alma.setText(alma);
		Eph.setText(eph);
		Fix.setText(fix);
	}

	//NMEA listener
	public void onNmeaReceived(long arg0, String arg1) {
		tv.addtext("NMEA Status changed\n");
	}

	public void onLocationChanged(Location location) {
		TextView[] lPos=Pos;
		lPos[0].setText(Double.toString(location.getLatitude()));
		lPos[1].setText(Double.toString(location.getLongitude()));
		lPos[2].setText(Double.toString(location.getAltitude()));
		
		int nsat=location.getExtras().getInt("satellites", -1);
		lPos[3].setText(Integer.toString(nsat));
	}

	public void onProviderDisabled(String provider) {
		tv.addtext("GPS Provider Disabled\n");
	}

	public void onProviderEnabled(String provider) {
		tv.addtext("GPS Provider Enabled\n");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		if (status==LocationProvider.OUT_OF_SERVICE){
			tv.addtext(provider+ "status:OUT_OF_SERVICE");
		}
		else if (status==LocationProvider.TEMPORARILY_UNAVAILABLE){
			tv.addtext(provider+ "status:TEMPORARILY_UNAVAILABLE");
		}
		else if (status==LocationProvider.AVAILABLE){
			tv.addtext(provider+ "status:AVAILABLE");
		}
	}

}
