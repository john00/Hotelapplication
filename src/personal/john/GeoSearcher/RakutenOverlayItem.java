package personal.john.GeoSearcher;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class RakutenOverlayItem extends OverlayItem {

	HotelInfo mInfo = null;
	public RakutenOverlayItem(GeoPoint point, String title, String address, HotelInfo info) {
		super(point, title, address);
		mInfo = info;
	}

	public HotelInfo getInfo(){
		return mInfo;
	}
}
