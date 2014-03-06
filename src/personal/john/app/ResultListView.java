package personal.john.app;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ResultListView extends Activity implements OnClickListener, RakutenClientReceiver{
	// 楽天クライアント
	private RakutenClient mRakutenClient = null;
	
	
	// 画面パーツ
	Button mReturnButton;
	ListView mListView;
	ArrayList<HotelInfo> mTargetList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.result_listview);
        
        try {
			mRakutenClient = new RakutenClient(this, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
        
        mReturnButton = (Button)findViewById(R.id.bt_return);
        mReturnButton.setOnClickListener(this);
        mListView = (ListView)findViewById(R.id.listview);

        Intent intent = getIntent();
        if(intent != null){
        	double[] myLocation = intent.getDoubleArrayExtra("personal.john.app.list");
			
        	mRakutenClient.setmMyLatitute(myLocation[0]);
        	mRakutenClient.setmMyLongitude(myLocation[1]);
        	mRakutenClient.queryInfo();
        	
        }
	}

	@Override
	public void onClick(View arg0) {
		finish();
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
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				finish();
			}
		});
		
	}
}
