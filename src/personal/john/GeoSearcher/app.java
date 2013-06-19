/*package com.john.app.com.john;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.john.app.R;

public class app extends MapActivity implements OnMenuItemClickListener {

	// ログ用のタグ
	static final String TAG ="app";

	// GPSへの問い合わせ周期
	static final long MIN_TIME = 0;
	static final float MIN_METER = 0;

	// ダイアログ
	private ProgressDialog mDialog;

	// リソース
	private Resources mRes;
	private Activity me;	//デバッグ用

	// このアプリはマップ以下の機能を持っている
    private ItemsMapView mMapView;
    private LocationManager lmLocMg;
    private MyLocationListener mLocationListener;
    private StationFinder mStationFinder;

	/**
	 * Web APIへのアクセスが完了すると呼び出される
//	 /
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg){
			GeoPoint gp = (GeoPoint)msg.obj;
			displayStations(gp);
			if (mDialog != null) {
				mDialog.dismiss();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
        setContentView(R.layout.main);

		// マップを取得
		mMapView = (ItemsMapView)findViewById(R.id.mainmap);
		mMapView.setEnabled(true);
		mMapView.setClickable(true);
		mMapView.setBuiltInZoomControls(true);
		mMapView.invalidate();


		// GPSイベントリスナーとマネージャ
		mLocationListener = new MyLocationListener();
		lmLocMg = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		lmLocMg.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_METER, mLocationListener);

		// リソース
		mRes = getResources();

		// 駅探索オブジェクト
		mStationFinder = new StationFinder();
	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");

		// ロケーションマネージャからリスナーを削除する
		lmLocMg.removeUpdates(mLocationListener);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		// メニューの属性
		int groupId = 0;
		int itemId = 0;
		int order = 0;
		MenuItem item1 = menu.add(groupId, itemId, order, "GetPoint");	// 現在地取得
		MenuItem item2 = menu.add(groupId, 1, order, "Exit");		// 終了

		item1.setOnMenuItemClickListener(this);
		item2.setOnMenuItemClickListener(this);
		return ret;
	}

	public boolean onMenuItemClick(MenuItem item) {
		if(item.getItemId() == 0) {
			// 現在地を取得する
			GeoPoint gp = mLocationListener.getGeoPoint();

			if (gp == null) {
				// 現在地が取得できない場合はメッセージを表示して終わり
				Toast.makeText(app.this, "現在地が取得できません", Toast.LENGTH_SHORT).show();
				return false;
			}

			String title = mRes.getString(R.string.load_dialog_title);
			String msg = mRes.getString(R.string.load_dialog_title);
			mDialog = ProgressDialog.show(this, title, msg, true, true);

			// 現在地が取得できれば近くの駅を探して表示する
			// 今表示中の駅をクリア
			mMapView.clearStations();

			// Web APIへアクセス
			mStationFinder.setGPoint(gp);
			new Thread(new RequestRunnable(mStationFinder, mHandler)).start();
			Log.i(TAG, "test");

		} else {
			finish();
		}
		return false;
	}

	/**
	 * Web APIから取得した周辺情報を表示する
	 *
	 * @param gp
//	 /
	private void displayStations(GeoPoint gp) {
		if (gp == null) {
			return;
		}

		// 全駅が入る大きさに拡大するときに使う
		int maxLat = gp.getLatitudeE6();
		int maxLng = gp.getLongitudeE6();
		int minLat = maxLat;
		int minLng = maxLng;

		// 現在地と駅の中心を求め、そこに移動するときに使う
		int midLat = maxLat;
		int midLng = maxLng;

		int size = mStationFinder.size();
		int counter = 0;
		for (int i = 0; i < size; ++i) {
			try {
				// 周辺駅情報を取得
				JSONObject station = mStationFinder.getStation(i);
				GeoPoint stationGP = mStationFinder.getStationLocation(station);
				String stationName = mStationFinder.getStationName(station);
				double distance = mStationFinder.getStationDistance(station);
				String stationDistance = Math.round(distance) + "m";

				// 駅をマップに配置
				mMapView.addStation(stationGP, stationName, stationDistance);

				// 移動と拡大のためのデータ計算
				int lat = stationGP.getLatitudeE6();
				int lng = stationGP.getLongitudeE6();
				midLat += lat;
				midLng += lng;
				maxLat = maxLat < lat ? lat : maxLat;
				minLat = lat < minLat ? lat : minLat;
				maxLng = maxLng < lng ? lng : maxLng;
				minLng = lng < minLng ? lng : minLng;

				// 例外なく処理できた数
				counter++;
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}

		if(0 < counter) {
			// 駅が1つ以上あれば、自分の位置と駅の中間地点へ移動
			mMapView.moveGPoint(new GeoPoint(midLat / (counter + 1), midLng / (counter + 1)));
			// 自分と駅が入る大きさに地図を拡大する
			mMapView.spanMap(maxLat - minLat, maxLng - minLng);
		}
	}

	/**
	 * LBSのイベントリスナー
//	 /
	private class MyLocationListener implements LocationListener {

		// 現在地から周辺駅を検索するために保持しておく
		private GeoPoint currentLocation;

		private MyLocationListener() {
			Log.i(TAG, "constructor MyLocationListener");
		}

		/**
		 * @return nullが返る可能性あり
//		 /
		GeoPoint getGeoPoint() {
			return currentLocation;
		}

		// 場所が変化したら呼び出される
		// 現在地にアイコンを配置し、移動する
		// 現在地をフィールドに格納しておくのは近隣駅を探すときに使うため
		public void onLocationChanged(Location loc) {
			Log.i(TAG, "onLocationChanged");

			int lat = (int)(loc.getLatitude() * 1E6);
			int lng = (int)(loc.getLongitude() * 1E6);
			currentLocation = new GeoPoint(lat, lng);
			mMapView.setCurrentLocation(currentLocation);
		}

		public void onProviderDisabled(String provider) {
			Log.i(TAG, "onProviderDisabled");
		}

		public void onProviderEnabled(String provider) {
			Log.i(TAG, "onProviderEnabled");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "onStatusChanged");
		}
	}
}
*/

package personal.john.GeoSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import personal.john.app.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.MenuItem.OnMenuItemClickListener;


public class app extends MapActivity implements LocationListener, OnClickListener, RakutenClientReceiver {
	static final String TAG = "app";

//    private LocationManager lmLocMg;
	private MapController mcCtrl;
//	private Geocoder mGeocoder;
//	private MapView mvMap;
	GeoPoint gpCorrentPoint;
//    private StationFinder mStationFinder = new StationFinder();

	// ダイアログ
	private ProgressDialog mDialog;

	static final long MIN_TIME = 0;
	static final float MIN_DISTANCE = 0;
	static final int INITIAL_ZOOM_LEVEL = 15;

//	private Activity me;	//デバッグ用

	/* Resource */
	Drawable red_pin = null;
	Drawable blue_pin = null;
//	private static final int MAP_NORMAL = 0;
	double mRange = 1;

	/* Layout */
	MapView mMapView = null;
	PinOverlay mPinOverlay = null;
	MyLocationOverlay mMyLocationOverlay = null;


	/* Service */
	LocationManager mLocationManager = null;
	RakutenClient mRakutenClient = null;
	public static final int ZOOMLEVEL_THRESHOLD = 15;
//	private final int X = 0;
//	private final int Y = 1;
//	private final int Z = 2;
//	private final int AZIMUTH 	= 0;
//	private final int PITCH 	= 1;
//	private final int ROLL 		= 2;

	/* data */
	ArrayList<HotelInfo> mTargetList = null;
//	private int mCurrentCategory = RakutenClient.CATEGORY_LARGE;
//	private int mCurrentCategoryIndex = 0;
	private Location mMyLocation = null;

	/* status */
//	boolean mIsCamera = false;
//	private int mMapMode = MAP_NORMAL;

	/* DB */
//	static final String DB_NAME = "GeoSearcherDB.db";
//	static final String DB_TABLE = "GeoTable";
//	static final String DB_GEONAME = "GeoName";
//	static final int DB_VERSION = 1;
//	static final String CREATE_TABLE = "create table " + DB_TABLE +
//										"(" + DB_GEONAME + " text primary key)";
//	private SQLiteDatabase mDb;
//	private SimpleCursorAdapter mAdapter;
	GeoSearcherDB gsdb = new GeoSearcherDB(this);

	/**
	 * Web APIへのアクセスが完了すると呼び出される
	 */
//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg){
//			GeoPoint gp = (GeoPoint)msg.obj;
//			displayStations(gp);
//			if (mDialog != null) {
//				mDialog.dismiss();
//			}
//		}
//	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mMapView = (MapView)findViewById(R.id.mainmap);
        mMapView.setBuiltInZoomControls(true);

		// 位置とズームレベルの初期状態を設定する
        mcCtrl = mMapView.getController();
        mcCtrl.setZoom(INITIAL_ZOOM_LEVEL);

		// 地名の検索用クラス
//        mGeocoder = new Geocoder(getApplicationContext());

        //デバッグ用
//        me = this;

//        List<Overlay> listOvls = mMapView.getOverlays();

		// オーバーレイの生成
        Rect defaultRect = new Rect(-18, -36, 18, 0);
		red_pin = this.getResources().getDrawable(R.drawable.pin_red);
		red_pin.setBounds(defaultRect);
		mPinOverlay = new PinOverlay(this, red_pin);

		// 端末位置オーバーレイの生成
		blue_pin = this.getResources().getDrawable(R.drawable.pin_blue);
		blue_pin.setBounds(defaultRect);
		mMyLocationOverlay = new MyLocationOverlay(this, blue_pin);

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

//        pinoverlay.addItem(overlayItem0);
//        pinoverlay.addItem(overlayItem1);
//        pinoverlay.addItem(overlayItem2);

//        listOvls.add(pinoverlay);

        gpCorrentPoint = new GeoPoint(35681099,139767084);
//        mcCtrl.animateTo(gpCorrentPoint);
    	mDialog = new ProgressDialog(mMapView.getContext());

    }

    @Override
    protected void onStart() {
    	super.onStart();
    	mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = "";
    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		provider = LocationManager.GPS_PROVIDER;
    	} else {
    		provider = LocationManager.NETWORK_PROVIDER;
    	}

    	mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);
    }

	@Override
	protected void onResume() {
		super.onResume();
        String provider = "";
    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		provider = LocationManager.GPS_PROVIDER;
    	} else {
    		provider = LocationManager.NETWORK_PROVIDER;
    	}

		// 最後に取得した位置があれば表示する
		Location loc = mLocationManager.getLastKnownLocation(provider);
		if (loc != null) {
			onLocationChanged(loc);
		}

		mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this);

		gsdb.GeoSearcherDBOpen();
		mMyLocationOverlay.setGeoSearcherDB(gsdb);

		mcCtrl.animateTo(gpCorrentPoint);
		updateMyLocationOverlay();
		updatePinOverlay();

	}

    @Override
    protected void onPause() {
    	super.onPause();
    	if (mLocationManager != null) {
    		mLocationManager.removeUpdates(this);
    	}
    	gsdb.GeoSearcherDBClose();
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	if (mLocationManager != null) {
    		mLocationManager.removeUpdates(this);
    	}
    }

	public void onDestroy(){
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if (mTargetList != null){
			mTargetList.clear();
			mTargetList = null;
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO 自動生成されたメソッド・スタブ
		return false;

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = super.onCreateOptionsMenu(menu);
		MenuItem item1 = menu.add(1, 1, 1, "ホテル検索");	// 宿泊施設検索
		MenuItem item2 = menu.add(1, 2, 0, "検索範囲");  // 検索範囲設定
		MenuItem item3 = menu.add(1, 0, 2, "終了");		// 終了
		final String[] itemslist = {"100m", "500m", "1000m", "2000m", "3000m"};
		OnMenuItemClickListener listener1 = new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem item){
				if(item.getItemId() == 1) {
					// デバッグ用
			        String provider = "";
			    	if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			    		provider = LocationManager.GPS_PROVIDER;
			    	} else {
			    		provider = LocationManager.NETWORK_PROVIDER;
			    	}
	//				Location location = mLocationManager.getLastKnownLocation(provider);
					mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, app.this);

					String title = getResources().getString(R.string.load_dialog_title);
					String msg = getResources().getString(R.string.load_dialog_title);
					mDialog = ProgressDialog.show(app.this, title, msg, true, true);

					queryInfo();
					mDialog.dismiss();
					updatePinOverlay();
					if (mMyLocation != null){
						MapController mc = mMapView.getController();
						mc.animateTo(locationToGeoPoint(mMyLocation));
					}

					if (0 == mRakutenClient.getRecordCount()) {
						new AlertDialog.Builder(app.this)
						.setTitle("ヒットなし")
						.setMessage("近くにホテルがありません。検索範囲を広げるか、移動して再度検索してください。")
						.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int id) {
						        dialog.cancel();
						   }
						})
						.show();

					}
	//				Toast toast = Toast.makeText(me, getLocationName(location), Toast.LENGTH_LONG);
	//				toast.show();
				}

				if(item.getItemId() == 2) {
					new AlertDialog.Builder(app.this)
					.setTitle("検索範囲を選択してください")
					.setItems(itemslist, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							switch(which) {
							case 0:
								mRange = 0.1;
								break;
							case 1:
								mRange = 0.5;
								break;
							case 2:
								mRange = 1;
								break;
							case 3:
								mRange = 2;
								break;
							case 4:
								mRange = 3;
								break;
							default:
								mRange = 1;
							}
						}
					})
					.setNegativeButton("閉じる", new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int id) {
					        dialog.cancel();
					   }
					})
					.show();

				}

				if(item.getItemId() == 0) {
					finish();
				}

					return false;
			}
		};

		item1.setOnMenuItemClickListener(listener1);
		item2.setOnMenuItemClickListener(listener1);
		item3.setOnMenuItemClickListener(listener1);
		return ret;
	}

	public void onLocationChanged(Location loc) {
		// LocationをGeoPointに変更して地図の表示を変更する
		gpCorrentPoint = new GeoPoint((int)(loc.getLatitude()*1E6),
								   		(int)(loc.getLongitude()*1E6));

		mMyLocation = loc;
		updateMyLocationOverlay();

   		mLocationManager.removeUpdates(this);
	}


	public void onProviderDisabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onProviderEnabled(String arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO 自動生成されたメソッド・スタブ

	}

	private void updatePinOverlay(){
		if (mTargetList == null){
			return;
		}
		int size = mTargetList.size();
		GeoPoint geopoint = null;
		RakutenOverlayItem overlayItem = null;
		// オーバレイリストのクリア
        List<Overlay> mapOverlays = mMapView.getOverlays();
        mapOverlays.remove(mPinOverlay);
		mPinOverlay.clearItems();
		// オーバレイリストの取得
		for (int i = 0; i < size; i++){
			Location location = mTargetList.get(i).getLocation();
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
	        geopoint = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
	        overlayItem = new RakutenOverlayItem(geopoint,
	        		mTargetList.get(i).getName(),
	        		mTargetList.get(i).getAddress() /*mTargetList.get(i).mPrShort*/,
	        		mTargetList.get(i));
	        // オーバーレイへItemを追加
	        mPinOverlay.addItem(overlayItem);
		}
        // オーバーレイリストへピンオーバーレイを追加
        mapOverlays.add(mPinOverlay);
//		mMapLayout.invalidate();
	}

	private void updateMyLocationOverlay(){
		if (mMyLocation != null){
			// オーバレイリストのクリア
	        List<Overlay> mapOverlays = mMapView.getOverlays();
	        mapOverlays.remove(mMyLocationOverlay);
	        mMyLocationOverlay.clearItems();

			double latitude = mMyLocation.getLatitude();
			double longitude = mMyLocation.getLongitude();
			GeoPoint geoPoint = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
			OverlayItem overlayItem = new OverlayItem(geoPoint,
	        		"my location",
	        		"address");
	        // オーバーレイへItemを追加
			mMyLocationOverlay.addItem(overlayItem);
	        // オーバーレイリストへピンオーバーレイを追加
	        mapOverlays.add(mMyLocationOverlay);
//			mMapLayout.invalidate();
		}
	}

	public void onClick(DialogInterface arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

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

	public void receiveHotel(ArrayList<HotelInfo> infoList) {
		if (mTargetList != null){
			mTargetList.clear();
		}
		mTargetList = infoList;
	}

	private void queryInfo(){
		double latitude;
		double longitude;
		double range;

		latitude = gpCorrentPoint.getLatitudeE6()/1E6;
		longitude = gpCorrentPoint.getLongitudeE6()/1E6;
		int zoomLevel = mMapView.getZoomLevel();
		if(zoomLevel >= ZOOMLEVEL_THRESHOLD){
			range = mRange;
		}else{
			return;
		}
		try {
			mRakutenClient.requestHotel(latitude, longitude, range);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private GeoPoint locationToGeoPoint(Location location){
    	GeoPoint geoPoint = null;
    	if (location != null){
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			geoPoint = new GeoPoint((int)(latitude * 1E6), (int)(longitude * 1E6));
    	}
		return geoPoint;
    }

}
