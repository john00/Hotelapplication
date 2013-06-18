package personal.john.GeoSearcher;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class HotelInfo {
	private Location mLocation = null;
	public String mNo = null;
	public String mName = null;
	public String mKanaName = null;
	public String mInfomationUrl = null;
	public String mPlanListUrl = null;
	public String mLatitude = null;
	public String mLongitude = null;
	public String mTelephoneNo = null;
	public String mPrShort = null;
	public String mSpecial = null;


	public HotelInfo(){
		Location location = new Location("RakutenWebService");
		mLocation = location;
	}

	public HotelInfo(final Location location, final String name){
		mLocation = location;
		mName = name;
	}

	public String getNo(){
		return mNo;
	}

	public String getName(){
		return mName;
	}

	public String getKanaName(){
		return mKanaName;
	}

	public Location getLocation(){
		mLocation.setAltitude(0);
		mLocation.setLatitude(Location.convert(mLatitude));
		mLocation.setLongitude(Location.convert(mLongitude));
		return mLocation;
	}

	public GeoPoint getGPoint(){
		GeoPoint gpHotel = new GeoPoint((int)(mLocation.getLatitude()*1E6), (int)(mLocation.getLongitude()*1E6));
		return gpHotel;
	}

	public String getInfomationUrl(){
		return mInfomationUrl;
	}

	public String getPlanListUrl(){
		return mPlanListUrl;
	}

	public String getTelephoneNo(){
		return mTelephoneNo;
	}

	public String toString(){
		if(mLocation != null){
			return new String("Name:"+mName+",Location:"+mLocation.toString());
		}else{
			return new String("Name:"+mName+",Location:nodata");
		}
	}

	public String getSpecial() {
		return mSpecial;
	}

}

