package personal.john.GeoSearcher;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class PinOverlay extends ItemizedOverlay<RakutenOverlayItem> {

	Context mContext = null;

	private ArrayList<RakutenOverlayItem> mItemList = new ArrayList<RakutenOverlayItem>();

	public PinOverlay(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected RakutenOverlayItem createItem(int i) {
		return mItemList.get(i);
	}

	@Override
	public int size() {
		return mItemList.size();
	}

	public void addItem(RakutenOverlayItem item) {
		mItemList.add(item);
		populate();
	}

	public void clearItems(){
		mItemList.clear();
		populate();
	}

	protected boolean onTap(int index){
		if(index < mItemList.size()) {
			OverlayItem item = mItemList.get(index);
			final String strTelphoneNo = "tel:" + mItemList.get(index).mInfo.getTelephoneNo();
			final String strWebUrl = mItemList.get(index).mInfo.getInfomationUrl();
			final String strAddress = mItemList.get(index).mInfo.getAddress();
//			Dialog dialog = new Dialog(mContext);
//			dialog.setContentView(R.layout.hoteldialog);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());

//			TextView text = (TextView)dialog.findViewById(R.id.text_c);
//			text.setText(item.getSnippet());
//			ImageView image = (ImageView)dialog.findViewById(R.id.image_c);
			dialog.setMessage(item.getSnippet());
			dialog.setNegativeButton("close", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			dialog.setNeutralButton("Tel", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(strTelphoneNo));
					mContext.startActivity(intent);
				}
			});
			dialog.setPositiveButton("Web", new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strWebUrl));
						mContext.startActivity(intent);
					} catch (Exception e) {
						TextView textErr = new TextView(mContext);
						textErr.setText(e.getMessage());
						Dialog dialogErr = new Dialog(mContext);
						dialogErr.setTitle(e.getClass().getName());
						dialogErr.setContentView(textErr);
						dialogErr.show();
					}
				}
			});
			dialog.show();
			return true;
		} else {
			return false;
		}
	}
}
