
package personal.john.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ResultListView extends Activity implements OnClickListener, RakutenClientReceiver {
    // 楽天クライアント
    private RakutenClient mRakutenClient = null;

    // DB用オブジェクト
    private GeoSearcherDB mDatabaseObject;

    // 画面パーツ
    Button mReturnButton;

    ListView mListView;

    ArrayList<HotelInfo> mTargetList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActionBar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.result_listview);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            mRakutenClient = new RakutenClient(this, this);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        mReturnButton = (Button) findViewById(R.id.bt_return);
        mReturnButton.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();
        if (intent != null) {
            double[] myLocation = intent.getDoubleArrayExtra("personal.john.app.list");

            mRakutenClient.setmMyLatitute(myLocation[0]);
            mRakutenClient.setmMyLongitude(myLocation[1]);
            mRakutenClient.queryInfo();

        }

        // DB作成
        mDatabaseObject = new GeoSearcherDB(this);
    }

    @Override
    public void onClick(View arg0) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] itemslist = {
                "ホテル名", "距離"
        };

        switch (item.getItemId()) {
            case R.id.listitem_sort:
                //
                new AlertDialog.Builder(ResultListView.this)
                        .setTitle(ResultListView.this.getString(R.string.menulistitem_sort))
                        .setItems(itemslist, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Collections.sort(mTargetList, new MyComparator(MyComparator.ASC, MyComparator.MODE_HOTELNAME));
                                        makeList();
                                        break;
                                    case 1:
                                        for (int listIndex = 0; listIndex < mTargetList.size(); listIndex++) {
                                            double destLat = Double.valueOf(mTargetList.get(
                                                    listIndex).getLatitude());
                                            double destLon = Double.valueOf(mTargetList.get(
                                                    listIndex).getLongitude());
                                            mTargetList.get(listIndex).setDistance(
                                                    mRakutenClient.getmMyLatitute(),
                                                    mRakutenClient.getmMyLongitude(), destLat,
                                                    destLon);
                                        }
                                        Collections.sort(mTargetList, new MyComparator(MyComparator.ASC, MyComparator.MODE_DISTANCE));
                                        makeList();
                                        break;
                                    default:
                                }
                            }
                        }).setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
                break;
            case R.id.item_exit:
                finish();
                break;
        }

        return true;
    }

    @Override
    public void receiveHotel(ArrayList<HotelInfo> infoList) {
        if (mTargetList != null) {
            mTargetList.clear();
        }
        mTargetList = infoList;
    }

    @Override
    public void receiveError(int id) {
        switch (id) {
            case RakutenClient.ERROR_GENERAL:
                break;
            case RakutenClient.ERROR_FATAL:
                break;
            default:
                break;
        }
    }

    // リスト項目生成メソッド
    public void makeList() {
        List<MyCustomListData> object = new ArrayList<MyCustomListData>();

        for (int iTargetCount = 0; iTargetCount < mTargetList.size(); iTargetCount++) {
            MyCustomListData tmpItem = new MyCustomListData();
            tmpItem.setMessage(mTargetList.get(iTargetCount).getName());
            object.add(tmpItem);
        }
        MyCustomListAdapter myCustomListAdapter = new MyCustomListAdapter(this, 0, object);
        mListView.setAdapter(myCustomListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                final int iTargetListIndex = arg2;
                final CharSequence[] items = {
                        "電話で予約", "ルート表示", "メモ", "楽天Webページを開く", "閉じる"
                };

                AlertDialog.Builder dialog = new AlertDialog.Builder(ResultListView.this);
                dialog.setTitle(mTargetList.get(iTargetListIndex).getName());

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 電話
                                String strTelphoneNo = "tel:"
                                        + mTargetList.get(iTargetListIndex).getTelephoneNo();
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri
                                        .parse(strTelphoneNo));
                                startActivity(intent);
                                break;
                            case 1: // ルート表示
                                String url = "http://maps.google.com/maps?dirflg=w";
                                url += "&saddr=" + mRakutenClient.getmMyLatitute() + ","
                                        + mRakutenClient.getmMyLongitude() + "(現在地)";
                                url += "&daddr=" + mTargetList.get(iTargetListIndex).getLatitude()
                                        + "," + mTargetList.get(iTargetListIndex).getLongitude()
                                        + "(目的地)";

                                Intent intentRote = new Intent();
                                intentRote.setAction(Intent.ACTION_VIEW);
                                intentRote.setClassName("com.google.android.apps.maps",
                                        "com.google.android.maps.MapsActivity");
                                intentRote.setData(Uri.parse(url));
                                startActivity(intentRote);
                                break;
                            case 2: // メモ
                                String[] strInfo = {
                                        mTargetList.get(iTargetListIndex).getNo(), "0", ""
                                };
                                String strHotelId = mTargetList.get(iTargetListIndex).getNo();
                                mDatabaseObject.openGeoSearcherDB();
                                if (mDatabaseObject.readArrivedData(strHotelId) != 0)
                                    strInfo[1] = "1";
                                strInfo[2] = mDatabaseObject.readMemoData(strHotelId);
                                mDatabaseObject.closeGeoSearcherDB();
                                Intent intentToSettingWindow = new Intent();
                                intentToSettingWindow.setClassName("personal.john.app",
                                        "personal.john.app.MemoWindow");
                                intentToSettingWindow
                                        .putExtra("personal.john.app.Arrived", strInfo);

                                startActivity(intentToSettingWindow);
                                break;
                            case 3: // 楽天Webページを開く
                                Intent intentWeb = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(mTargetList.get(iTargetListIndex)
                                                .getInfomationUrl()));
                                startActivity(intentWeb);
                            default:
                        }
                    }
                });

                dialog.show();
            }
        });

    }
}
