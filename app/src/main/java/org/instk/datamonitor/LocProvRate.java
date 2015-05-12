package org.instk.datamonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LocProvRate extends Activity implements OnClickListener{
	long mindist,mintime;
	EditText et1,et2;
	Button bt1, bt2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.locprovrate);
		Intent lIntent=getIntent();
		mindist=(long) lIntent.getFloatExtra("mindist", 0);
		mintime=lIntent.getLongExtra("mintime", 0);
		
		et1=(EditText) findViewById(R.id.LPRet1);
		et2=(EditText) findViewById(R.id.LPRet2);
		bt1=(Button) findViewById(R.id.LPRbut1);	//Save
		bt2=(Button) findViewById(R.id.LPRbut2);	//Discard
		et1.setText(Long.toString(mindist));
		et2.setText(Long.toString(mintime));
		
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		setResult(RESULT_CANCELED);
		finish();
	}

	public void onClick(View but) {
		int id=but.getId();
		
		if (id==R.id.LPRbut1) { //return the new values
			mindist=Long.valueOf(et1.getText().toString().trim());
			mintime=Long.valueOf(et2.getText().toString().trim());	
			
			Intent lintent=new Intent();
			lintent.putExtra("mindist", (float) mindist);
			lintent.putExtra("mintime", (long) (mintime));
			setResult(RESULT_OK, lintent);
			finish();
		}
		else if (id==R.id.LPRbut2) { //discard and return
			setResult(RESULT_CANCELED);
			finish();
		}
		
	}

}
