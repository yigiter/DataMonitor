package org.instk.datamonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;



public class SensorRateSelect extends Activity implements View.OnClickListener {

	CSensorStates mSenStates;
	RadioGroup mrg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.sensorrateselect);
		Button btndiscard=(Button) findViewById(R.id.SRSbutton1);
		Button btnsave=(Button) findViewById(R.id.SRSbutton2);
		mrg=(RadioGroup) findViewById(R.id.SRSradioGroup1);
		
		btndiscard.setOnClickListener(new View.OnClickListener() {
			public void onClick (View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		btnsave.setOnClickListener(this);
	
		//In fact for this activity only rate is sufficient.
		//However, in the future, I will implement another activity which lets user to specify each sensor's rate separately.
		//That is why, I transfer entire sensor states.
		Intent lintent=getIntent();
		CSensorStates lSenStates=(CSensorStates) lintent.getParcelableExtra("sensor_states");
		mSenStates=lSenStates;
		
		//Set the current value
		if (lSenStates.getNum()>0) {
			int rate=lSenStates.getRate(0); //I assume all the same
			switch (rate) {
			case (SensorManager.SENSOR_DELAY_FASTEST):
				mrg.check(R.id.SRSradio0);
				break;
			case (SensorManager.SENSOR_DELAY_GAME):
				mrg.check(R.id.SRSradio1);
				break;
			case (SensorManager.SENSOR_DELAY_NORMAL):
				mrg.check(R.id.SRSradio2);
				break;
			case (SensorManager.SENSOR_DELAY_UI):
				mrg.check(R.id.SRSradio3);
				break;
			}
		}
	}

	@Override //Do nothing
	protected void onPause() {
		super.onPause();
		
		setResult(RESULT_CANCELED);
		finish();
	}

	public void onClick(View arg0) {
		int rate;
		switch (mrg.getCheckedRadioButtonId()) {
		case R.id.SRSradio0:
			rate=SensorManager.SENSOR_DELAY_FASTEST;
			break;
		case R.id.SRSradio1:
			rate=SensorManager.SENSOR_DELAY_GAME;
			break;
		case R.id.SRSradio2:
			rate=SensorManager.SENSOR_DELAY_UI;
			break;
		case R.id.SRSradio3:
			rate=SensorManager.SENSOR_DELAY_NORMAL;
			break;
		default:
			rate=SensorManager.SENSOR_DELAY_UI;
		}
		
		
		//Write the new values to the pref file and return to the previous view
		SharedPreferences lPrefs = getSharedPreferences("SensorMonPrefs", MODE_PRIVATE);
		SharedPreferences.Editor lPrefEd=lPrefs.edit();
		
		CSensorStates lSenStates=mSenStates;
		for (int i=0; i<lSenStates.getNum(); i++) {
			lPrefEd.putInt(lSenStates.getName(i)+"_rate", rate);
		}
		lPrefEd.commit();
		
		//Quit
		setResult(RESULT_OK);
		finish();
	}
	
	

}
