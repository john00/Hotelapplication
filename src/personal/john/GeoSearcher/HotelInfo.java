package personal.john.GeoSearcher;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class HotelInfo {
	private Location mLocation = null;
	private String mNo = null;
	private String mName = null;
	private String mKanaName = null;
	private String mInfomationUrl = null;
	private String mPlanListUrl = null;
	private String mLatitude = null;
	private String mLongitude = null;
	private String mTelephoneNo = null;
	private String mPrShort = null;
	private String mSpecial = null;
	private String mAddress = null;	  /* add 0619 */


	public HotelInfo(){
		Location location = new Location("RakutenWebService");
		mLocation = location;
	}

	public HotelInfo(final Location location, final String name){
		mLocation = location;
		mName = name;
	}

	/* add 0619 */
	public void setNo(String no) {
		mNo = no;
	}

	public String getNo(){
		return mNo;
	}

	/* add 0619 */
	public void setName(String name) {
		mName = name;
	}

	public String getName(){
		return mName;
	}

	/* add 0619 */
	public void setKanaName(String kananame) {
		mKanaName = kananame;
	}

	public String getKanaName(){
		return mKanaName;
	}

/* add start 0619 */
	public void setLatitude(String latitude) {
		mLatitude = latitude;
	}

	public String getLatitude(){
		return mLatitude;
	}

	public void setLongitude(String longitude) {
		mLongitude = longitude;
	}

	public String getLongitude(){
		return mLongitude;
	}
/* add end 0619 */

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

	/* add 0619 */
	public void setInfomationUrl(String infomationurl) {
		mInfomationUrl = infomationurl;
	}

	public String getInfomationUrl(){
		return mInfomationUrl;
	}

	/* add 0619 */
	public void setPlanListUrl(String planlisturl) {
		mPlanListUrl = planlisturl;
	}

	public String getPlanListUrl(){
		return mPlanListUrl;
	}

	/* add 0619 */
	public void setTelephoneNo(String telephoneno) {
		mTelephoneNo = telephoneno;
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

	/* add 0619 */
	public void setSpecial(String special) {
		mSpecial = special;
	}

	public String getSpecial() {
		return mSpecial;
	}

	/* add start 0619 */
	public void setAddress(String address) {
		mAddress = address;
	}

	public String getAddress() {
		return mAddress;
	}
	/* add end 0619 */
}

