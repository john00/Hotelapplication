package personal.john.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class MemoWindow extends Activity implements OnClickListener{
	// 画面オブジェクト
	private CheckBox mChkboxArrived;
	// DB用オブジェクト
	private GeoSearcherDB mDatabaseObject;
	// メイン画面情報
	private String mHotelID = "";
	private int mArrived = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);
        
		// DB作成
		mDatabaseObject = new GeoSearcherDB(this);
 
		// 画面オブジェクト作成
		mChkboxArrived = (CheckBox)findViewById(R.id.checkbox_id);
		Button btRegist = (Button)findViewById(R.id.bt_memo_regist);
		btRegist.setOnClickListener(this);
        
        Intent intent = getIntent();
        if(intent != null){
            String[] strCheck = intent.getStringArrayExtra("personal.john.app.Arrived");
            mHotelID = strCheck[0];
            mArrived = Integer.parseInt(strCheck[1]);
            		
            if (mArrived == 1) mChkboxArrived.setChecked(true);
        }
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.bt_memo_regist:
			int iArraived = 0;
			if (mChkboxArrived.isChecked()) iArraived = 1;
			
			mDatabaseObject.GeoSearcherDBOpen();
			mDatabaseObject.writeArrivedData(mHotelID, iArraived);
			mDatabaseObject.GeoSearcherDBClose();
			this.finish();
		}
	}
    
}

