package personal.john.app;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import personal.john.app.R;

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
	}
	
	@Override
	protected void onStart() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();
		setupMapIfNeeded();
		
		if(mLocationManager != null) {
			mMap.setMyLocationEnabled(true);
			// マップ位置初期化（とりあえず東京駅）
		}
	}
	
	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
		
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
		
	}

	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
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
		// TODO 自動生成されたメソッド・スタブ
        if (mListener != null) {
            mListener.onLocationChanged(location);
        }
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		switch(v.getId()) {
		case R.id.bt_hotel_search:
			// 現在地周辺のホテルを検索する。
			// Toast.makeText(this, "GPSを有効に設定してください。", Toast.LENGTH_SHORT).show();  // テスト用
			
			queryInfo();
			updateMarker();
			if (0 == mRakutenClient.getRecordCount()) {
				new AlertDialog.Builder(this)
				.setTitle("検索結果なし")
				.setMessage("近場にホテルがありません。検索範囲を広げるか、移動して再度検索を行ってください。")
				.setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
				        dialog.cancel();
				   }
				})
				.show();
			}
			break;
		case R.id.bt_hotel_search_detail:
			// 絞り込み検索用の画面を表示する。
			mMap.clear();
			break;
		default:
			break;
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO 自動生成されたメソッド・スタブ
		mListener = listener;
	}

	@Override
	public void deactivate() {
		// TODO 自動生成されたメソッド・スタブ
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
		double latitude;
		double longitude;
		double range;

		latitude = mMap.getMyLocation().getLatitude();
		longitude = mMap.getMyLocation().getLongitude();
		range = mRakutenClient.getSearchRange();
		try {
			mRakutenClient.requestHotel(latitude, longitude, range);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateMarker() {
		if (mTargetList == null){
			return;
		}
		
		int size = mTargetList.size();		
		mMap.clear();
		mMap.setOnInfoWindowClickListener(this);
		
		for(int iHotel = 0; iHotel < size; iHotel++) {
			LatLng latlng = new LatLng(mTargetList.get(iHotel).getLocation().getLatitude(), mTargetList.get(iHotel).getLocation().getLongitude());
			String title = mTargetList.get(iHotel).getName();
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
//			BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
			MarkerOptions options = new MarkerOptions();
			
			options.position(latlng).title(title).icon(icon).snippet(mTargetList.get(iHotel).getAddress());
			mMap.addMarker(options);
		}
		
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// TODO 自動生成されたメソッド・スタブ
		final int iTargetListIndex  = Integer.parseInt(marker.getId().substring(1));
//		Toast.makeText(this, mTargetList.get(Integer.parseInt(str)).getAddress(), Toast.LENGTH_SHORT).show();  // テスト用
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(marker.getTitle());

		dialog.setMessage(marker.getSnippet());
		dialog.setNegativeButton("閉じる", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		dialog.setNeutralButton("Tel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				String strTelphoneNo = "tel:" + mTargetList.get(iTargetListIndex).getTelephoneNo();
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(strTelphoneNo));
				startActivity(intent);
			}
		});
		dialog.setPositiveButton("Web", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				try {
					String strWebUrl = mTargetList.get(iTargetListIndex).getInfomationUrl();
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strWebUrl));
					startActivity(intent);
				} catch (Exception e) {
/*					TextView textErr = new TextView(this);
					textErr.setText(e.getMessage());
					Dialog dialogErr = new Dialog(mContext);
					dialogErr.setTitle(e.getClass().getName());
					dialogErr.setContentView(textErr);
					dialogErr.show();
*/				}
			}
		});
		dialog.show();
		
	}
	
}
