package personal.john.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class MemoWindow extends Activity implements OnClickListener{
	// ��ʃI�u�W�F�N�g
	private CheckBox mChkboxArrived;
	// DB�p�I�u�W�F�N�g
	private GeoSearcherDB mDatabaseObject;
	// ���C����ʏ��
	private String mHotelID = "";
	private int mArrived = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo);
        
		// DB�쐬
		mDatabaseObject = new GeoSearcherDB(this);
 
		// ��ʃI�u�W�F�N�g�쐬
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

