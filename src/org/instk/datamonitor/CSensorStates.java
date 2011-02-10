package org.instk.datamonitor;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Parcel;
import android.os.Parcelable;

public class CSensorStates  implements Parcelable{
	private String[] names=null;
	private int[] types=null;
	private boolean[] act_list=null;
	private int nsen=0;
	private int[] rates=null;
	
	public CSensorStates (boolean val) { //Constructor for debugging in simulator
		nsen=3;
		
		names = new String[nsen];
		types = new int[nsen];
		act_list=new boolean[nsen];
		rates=new int[nsen];
		
		names[0]="ivmeolceer";
		names[1]="donuolcer";
		names[2]="termo";
		act_list[0]=true;
		act_list[1]=false;
		act_list[2]=true;
		types[0]=0;
		types[1]=1;
		types[2]=2;
		rates[0]=5;
		rates[1]=5;
		rates[2]=5;
	}
	
	public CSensorStates (List<Sensor> aSList) {
		nsen=aSList.size();
		
		if (nsen>0) {
			names = new String[nsen];
			types = new int[nsen];
			act_list=new boolean[nsen];
			rates=new int[nsen];
			
			//Set name
			for (int i=0;i<nsen;i++) {
				switch (aSList.get(i).getType()) {
				case (Sensor.TYPE_ACCELEROMETER):
					names[i]="ACCELEROMETER";
					break;
				case (Sensor.TYPE_GRAVITY):
					names[i]="GRAVITY";
					break;
				case (Sensor.TYPE_GYROSCOPE):
					names[i]="GYROSCOPE";
					break;
				case (Sensor.TYPE_LIGHT):
					names[i]="LIGHT";
					break;
				case (Sensor.TYPE_LINEAR_ACCELERATION):
					names[i]="LINEAR_ACCELERATION";
					break;
				case (Sensor.TYPE_MAGNETIC_FIELD):
					names[i]="MAGNETIC_FIELD";
					break;
				case (Sensor.TYPE_ORIENTATION):
					names[i]="ORIENTATION";
					break;
				case (Sensor.TYPE_PRESSURE):
					names[i]="PRESSURE";
					break;
				case (Sensor.TYPE_PROXIMITY):
					names[i]="PROXIMITY";
					break;
				case (Sensor.TYPE_ROTATION_VECTOR):
					names[i]="ROTATION_VECTOR";
					break;
				case (Sensor.TYPE_TEMPERATURE):
					names[i]="TEMPERATURE";
					break;
				default:
					names[i]=aSList.get(i).getName();
				}
				//Set type and active status
				types[i]=aSList.get(i).getType();
				act_list[i]=false;
				rates[i]=SensorManager.SENSOR_DELAY_NORMAL;
			}
		}
	}
	
	String[] getNames() {
		return names; 
	}
	
	String getName(int i) {
		return names[i];
	}
	
	String getNameByType(int typ) {
		for (int i=0;i<nsen;i++) {
			if (types[i]==typ)
				return names[i];
		}
		
		return "Undefined Type";
	}
	
	void setActive(int i, boolean val) {
		act_list[i]=val;
	}
	
	void setRate(int i, int val) {
		rates[i]=val;
	}
	
	void setActToggle(int i){
		act_list[i]=!act_list[i];
	}
	
	void setRate(int val) {
		for (int i=0;i<nsen;i++)
			rates[i]=val;
	}
	
	int getRate(int i) {
		return rates[i];
	}
	
	Boolean getActive(int i) {
		return act_list[i];
	}
	
	int getNum(){
		return nsen;
	}
	
	int getNumAct() {
		int nact=0;
		for (int i=0;i<nsen;i++) {
			if (act_list[i])
				nact++;
		}
		
		return nact;
	}
	
	public int getType(int i) {
		return types[i];
	}

	//////////////////////////////////////////////////////////////////
	//Required for Parcelable classes
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flag) {
		out.writeInt(nsen);
		out.writeStringArray((String[]) names);
		out.writeIntArray(types);
		out.writeBooleanArray(act_list);
		out.writeIntArray(rates);
	}
	
	public static final Parcelable.Creator<CSensorStates> CREATOR
	= new Parcelable.Creator<CSensorStates>() {
		public CSensorStates createFromParcel(Parcel source) {
			return new CSensorStates(source);
			}

		public CSensorStates[] newArray(int size) {
			return new CSensorStates[size];
		}
	};
	
	private CSensorStates(Parcel source) {
		nsen=source.readInt();
		
		if (nsen>0) {
			names = new String[nsen];
			types = new int[nsen];
			rates = new int[nsen];
			act_list=new boolean[nsen];
			source.readStringArray((String[]) names);
			source.readIntArray(types);
			source.readBooleanArray(act_list);
			source.readIntArray(rates);
		}
	}

};


