package org.instk.datamonitor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class main extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String[] taskList={"Sensor Information", "Monitor Sensors", "Location Manager Information", "Monitor Location Managers", "Monitor GPS Satellites", "Log Data", "About"};
        
        setListAdapter(new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, taskList));
    }
    
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent lIntent=null;
        switch (position) {
        case 0:
        	lIntent=new Intent(this, SensorInfo.class);
        	break;
        case 1:
        	lIntent=new Intent(this, SensorMonitor.class);
        	break;
        case 2:
        	lIntent=new Intent(this, LocInfo.class);
        	break;
        case 3:
        	lIntent=new Intent(this, LocMonitor.class);
        	break;
        case 4:
        	lIntent=new Intent(this, SatMonitor.class);
        	break;
        case 5:
        	lIntent=new Intent(this, DatLog.class);
        	break;
        case 6:
        	lIntent=new Intent(this, About.class);
        	break;
        }
        
        if (lIntent!=null)
        	startActivity(lIntent);
    }
}