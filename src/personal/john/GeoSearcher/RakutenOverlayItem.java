package personal.john.GeoSearcher;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class RakutenOverlayItem extends OverlayItem {

	HotelInfo mInfo = null;
	public RakutenOverlayItem(GeoPoint point, String title, String snippet, HotelInfo info) {
		super(point, title, snippet);
		mInfo = info;
	}

	public HotelInfo getInfo(){
		return mInfo;
	}
}
