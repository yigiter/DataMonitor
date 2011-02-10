package org.instk.datamonitor;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class CLocProvStates implements Parcelable {

	private String[] names=null;
	private int[] types=null;
	private boolean[] act_list=null;
	private long[] mintime=null;
	private float[] mindist=null;
	private int nman=0;
	
	public CLocProvStates(List<String> LocMan) {
		nman=LocMan.size();
		
		if (nman>0) {
			names=new String[nman];
			act_list=new boolean[nman];
			mintime=new long[nman];
			mindist=new float[nman];
			types=new int[nman];
			for (int i=0;i<nman;i++) {
				names[i]=LocMan.get(i);
				act_list[i]=false;
				mintime[i]=0;
				mindist[i]=0;
				types[i]=i;		//An integer type alias simplifies the matter
			}
		}
	}
	
	String[] getNames() {
		return names; 
	}
	
	String getName(int i) {
		return names[i]; 
	}
	
	int getNum() {
		return nman;
	}
	
	int getNumAct() {
		int nact=0;
		for (int i=0;i<nman;i++) {
			if (act_list[i])
				nact++;
		}
		
		return nact;
	}
	
	boolean getActive(int i) {
		return act_list[i];
	}
	
	long getMinTime(int i) {
		return mintime[i];
	}
	
	float getMinDist(int i) {
		return mindist[i];
	}
	
	void setActive(int i, boolean val){
		act_list[i]=val;
	}
	
	void setActToggle(int i){
		act_list[i]=!act_list[i];
	}
	
	void setToggle(int i){
		act_list[i]=!act_list[i];
	}
	
	void setCriterion(int i, float mind, long mint){
		mindist[i]=mind;
		mintime[i]=mint;
	}
	
	void setCriterion(float mind, long mint){
		for (int i=0;i<nman;i++) {
			mindist[i]=mind;
			mintime[i]=mint;	
		}
	}
	
	public boolean isExist(String provider) {
		for (int i=0;i<nman; i++) {
			if (names[i].equals(provider)) return true;
		}
		
		return false;
	}
	
	
	//////////////////////////////////////////////////////////////
	///Parcel Implementation
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int arg1) {		
		out.writeInt(nman);
		out.writeStringArray((String[]) names);
		out.writeBooleanArray(act_list);
		out.writeLongArray(mintime);
		out.writeFloatArray(mindist);
		out.writeIntArray(types);
	}

	public static final Parcelable.Creator<CLocProvStates> CREATOR
	= new Parcelable.Creator<CLocProvStates>() {
		public CLocProvStates createFromParcel(Parcel source) {
			return new CLocProvStates(source);
			}

		public CLocProvStates[] newArray(int size) {
			return new CLocProvStates[size];
		}
	};
	
	private CLocProvStates(Parcel source) {
		nman=source.readInt();
		
		if (nman>0) {
			names = new String[nman];
			act_list=new boolean[nman];
			mintime=new long[nman];
			mindist=new float[nman];
			types=new int[nman];
			source.readStringArray((String[]) names);
			source.readBooleanArray(act_list);
			source.readLongArray(mintime);
			source.readFloatArray(mindist);
			source.readIntArray(types);
		}
	}
}
