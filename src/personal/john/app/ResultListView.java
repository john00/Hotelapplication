package personal.john.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ResultListView extends Activity implements OnClickListener{
	Button mReturnButton;
	ListView mListView;
	ArrayList<HotelInfo> mTargetList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.result_listview);
        
        mReturnButton = (Button)findViewById(R.id.bt_return);
        mReturnButton.setOnClickListener(this);
        mListView = (ListView)findViewById(R.id.listview);

        Intent intent = getIntent();
        if(intent != null){
        	ArrayList<String> strList = intent.getStringArrayListExtra("personal.john.app.list");
        	List<MyCustomListData> object = new ArrayList<MyCustomListData>();
			
			for (int iTargetCount = 0; iTargetCount < strList.size(); iTargetCount++) {
				MyCustomListData tmpItem = new MyCustomListData();
				tmpItem.setMessage(strList.get(iTargetCount));
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

	@Override
	public void onClick(View arg0) {
		// TODO 自動生成されたメソッド・スタブ
		finish();
	}

}
