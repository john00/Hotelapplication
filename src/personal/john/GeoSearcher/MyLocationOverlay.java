package personal.john.GeoSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay<OverlayItem> {

	Context mContext = null;
	String mGeoName = null;
	public GeoSearcherDB gsdb = null;

	private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();

	public MyLocationOverlay(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		Log.d("Test", "defaultMarker rect:"+defaultMarker.getBounds().toString());
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mItemList.get(i);
	}

	@Override
	public int size() {
		return mItemList.size();
	}

	public void addItem(OverlayItem overlayItem) {
		mItemList.add(overlayItem);
		populate();
	}

	public void clearItems(){
		mItemList.clear();
		populate();
	}

	@Override
	protected boolean onTap(int index){
		if (index < mItemList.size()){
			final OverlayItem item = mItemList.get(index);
			Geocoder geocoder = new Geocoder(mContext, Locale.JAPAN);
			double latitude = item.getPoint().getLatitudeE6()/1E6;
			double longitude = item.getPoint().getLongitudeE6()/1E6;
			StringBuffer addressString = new StringBuffer();
			try {
				List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
				if(!addressList.isEmpty()){
					Address address = addressList.get(0);
					String line = null;
					for (int i = 0; (line = address.getAddressLine(i)) != null; i++){
						addressString.append(line+"\n");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

//			String strCheckRes = null;
			mGeoName = addressString.toString();
/*			if(gsdb.checkGeoName(mGeoName)) {
				strCheckRes = "到達済みです。";
			} else {
				strCheckRes = "初到達です。";
			}
*/
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("現在地"/* + strCheckRes + ")"*/);
			dialog.setMessage(mGeoName);
			dialog.setNegativeButton("close", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			dialog.show();
			return true;
		}else{
			return false;
		}
	}

	public void setGeoSearcherDB(GeoSearcherDB gs) {
		gsdb = gs;
	}

//	private String getGeoName() {
//		return mGeoName;
//	}
}
