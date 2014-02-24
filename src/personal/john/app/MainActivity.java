package personal.john.app;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.android.gms.internal.m;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements LocationListener, OnClickListener, OnInfoWindowClickListener, LocationSource, RakutenClientReceiver {
	
	private GoogleMap mMap;
	private OnLocationChangedListener mListener;
	private LocationManager mLocationManager;

	/* data */
	ArrayList<HotelInfo> mTargetList = null;
	RakutenClient mRakutenClient = null;
	ArrayList<DirectionsData> mDirectionsList = null;
	
	// DB用オブジェクト
	private GeoSearcherDB mDatabaseObject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
		if(mLocationManager != null) {
	    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		    	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 2.0f, this);
	    	}
	    	if(mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
		    	mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 2.0f, this);
	    	}
	    } else {
	    	Toast.makeText(this, "GPSを有効に設定してください。", Toast.LENGTH_SHORT).show();
	    }
		
		setupMapIfNeeded();
		
		// 初期位置を現在地に設定
        String provider = "";
    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		provider = LocationManager.GPS_PROVIDER;
    	} else {
    		provider = LocationManager.NETWORK_PROVIDER;
    	}

		Location loc = mLocationManager.getLastKnownLocation(provider);
		if (loc != null) {
			CameraUpdate iniCamera = CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().
					target(new LatLng(loc.getLatitude(), loc.getLongitude())).zoom(14.0f).build());
			mMap.moveCamera(iniCamera);
		}
		
		// ボタン作成
		Button btHotelSearch = (Button) findViewById(R.id.bt_hotel_search);
		btHotelSearch.setOnClickListener(this);
		Button btHotelSearchDetail = (Button) findViewById(R.id.bt_hotel_search_detail);
		btHotelSearchDetail.setOnClickListener(this);

		/* Rakuten Client */
		try {
			mRakutenClient = new RakutenClient(this);
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		// DB作成
		mDatabaseObject = new GeoSearcherDB(this);
		
		mDirectionsList = new ArrayList<DirectionsData>();
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setupMapIfNeeded();
		
		if(mLocationManager != null) {
			mMap.setMyLocationEnabled(true);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void setupMapIfNeeded() {
		if(mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMyLocationEnabled(true);
		}
		
		mMap.setLocationSource(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final String[] itemslist = {"100m", "500m", "1000m", "2000m", "3000m"};
		
		switch(item.getItemId()) {
		case R.id.item_range:
			// ホテルの検索範囲設定
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("検索範囲を選択してください")
			.setItems(itemslist, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					switch(which) {
					case 0:
						mRakutenClient.setSearchRange(0.1);
						break;
					case 1:
						mRakutenClient.setSearchRange(0.5);
						break;
					case 2:
						mRakutenClient.setSearchRange(1);
						break;
					case 3:
						mRakutenClient.setSearchRange(2);
						break;
					case 4:
						mRakutenClient.setSearchRange(3);
						break;
					default:
						mRakutenClient.setSearchRange(1);
					}
				}
			})
			.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int id) {
			        dialog.cancel();
			   }
			})
			.show();
			break;
		case R.id.item_exit:
			finish();
			break;
		}
		
		return true;
	}

	@Override
	public void onLocationChanged(Location location) {
        if (mListener != null) {
            mListener.onLocationChanged(location);
        }
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_hotel_search:		
			mMap.clear();
			// 現在地周辺のホテルを検索する。
			queryInfo();
			break;
		case R.id.bt_hotel_search_detail:
			// 絞り込み検索用の画面を表示する。
			break;
		default:
			break;
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void receiveHotel(ArrayList<HotelInfo> infoList) {
		if (mTargetList != null){
			mTargetList.clear();
		}
		mTargetList = infoList;
	}
	
	@Override
	public void receiveError(int id) {
		switch(id){
		case RakutenClient.ERROR_GENERAL:
			break;
		case RakutenClient.ERROR_FATAL:
			break;
		default:
			break;
		}
	}
	
	private void queryInfo(){
		RakutenClientExecuteThread rcExeThread = new RakutenClientExecuteThread(this);
		
		mRakutenClient.setmMyLatitute(mMap.getMyLocation().getLatitude());
		mRakutenClient.setmMyLongitude(mMap.getMyLocation().getLongitude());
		
		rcExeThread.execute(mRakutenClient);
		
	}

	public void updateMarker() {
		if (mTargetList == null){
			return;
		}
		
		int size = mTargetList.size();		
		mMap.clear();
		
		for(int iHotel = 0; iHotel < size; iHotel++) {
			int iArrived = 0;
			if (mTargetList.get(iHotel).getNo() != "") {
				mDatabaseObject.GeoSearcherDBOpen();
				iArrived = mDatabaseObject.readArrivedData(mTargetList.get(iHotel).getNo());
				mDatabaseObject.GeoSearcherDBClose();
			}
			if (iArrived != 1) {
				LatLng latlng = new LatLng(mTargetList.get(iHotel).getLocation().getLatitude(), mTargetList.get(iHotel).getLocation().getLongitude());
				String title = mTargetList.get(iHotel).getName();
				BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
				MarkerOptions options = new MarkerOptions();
				
				options.position(latlng).title(title).icon(icon).snippet(mTargetList.get(iHotel).getAddress());
				mMap.addMarker(options);
			}
		}
		mMap.setOnInfoWindowClickListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// ホテルの住所からmTargetListのインデックスを検索
		int index;
		for (index = 0; index < mTargetList.size(); index++) {
			if(marker.getSnippet().equals(mTargetList.get(index).getAddress())) break;
		}
		
		final int iTargetListIndex = index;
		final CharSequence[] items = {"電話で予約","ルート表示", "メモ", "閉じる"};
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(marker.getTitle());

		dialog.setItems(items, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:	// 電話
					String strTelphoneNo = "tel:" + mTargetList.get(iTargetListIndex).getTelephoneNo();
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(strTelphoneNo));
					startActivity(intent);
					break;
				case 1:	// ルート表示
					DirectionsData dd = mDirectionsList.get(iTargetListIndex);
					Toast.makeText(MainActivity.this, dd.getCopyright(), Toast.LENGTH_SHORT).show();  // テスト用
					break;
				case 2: // メモ
					String[] strInfo = {mTargetList.get(iTargetListIndex).getNo(),"0"};
					mDatabaseObject.GeoSearcherDBOpen();
					if (mDatabaseObject.readArrivedData(mTargetList.get(iTargetListIndex).getNo()) != 0) strInfo[1] = "1";
					mDatabaseObject.GeoSearcherDBClose();
					Intent intentToSettingWindow = new Intent();
					intentToSettingWindow.setClassName("personal.john.app", "personal.john.app.MemoWindow");
					intentToSettingWindow.putExtra("personal.john.app.Arrived", strInfo);
					
					startActivity(intentToSettingWindow);
					break;
				default:
				}
			}
        });

		dialog.show();
		
	}
	
}
