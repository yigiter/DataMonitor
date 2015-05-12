package org.instk.datamonitor;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class About extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		
		TextView ltv1 = (TextView) findViewById(R.id.Atv1);
		ltv1.setText("Data Monitor (Alpha Release)\n2011 - www.instk.org\n");
		Linkify.addLinks(ltv1, Linkify.ALL);
		
		TextView ltv2 = (TextView) findViewById(R.id.Atv2);
		ltv2.setText("This application is being developed as a part of open source inertial navigation toolkit.\n Source codes are available at github.com/yigiter/");
		Linkify.addLinks(ltv2, Linkify.ALL);
		
		TextView ltv3 = (TextView) findViewById(R.id.Atv3);
		ltv3.setText("Contributors:\n  Yigiter Yuksel\n  Burak Kaygisiz");
	}

}
